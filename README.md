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

## 2. Danh sách linh kiện

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

## 3. Mapping chân ESP32 (theo code)

| Thiết bị          | Chân ESP32  |
| ----------------- | ----------- |
| DS18B20 (DATA)    | GPIO **4**  |
| MQ-2 (AO)         | GPIO **34** |
| CO sensor (AO)    | GPIO **35** |
| Flame sensor (DO) | GPIO **5**  |
| Buzzer            | GPIO **23** |

---

## 4. Sơ đồ lắp mạch (Fritzing – dạng Markdown)

### 4.1. DS18B20 – Cảm biến nhiệt độ

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

### 4.2. Cảm biến khí MQ-2

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

### 4.3. Cảm biến khí CO

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

### 4.4. Cảm biến Flame

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

### 4.5. Buzzer

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
