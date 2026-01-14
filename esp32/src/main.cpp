#include <OneWire.h>
#include <DallasTemperature.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <Preferences.h>
#include "config.h"  // File cấu hình WiFi và MQTT
#include "WifiConfig.h"  // WiFi Config Portal

// ---------- Sử dụng cấu hình từ file config.h ----------
String mqttTopic = String(MQTT_TOPIC_FIRE);
String mqttDeviceTopic = String(MQTT_TOPIC_DEVICE);
String mqttConfigTopic = String(MQTT_TOPIC_CONFIG);
String mqttThresholdTopic = String(MQTT_TOPIC_THRESHOLD);
String mqttStatusTopic = String(MQTT_TOPIC_STATUS);

WiFiClientSecure espClient;
PubSubClient client(espClient);

// ------ Cấu hình cảm biến ------
// DS18B20
#define ONE_WIRE_BUS 4
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

// Cảm biến khác
#define MQ2_PIN 34
#define CO_PIN 35
#define FLAME_PIN 5
#define BUZZER_PIN 23

String deviceID = WiFi.macAddress();

String topicTemp = mqttThresholdTopic + deviceID + "/TEMP";
String topicMQ2 = mqttThresholdTopic + deviceID + "/MQ2";
String topicCO = mqttThresholdTopic + deviceID + "/CO";
String topicFlame = mqttThresholdTopic + deviceID  + "/FLAME";

// Biến toàn cục để chia sẻ giữa các task
float temperature;
int mq2_val, co_val, flame_val;
bool alert = false;
int temp_threshold = 60;
int mq2_threshold = 10000;
int co_threshold = 10000;
bool en_temp = true;
bool en_mq2 = true;
bool en_co = true;
bool en_flame = true;
bool deviceActive = true; // ACTIVE hoặc INACTIVE
bool wifiConnected = false; // Trạng thái kết nối WiFi
bool mqttConnected = false; // Trạng thái kết nối MQTT
// ---------- Kết nối WiFi ----------
void setupWiFi()
{
  Preferences preferences;
  // Mở Preferences để kiểm tra cấu hình đã lưu
  preferences.begin("wifi_config", true); // true = chỉ đọc
  saved_ssid = preferences.getString("ssid", "");
  saved_password = preferences.getString("pass", "");
  preferences.end();
  
  // Nếu không có cấu hình đã lưu, thử dùng config.h
  if (saved_ssid.length() == 0) {
    Serial.println("Không tìm thấy cấu hình WiFi đã lưu.");
    Serial.println("Khởi động WiFi Config Portal...");
    Serial.println("Kết nối đến: ESP32-CONFIG-AP (Password: 12345678)");
    Serial.println("Truy cập: http://192.168.4.1");
    
    startConfigPortal();
    
    // Sau khi cấu hình xong, sẽ tự động restart
    // Code dưới đây sẽ không chạy nếu ở chế độ Config Portal
    return;
  }
  
  // Kết nối WiFi với thông tin đã lưu
  Serial.print("Connecting to WiFi: ");
  Serial.println(saved_ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(saved_ssid.c_str(), saved_password.c_str());
  
  int timeout = 20; // 20 giây timeout
  while (WiFi.status() != WL_CONNECTED && timeout > 0)
  {
    delay(500);
    Serial.print(".");
    timeout--;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi connected!");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("\nKết nối thất bại! Khởi động Config Portal...");
    startConfigPortal();
  }
}

// ---------- Xóa cấu hình WiFi đã lưu ----------
void clearWiFiConfig() {
  Preferences preferences;
  preferences.begin("wifi_config", false); // false = read/write mode
  preferences.clear(); // Xóa tất cả dữ liệu trong namespace
  preferences.end();
  Serial.println("WiFi configuration cleared!");
}
// ---------- Forward declarations ----------
void parseThresholdMessage(String message);
void parseStatusMessage(String message);
void parseConfigMessage(String message);

// ---------- Callback MQTT ----------
void callback(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  String message = "";
  for (unsigned int i = 0; i < length; i++)
  {
    message += (char)payload[i];
  }
  Serial.println(message);

  String topicStr = String(topic);
  
  // Xử lý threshold topic
  if (topicStr.indexOf("threshold") >= 0) {
    Serial.println("Processing threshold update...");
    parseThresholdMessage(message);
    return;
  }
  
  // Xử lý status topic
  if (topicStr.indexOf("status") >= 0) {
    Serial.println("Processing status update...");
    parseStatusMessage(message);
    return;
  }
  
  // Xử lý config topic (code cũ)
  if (topicStr.indexOf("config") >= 0) {
    Serial.println("Processing config update...");
    parseConfigMessage(message);
    return;
  }
}


// ---------- Parse Threshold Message ----------
void parseThresholdMessage(String message) {
  // Format mới: {"sensorType": "FLAME", "threshold": 500}
  
  // Lấy threshold value
  int thresholdValue = 0;
  if (message.indexOf("threshold") >= 0) {
    int start = message.indexOf(":", message.indexOf("threshold")) + 1;
    int end = message.indexOf(",", start);
    if (end == -1) end = message.indexOf("}", start);
    String valueStr = message.substring(start, end);
    valueStr.trim();
    thresholdValue = valueStr.toInt();
  }
  
  // Xác định sensor type và cập nhật threshold tương ứng
  if (message.indexOf("TEMP") >= 0 || message.indexOf("TEMP") >= 0) {
    temp_threshold = thresholdValue;
    Serial.println("Updated Temp Threshold: " + String(temp_threshold));
  }
  else if (message.indexOf("MQ2") >= 0) {
    mq2_threshold = thresholdValue;
    Serial.println("Updated MQ2 Threshold: " + String(mq2_threshold));
  }
  else if (message.indexOf("CO") >= 0) {
    co_threshold = thresholdValue;
    Serial.println("Updated CO Threshold: " + String(co_threshold));
  }
  else if (message.indexOf("FLAME") >= 0) {
    // FLAME sensor không dùng threshold (digital sensor)
    Serial.println("FLAME sensor does not use threshold (digital sensor)");
  }
  
}

// ---------- Parse Status Message ----------
void parseStatusMessage(String message) {
  // Format: {"value":"ACTIVE"} hoặc {"value":"INACTIVE"}
  if (message.indexOf("value") >= 0) {
    // Kiểm tra INACTIVE trước (vì INACTIVE chứa chuỗi ACTIVE)
    if (message.indexOf("INACTIVE") >= 0) {
      deviceActive = false;
      en_temp = false;
      en_mq2 = false;
      en_co = false;
      en_flame = false;
      Serial.println("Device Status: INACTIVE - All sensors OFF");
      return;
    }
    else if (message.indexOf("ACTIVE") >= 0) {
      deviceActive = true;
      en_temp = true;
      en_mq2 = true;
      en_co = true;
      en_flame = true;
      Serial.println("Device Status: ACTIVE - All sensors ON");
      return;
    }
  }
 
}

// ---------- Parse Config Message ----------
void parseConfigMessage(String message) {
  // Kết hợp cả threshold và status (backward compatibility)
  parseThresholdMessage(message);
  parseStatusMessage(message);
}

// ---------- Kết nối MQTT ----------
void setupMQTT()
{
  espClient.setCACert(CA_CERTIFICATE);
  espClient.setTimeout(3000); // Socket timeout 3 giây
  client.setServer(MQTT_SERVER, MQTT_PORT);
  client.setCallback(callback);
  
  // Giảm Keep-Alive xuống 3 giây để phát hiện offline nhanh nhất (4-5s)
  client.setKeepAlive(3);
  
  // Tăng buffer size để xử lý message nhanh hơn
  client.setBufferSize(512);
  
  // Chuẩn bị Last Will and Testament (LWT) message với QoS 1
  String lwt_topic = "iot/device/status/" + deviceID;
  String lwt_message = "{\"device\":\"" + deviceID + "\",\"value\":\"INACTIVE\"}";
  
  while (!client.connected())
  {
    Serial.println("Connecting to MQTT...");
    
    // Kết nối với LWT: QoS 1 để đảm bảo LWT được gửi khi offline
    if (client.connect(deviceID.c_str(), MQTT_USER, MQTT_PASSWORD, 
                       lwt_topic.c_str(), 1, true, lwt_message.c_str()))
    {
      Serial.println("connected");
      mqttConnected = true;
      
      // Gửi message ONLINE ngay khi kết nối thành công
      String online_message = "{\"device\":\"" + deviceID + "\",\"value\":\"ACTIVE\"}";
      client.publish(lwt_topic.c_str(), online_message.c_str(), true);
      Serial.println("Published ACTIVE status");
      
      // Subscribe to all topics
      client.subscribe(mqttConfigTopic.c_str());
      Serial.println("Subscribed to: " + mqttConfigTopic);
      
      client.subscribe(topicTemp.c_str());
      Serial.println("Subscribed to: " + topicTemp);
      client.subscribe(topicMQ2.c_str());
      Serial.println("Subscribed to: " + topicMQ2);
      client.subscribe(topicCO.c_str());
      Serial.println("Subscribed to: " + topicCO);
      client.subscribe(topicFlame.c_str());
      Serial.println("Subscribed to: " + topicFlame);
      
      client.subscribe(mqttStatusTopic.c_str());
      Serial.println("Subscribed to: " + mqttStatusTopic);
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.println(client.state());
      mqttConnected = false;
      delay(2000);
    }
  }
}

// --- Task đọc cảm biến ---
void sensorTask(void *parameter)
{
  for (;;)
  {
    if (en_temp) { sensors.requestTemperatures(); temperature = sensors.getTempCByIndex(0); }
    if (en_mq2) mq2_val = analogRead(MQ2_PIN);
    if (en_co) co_val = analogRead(CO_PIN);
    if (en_flame) flame_val = digitalRead(FLAME_PIN);

    flame_val = 1 - flame_val; // Đảo ngược giá trị (0: không có lửa, 1: có lửa)

    // Kiểm tra ngưỡng
    alert = ( (en_temp && temperature > temp_threshold) || 
              (en_mq2 && mq2_val > mq2_threshold) || 
              (en_co && co_val > co_threshold) || 
              (en_flame && flame_val == 1) );
    
    // Gửi dữ liệu lên MQTT (chỉ khi device ACTIVE)
    if (client.connected() && deviceActive)
    {
      String basePayload = "{\"sensorType\":\"";
      Serial.print("\nDevice: " + deviceID);
      Serial.print(" - Wifi: " + saved_ssid);
      Serial.print("\nPublishing data - ");
      if (en_temp) {
        String msg = basePayload + "TEMP\",\"value\":" + String(temperature) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());

        Serial.print("Temp: ");
        Serial.print(temperature);
        Serial.print(" | Threshold: ");
        Serial.print(temp_threshold);
      }
      if (en_mq2) {
        String msg = basePayload + "MQ2\",\"value\":" + String(mq2_val) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());

        Serial.print(" | MQ2: ");
        Serial.print(mq2_val);
        Serial.print(" | Threshold: ");
        Serial.print(mq2_threshold);
      }

      if (en_co) {
        String msg = basePayload + "CO\",\"value\":" + String(co_val) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());

        Serial.print(" | CO: ");
        Serial.print(co_val);
        Serial.print(" | Threshold: ");
        Serial.print(co_threshold);

      }

      if (en_flame) {
        String msg = basePayload + "FLAME\",\"value\":" + String(flame_val) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());

        Serial.print(" | Flame: ");
        Serial.print(flame_val);
        Serial.println();
      }
      // Serial.println("Published sensor data separately");
    }

    vTaskDelay(2000 / portTICK_PERIOD_MS); // delay 2s
  }
}

// --- Task cảnh báo (buzzer nháy nhịp) ---
void alertTask(void *parameter)
{
  for (;;)
  {
    if (alert)
    {
      // for(int i=0;i<3;i++){
      digitalWrite(BUZZER_PIN, HIGH);
      vTaskDelay(200 / portTICK_PERIOD_MS);
      digitalWrite(BUZZER_PIN, LOW);
      vTaskDelay(200 / portTICK_PERIOD_MS);
      // }
    }
    else
    {
      digitalWrite(BUZZER_PIN, LOW);
    }
    // vTaskDelay(500 / portTICK_PERIOD_MS);
  }
}

// --- Task giám sát kết nối WiFi ---
void wifiMonitorTask(void *parameter)
{
  for (;;)
  {
    // Kiểm tra trạng thái WiFi
    if (WiFi.status() == WL_CONNECTED) {
      if (!wifiConnected) {
        wifiConnected = true;
        Serial.println("[WiFi] Connected - IP: " + WiFi.localIP().toString());
        
        // Kết nối lại MQTT khi WiFi vừa khôi phục
        if (!client.connected()) {
          Serial.println("[WiFi] Reconnecting to MQTT...");

          setupMQTT();
        }
      }
    } else {
      if (wifiConnected) {
        wifiConnected = false;
        Serial.println("[WiFi] Disconnected! Attempting to reconnect...");
      }
      
      // Thử kết nối lại WiFi
      WiFi.disconnect();
      delay(100);
      WiFi.begin(saved_ssid.c_str(), saved_password.c_str());
      
      // Chờ kết nối trong 10 giây
      int timeout = 20;
      while (WiFi.status() != WL_CONNECTED && timeout > 0) {
        delay(500);
        Serial.print(".");
        timeout--;
      }
      
      if (WiFi.status() == WL_CONNECTED) {
        Serial.println("\n[WiFi] Reconnected successfully!");
      } else {
        Serial.println("\n[WiFi] Reconnection failed. Will retry...");
      }
    }
    
    // Kiểm tra mỗi 1 giây
    vTaskDelay(1000 / portTICK_PERIOD_MS);
  }
}

void setup()
{
  Serial.begin(115200);
  delay(1000);
  Serial.println("\n=== ESP32 Fire Warning System ===");
  
  deviceID = WiFi.macAddress();
  mqttTopic += deviceID;
  mqttConfigTopic += deviceID;
  mqttThresholdTopic += deviceID;
  mqttStatusTopic += deviceID;
  
  sensors.begin();
  pinMode(FLAME_PIN, INPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  // Thiết lập WiFi (có thể chuyển sang Config Portal nếu chưa cấu hình)
  setupWiFi();
  
  // Chỉ tiếp tục nếu đã kết nối WiFi thành công
  if (WiFi.status() == WL_CONNECTED) {
    wifiConnected = true;
    setupMQTT();
    String payload = "Device " + deviceID + " connected.";
    client.publish(mqttDeviceTopic.c_str(), payload.c_str());
    
    // Tạo task FreeRTOS
    xTaskCreate(sensorTask, "SensorTask", 4096, NULL, 1, NULL);
    xTaskCreate(alertTask, "AlertTask", 2048, NULL, 1, NULL);
    xTaskCreate(wifiMonitorTask, "WiFiMonitorTask", 4096, NULL, 1, NULL);
  } else {
    Serial.println("WiFi chưa kết nối. Đang ở chế độ Config Portal.");
  }
}
void loop()
{
  if (Serial.available()) {
    String cmd = Serial.readStringUntil('\n');
    if (cmd == "CLEAR_WIFI") {
      clearWiFiConfig();
      Serial.println("Restarting...");
      ESP.restart();
    }
  }
  // Nếu đang ở chế độ Config Portal (AP mode)
  if (WiFi.getMode() == WIFI_AP) {
    server.handleClient();
    delay(10);
    return;
  }
  
  // Chế độ bình thường - kết nối MQTT
  if (!client.connected())
    setupMQTT();
  client.loop(); // Giữ MQTT sống và gửi keep-alive
  // Giảm delay để xử lý MQTT ping/pong nhanh hơn
  vTaskDelay(5 / portTICK_PERIOD_MS);
}
