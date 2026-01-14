# 🔥 Hệ thống cảnh báo cháy sử dụng ESP32 + MQTT (TLS)

## 1. Tổng quan

Hệ thống sử dụng **ESP32** để thu thập dữ liệu từ nhiều cảm biến nhằm phát hiện cháy sớm:

- 🌡️ Nhiệt độ: **DS18B20**
- 🔥 Khí gas: **MQ-2**
- ☠️ Khí CO
- 🔥 Cảm biến lửa (Flame)
- 🚨 Buzzer cảnh báo

Dữ liệu được gửi lên **MQTT Broker (EMQX Cloud)** thông qua **TLS (port 8883)**.
Thiết bị có thể bật/tắt từ xa, cấu hình ngưỡng cảnh báo qua MQTT.

---

## 2. Cấu hình WiFi và MQTT

Hệ thống hỗ trợ **2 cách cấu hình WiFi**:

### Cấu hình qua Web Portal

1. **Upload code** lên ESP32 lần đầu
2. ESP32 sẽ tự động tạo **WiFi Access Point** với tên: `ESP32-CONFIG-AP`
3. Kết nối điện thoại/máy tính vào WiFi `ESP32-CONFIG-AP` (Password: `12345678`)
4. Trình duyệt sẽ tự động mở, hoặc truy cập: `http://192.168.4.1`
5. Nhập tên WiFi và mật khẩu của bạn
6. Nhấn **Lưu & Khởi động lại**
7. ESP32 sẽ tự động kết nối vào WiFi của bạn!

**Xóa cấu hình WiFi đã lưu:**
Nếu muốn cấu hình lại WiFi, uncomment dòng sau trong `setup()` và upload lại:

```cpp
// preferences.clear(); // Xóa cấu hình WiFi đã lưu
```

---

#### 2.1. Bước 1: Tạo file cấu hình

1. Mở file `include/config.h.example`
2. **Tùy chọn A**: Sửa trực tiếp file `include/config.h`
3. **Tùy chọn B**: Sao chép `config.h.example` thành `config.h` rồi chỉnh sửa

#### 2.2. Bước 2: Điền thông tin WiFi

Mở file `include/config.h` và thay đổi thông tin sau:

```cpp
// ---------- CẤU HÌNH WIFI ----------
const char *WIFI_SSID = "TenWiFi_CuaBan";        // Thay bằng tên WiFi của bạn
const char *WIFI_PASSWORD = "MatKhauWiFi_CuaBan"; // Thay bằng mật khẩu WiFi
```

#### 2.3. Lưu ý

- File `config.h` chứa thông tin nhạy cảm (mật khẩu WiFi), **KHÔNG nên commit lên Git**
- File `config.h.example` là template, có thể commit lên Git để người khác tham khảo
- Sau khi chỉnh sửa `config.h`, cần **build và upload lại** code lên ESP32

---

## 3. Danh sách linh kiện

| STT | Linh kiện                            |
| --- | ------------------------------------ |
| 1   | ESP32 Dev Module                     |
| 2   | Cảm biến nhiệt độ DS18B20            |
| 3   | Điện trở 4.7kΩ (pull-up cho DS18B20) |
| 4   | Cảm biến khí MQ-2                    |
| 5   | Cảm biến khí CO (analog)             |
| 6   | Cảm biến Flame (digital)             |
| 7   | Buzzer                               |
| 8   | Breadboard                           |
| 9   | Dây jump                             |

---

## 4. Mapping chân ESP32 (theo code)

| Thiết bị          | Chân ESP32  |
| ----------------- | ----------- |
| DS18B20 (DATA)    | GPIO **4**  |
| MQ-2 (AO)         | GPIO **34** |
| CO sensor (AO)    | GPIO **35** |
| Flame sensor (DO) | GPIO **5**  |
| Buzzer            | GPIO **23** |

---

## 5. Sơ đồ lắp mạch (Fritzing – dạng Markdown)

### 5.1. DS18B20 – Cảm biến nhiệt độ

       DS18B20
     +-----------+
     |           |
     |   VCC o---+------------------- 3.3V
     |           |
     |  DATA o---+---- GPIO 4
     |           |      |
     |   GND o---+------+
     +-----------+      |
                        |
                   [ 4.7kΩ ]
                        |
                       3.3V

⚠️ **Bắt buộc có điện trở 4.7kΩ giữa DATA ↔ 3.3V**

---

### 5.2. Cảm biến khí MQ-2

        MQ-2
    +------------+
    |            |
    |  VCC  o----+------------------- 5V
    |            |
    |  GND  o----+------------------- GND
    |            |
    |  AO   o----+------------------- GPIO 34
    |            |
    +------------+

---

### 5.3. Cảm biến khí CO

       CO Sensor
    +---------------+
    |               |
    |  VCC  o-------+---------------- 5V
    |               |
    |  GND  o-------+---------------- GND
    |               |
    |  AO   o-------+---------------- GPIO 35
    |               |
    +---------------+

---

### 5.4. Cảm biến Flame

     Flame Sensor
     +---------------+
     |               |
     |  VCC  o-------+---------------- 3.3V
     |               |
     |  GND  o-------+---------------- GND
     |               |
     |  DO   o-------+---------------- GPIO 5
     |               |
     +---------------+

---

### 5.5. Buzzer

     Buzzer
     +----------+
     |          |
     |   +  o---+---------------- GPIO 23
     |          |
     |   -  o---+---------------- GND
     |          |
     +----------+

---

**Ghi chú:**

- GPIO **34, 35**: ADC only (chỉ đọc analog)
- GPIO **4**: OneWire (DS18B20)
- GPIO **5**: Digital Input (Flame)
- GPIO **23**: Digital Output (Buzzer)
- **Tất cả GND nối chung**
