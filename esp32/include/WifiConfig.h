#ifndef WIFICONFIG_H
#define WIFICONFIG_H

#include <Arduino.h>
#include <WebServer.h>

// Biến toàn cục
extern String saved_ssid;
extern String saved_password;
extern WebServer server;

// Các hàm công khai
void handleRoot();
void handleSave();
void startConfigPortal();
void startConnecting();

#endif // WIFICONFIG_H
