#include <WiFi.h> 
#include <WebServer.h> 
#include <Preferences.h> 

// Định nghĩa biến toàn cục
String saved_ssid = "";
String saved_password = "";

// Khởi tạo các đối tượng 
Preferences preferences; 
WebServer server(80); 

// Khai báo biến lưu cấu hình 
const char* pref_namespace = "wifi_config";
// -- CHẾ ĐỘ CẤU HÌNH (AP + WebServer) 
// tạo trang HTML cho người dùng nhập SSID/Password 
void handleRoot() { 
String html = R"rawliteral( 
<!DOCTYPE html> 
 9   
<html> 
<head> 
<meta charset="UTF-8" /> 
<meta name="viewport" content="width=device-width, initial-scale=1"> 
<title>ESP32 WiFi Config</title> 
<style> 
body{font-family: sans-serif; text-align: center; margin-top: 50px;} 
input[type=text], input[type=password] {width: 80%; padding: 12px 20px; margin: 8px 0; 
display: inline-block; border: 1px solid #ccc; box-sizing: border-box;} 
button {background-color: #04AA6D; color: white; padding: 14px 20px; margin: 8px 0; 
border: none; cursor: pointer; width: 80%;} 
</style> 
</head> 
<body> 
  <h2>Cấu hình WiFi cho ESP32</h2> 
  <form action="/save" method="get"> 
    <label for="ssid"><b>SSID (Tên mạng)</b></label><br> 
    <input type="text" placeholder="Nhập SSID" name="ssid" required><br> 
 
    <label for="pass"><b>Password</b></label><br> 
    <input type="password" placeholder="Nhập Password" name="pass"><br> 
 
    <button type="submit">Lưu & Khởi động lại</button> 
  </form> 
</body> 
</html> 
)rawliteral"; 
  server.send(200, "text/html", html); // gửi trang HTML cho người dùng 
} 
 
// Hàm xử lý khi người dùng nhấn nút Lưu 
void handleSave() { 
  // Lấy tham số SSID và Password từ form HTML 
  saved_ssid = server.arg("ssid"); 
  saved_password = server.arg("pass"); 
 
  Serial.print("Đã nhận cấu hình mới: SSID="); 
  Serial.println(saved_ssid); 
 
  // Lưu cấu hình vào Preferences 
  preferences.begin(pref_namespace, false); 
  preferences.putString("ssid", saved_ssid); 
  preferences.putString("pass", saved_password); 
  preferences.end(); 
 
  // Gửi phản hồi 
  server.send(200, "text/plain; charset=UTF-8", "Cấu hình đã được lưu. Thiết bị đang khởi động lại..."); 
 
  // Khởi động lại thiết bị 
  delay(1000); 
  ESP.restart(); 
} 
 
// Hàm khởi tạo chế độ Access Point và Web Server 
void startConfigPortal() { 
  Serial.println("--- BẬT CHẾ ĐỘ CẤU HÌNH ---"); 
 
  // Thiết lập ESP32 chạy ở chế độ Access Point 
  WiFi.mode(WIFI_AP); 
  const char* ap_ssid = "ESP32-CONFIG-AP"; 
  const char* ap_password = "12345678"; 
 
  WiFi.softAP(ap_ssid, ap_password); 
  IPAddress IP = WiFi.softAPIP(); 
   
  Serial.print("AP SSID: "); 
  Serial.println(ap_ssid); 
  Serial.print("Web Server IP: "); 
  Serial.println(IP); 
 
  // Khởi tạo Web Server 
  server.on("/", handleRoot); 
  server.on("/save", handleSave); 
  server.begin(); 
   
  Serial.println("Web Server đã khởi động."); 
} 
 
// -- CHẾ ĐỘ KẾT NỐI (STA) 
 
void startConnecting() { 
  Serial.print("Đang kết nối tới mạng đã lưu: "); 
  Serial.println(saved_ssid); 
 
  WiFi.mode(WIFI_STA); 
  WiFi.begin(saved_ssid.c_str(), saved_password.c_str()); 
 
  int connect_timeout = 20; // 20 giây 
 
  // Chờ kết nối 
  while (WiFi.status() != WL_CONNECTED && connect_timeout > 0) { 
    delay(1000); 
    Serial.print("."); 
    connect_timeout--; 
  } 
 
  // Kiểm tra kết quả 
  if (WiFi.status() == WL_CONNECTED) { 
    Serial.println("\n Đã kết nối WiFi thành công!"); 
    Serial.print("Địa chỉ IP: "); 
    Serial.println(WiFi.localIP()); 
    Serial.println(""); 
  } else { 
    Serial.println("\n Kết nối thất bại. Chuyển sang chế độ Cấu hình."); 
    startConfigPortal();  
  } 
} 
