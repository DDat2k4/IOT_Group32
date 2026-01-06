#include <OneWire.h>
#include <DallasTemperature.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
// ---------- Cấu hình WiFi và MQTT ----------
const char *ssid = "Kim Nguu T3";
const char *password = "0978587654t3";
// const char *mqttServer = "j6ce1b1c.ala.eu-central-1.emqxsl.com";
const char *mqttServer = "ud221f2f.ala.asia-southeast1.emqxsl.com";
const int mqttPort = 8883;
// const char *mqttUser = "nkquoc";    // Nếu có
// const char *mqttPass = "Soict2025"; 
const char *mqttUser = "iotgroup32";    
const char *mqttPass = "soict2025"; // Nếu có
String mqttTopic = "iot/fire/";
String mqttDeviceTopic = "iot/fire/device/";
String mqttConfigTopic = "iot/fire/config/";
String mqttThresholdTopic = "iot/threshold/";
String mqttStatusTopic = "iot/status/";

WiFiClientSecure espClient;
PubSubClient client(espClient);

// ----- File CA cho MQTT -----
// const char *ca_cert =
//     "-----BEGIN CERTIFICATE-----\n"
//     "MIIDjjCCAnagAwIBAgIQAzrx5qcRqaC7KGSxHQn65TANBgkqhkiG9w0BAQsFADBh"
//     "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3"
//     "d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBH"
//     "MjAeFw0xMzA4MDExMjAwMDBaFw0zODAxMTUxMjAwMDBaMGExCzAJBgNVBAYTAlVT"
//     "MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j"
//     "b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IEcyMIIBIjANBgkqhkiG"
//     "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuzfNNNx7a8myaJCtSnX/RrohCgiN9RlUyfuI"
//     "2/Ou8jqJkTx65qsGGmvPrC3oXgkkRLpimn7Wo6h+4FR1IAWsULecYxpsMNzaHxmx"
//     "1x7e/dfgy5SDN67sH0NO3Xss0r0upS/kqbitOtSZpLYl6ZtrAGCSYP9PIUkY92eQ"
//     "q2EGnI/yuum06ZIya7XzV+hdG82MHauVBJVJ8zUtluNJbd134/tJS7SsVQepj5Wz"
//     "tCO7TG1F8PapspUwtP1MVYwnSlcUfIKdzXOS0xZKBgyMUNGPHgm+F6HmIcr9g+UQ"
//     "vIOlCsRnKPZzFBQ9RnbDhxSJITRNrw9FDKZJobq7nMWxM4MphQIDAQABo0IwQDAP"
//     "BgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBhjAdBgNVHQ4EFgQUTiJUIBiV"
//     "5uNu5g/6+rkS7QYXjzkwDQYJKoZIhvcNAQELBQADggEBAGBnKJRvDkhj6zHd6mcY"
//     "1Yl9PMWLSn/pvtsrF9+wX3N3KjITOYFnQoQj8kVnNeyIv/iPsGEMNKSuIEyExtv4"
//     "NeF22d+mQrvHRAiGfzZ0JFrabA0UWTW98kndth/Jsw1HKj2ZL7tcu7XUIOGZX1NG"
//     "Fdtom/DzMNU+MeKNhJ7jitralj41E6Vf8PlwUHBHQRFXGU7Aj64GxJUTFy8bJZ91"
//     "8rGOmaFvE7FBcf6IKshPECBV1/MUReXgRPTqh5Uykw7+U0b6LJ3/iyK5S9kJRaTe"
//     "pLiaWN0bfVKfjllDiIGknibVb63dDcY3fe0Dkhvld1927jyNxF1WW6LZZm6zNTfl"
//     "MrY="
//     "-----END CERTIFICATE-----\n";
const char *ca_cert =
    "-----BEGIN CERTIFICATE-----\n"
    "MIIDjjCCAnagAwIBAgIQAzrx5qcRqaC7KGSxHQn65TANBgkqhkiG9w0BAQsFADBh"
    "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3"
    "d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBH"
    "MjAeFw0xMzA4MDExMjAwMDBaFw0zODAxMTUxMjAwMDBaMGExCzAJBgNVBAYTAlVT"
    "MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j"
    "b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IEcyMIIBIjANBgkqhkiG"
    "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuzfNNNx7a8myaJCtSnX/RrohCgiN9RlUyfuI"
    "2/Ou8jqJkTx65qsGGmvPrC3oXgkkRLpimn7Wo6h+4FR1IAWsULecYxpsMNzaHxmx"
    "1x7e/dfgy5SDN67sH0NO3Xss0r0upS/kqbitOtSZpLYl6ZtrAGCSYP9PIUkY92eQ"
    "q2EGnI/yuum06ZIya7XzV+hdG82MHauVBJVJ8zUtluNJbd134/tJS7SsVQepj5Wz"
    "tCO7TG1F8PapspUwtP1MVYwnSlcUfIKdzXOS0xZKBgyMUNGPHgm+F6HmIcr9g+UQ"
    "vIOlCsRnKPZzFBQ9RnbDhxSJITRNrw9FDKZJobq7nMWxM4MphQIDAQABo0IwQDAP"
    "BgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBhjAdBgNVHQ4EFgQUTiJUIBiV"
    "5uNu5g/6+rkS7QYXjzkwDQYJKoZIhvcNAQELBQADggEBAGBnKJRvDkhj6zHd6mcY"
    "1Yl9PMWLSn/pvtsrF9+wX3N3KjITOYFnQoQj8kVnNeyIv/iPsGEMNKSuIEyExtv4"
    "NeF22d+mQrvHRAiGfzZ0JFrabA0UWTW98kndth/Jsw1HKj2ZL7tcu7XUIOGZX1NG"
    "Fdtom/DzMNU+MeKNhJ7jitralj41E6Vf8PlwUHBHQRFXGU7Aj64GxJUTFy8bJZ91"
    "8rGOmaFvE7FBcf6IKshPECBV1/MUReXgRPTqh5Uykw7+U0b6LJ3/iyK5S9kJRaTe"
    "pLiaWN0bfVKfjllDiIGknibVb63dDcY3fe0Dkhvld1927jyNxF1WW6LZZm6zNTfl"
    "MrY="
    "-----END CERTIFICATE-----\n";

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
// Biến toàn cục để chia sẻ giữa các task
float temperature;
int mq2_val, co_val, flame_val;
bool alert = false;
int temp_threshold = 60;
int mq2_threshold = 800;
int co_threshold = 900;
bool en_temp = true;
bool en_mq2 = true;
bool en_co = true;
bool en_flame = true;
bool deviceActive = true; // ACTIVE hoặc INACTIVE

// ---------- Kết nối WiFi ----------
void setupWiFi()
{
  Serial.print("Connecting to WiFi");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");
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
  espClient.setCACert(ca_cert);
  client.setServer(mqttServer, mqttPort);
  client.setCallback(callback);
  
  // Chuẩn bị Last Will and Testament (LWT) message
  String lwt_topic = "iot/device/status/" + deviceID;
  String lwt_message = "{\"device\":\"" + deviceID + "\",\"value\":\"INACTIVE\"}";
  
  while (!client.connected())
  {
    Serial.println("Connecting to MQTT...");
    
    // Kết nối với LWT: client.connect(clientId, user, pass, willTopic, willQoS, willRetain, willMessage)
    if (client.connect(deviceID.c_str(), mqttUser, mqttPass, 
                       lwt_topic.c_str(), 0, true, lwt_message.c_str()))
    {
      Serial.println("connected");
      
      // Gửi message ONLINE ngay khi kết nối thành công
      String online_message = "{\"device\":\"" + deviceID + "\",\"value\":\"ACTIVE\"}";
      client.publish(lwt_topic.c_str(), online_message.c_str(), true);
      Serial.println("Published ACTIVE status");
      
      // Subscribe to all topics
      client.subscribe(mqttConfigTopic.c_str());
      Serial.println("Subscribed to: " + mqttConfigTopic);
      
      client.subscribe(mqttThresholdTopic.c_str());
      Serial.println("Subscribed to: " + mqttThresholdTopic);
      
      client.subscribe(mqttStatusTopic.c_str());
      Serial.println("Subscribed to: " + mqttStatusTopic);
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.println(client.state());
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

    Serial.print("Temp: ");
    Serial.print(temperature);
    Serial.print(" | Threshold: ");
    Serial.print(temp_threshold);
    Serial.print(" | MQ2: ");
    Serial.print(mq2_val);
    Serial.print(" | Threshold: ");
    Serial.print(mq2_threshold);
    Serial.print(" | CO: ");
    Serial.print(co_val);
    Serial.print(" | Threshold: ");
    Serial.print(co_threshold);
    Serial.print(" | Flame: ");
    Serial.println(flame_val);


    
    // Gửi dữ liệu lên MQTT (chỉ khi device ACTIVE)
    if (client.connected() && deviceActive)
    {
      String basePayload = "{\"sensorType\":\"";
      if (en_temp) {
        String msg = basePayload + "DS18B20\",\"value\":" + String(temperature) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());
      }
      if (en_mq2) {
        String msg = basePayload + "MQ2\",\"value\":" + String(mq2_val) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());
      }

      if (en_co) {
        String msg = basePayload + "CO\",\"value\":" + String(co_val) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());
      }

      if (en_flame) {
        String msg = basePayload + "FLAME\",\"value\":" + String(flame_val) + "}";
        client.publish(mqttTopic.c_str(), msg.c_str());
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
void setup()
{
  Serial.begin(115200);
  mqttTopic += deviceID;
  mqttConfigTopic += deviceID;
  mqttThresholdTopic += deviceID;
  mqttStatusTopic += deviceID;
  sensors.begin();
  pinMode(FLAME_PIN, INPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  setupWiFi();
  setupMQTT();
  String payload = "Device " + deviceID + " connected.";
  client.publish(mqttDeviceTopic.c_str(), payload.c_str());
  // Tạo task FreeRTOS
  xTaskCreate(sensorTask, "SensorTask", 4096, NULL, 1, NULL);
  xTaskCreate(alertTask, "AlertTask", 2048, NULL, 1, NULL);
}
void loop()
{
  if (!client.connected())
    setupMQTT();
  client.loop(); // Giữ MQTT sống
  vTaskDelay(10 / portTICK_PERIOD_MS);
}
