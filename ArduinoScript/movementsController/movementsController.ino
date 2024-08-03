#include "esp_camera.h"
#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>

// WiFi Configuration
const char* ssid = "your_wifi_ssid";
const char* password = "your_wifi_password";

// Flask server endpoints
const char* loginServerName = "https://yourserver.com/login";
const char* movementsServerName = "https://yourserver.com/api/movements";

// JWT Token
String jwtToken;

// Create a secure WiFiClient
WiFiClientSecure client;

// Camera configuration
#define PWDN_GPIO_NUM     32
#define RESET_GPIO_NUM    -1
#define XCLK_GPIO_NUM      0
#define SIOD_GPIO_NUM     26
#define SIOC_GPIO_NUM     27

#define Y9_GPIO_NUM       35
#define Y8_GPIO_NUM       34
#define Y7_GPIO_NUM       39
#define Y6_GPIO_NUM       36
#define Y5_GPIO_NUM       21
#define Y4_GPIO_NUM       19
#define Y3_GPIO_NUM       18
#define Y2_GPIO_NUM        5
#define VSYNC_GPIO_NUM    25
#define HREF_GPIO_NUM     23
#define PCLK_GPIO_NUM     22

camera_fb_t* previousFrame = NULL; // Stores the previous frame for motion detection

void setup() {
  Serial.begin(115200);
  // Connect to Wi-Fi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");

  // Camera configuration
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sscb_sda = SIOD_GPIO_NUM;
  config.pin_sscb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;

  if (psramFound()) {
    config.frame_size = FRAMESIZE_UXGA;
    config.jpeg_quality = 10;
    config.fb_count = 2;
  } else {
    config.frame_size = FRAMESIZE_SVGA;
    config.jpeg_quality = 12;
    config.fb_count = 1;
  }

  // Camera initialization
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Camera initialization failed with error 0x%x", err);
    return;
  }

  // Perform login to obtain JWT token
  if (performLogin("admin", "admin")) {
    Serial.println("Login successful");
  } else {
    Serial.println("Login failed");
  }
}

void loop() {
  // Capture a frame
  camera_fb_t * fb = esp_camera_fb_get();
  if (!fb) {
    Serial.println("Camera capture failed");
    return;
  }

  // Perform motion detection
  bool motionDetected = detectMotion(fb);
  
  if (motionDetected) {
    if (!sendMovementDetected()) {
      // If sending movement data fails due to an expired token, try logging in again
      if (performLogin("admin", "admin")) {
        Serial.println("Re-login successful");
        // Try sending the movement data again after re-login
        sendMovementDetected();
      } else {
        Serial.println("Re-login failed");
      }
    }
    Serial.println("Motion detected!");  // Print a message to the serial
  }

  // Return the frame buffer to the driver
  esp_camera_fb_return(fb);

  delay(1000);  // Pause for 1 second between checks
}

bool detectMotion(camera_fb_t * frame) {
  if (previousFrame == NULL) {
    // If there is no previous frame, store the current frame and return that no motion was detected
    previousFrame = frame;
    return false;
  }

  // Compare the current frame with the previous frame
  bool motionDetected = false;
  int threshold = 20; // Threshold for motion detection
  int motionCount = 0;
  int pixelCount = frame->len;

  // Simple pixel difference method
  for (int i = 0; i < pixelCount; i++) {
    if (abs(frame->buf[i] - previousFrame->buf[i]) > threshold) {
      motionCount++;
    }
  }

  // Set motion detection if a significant number of pixels differ
  if (motionCount > pixelCount * 0.1) { // Adjust sensitivity as needed
    motionDetected = true;
  }

  // Free the previous frame and update it to the current frame
  esp_camera_fb_return(previousFrame);
  previousFrame = frame;

  return motionDetected;
}

bool performLogin(const char* username, const char* password) {
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Starting login...");
    client.setInsecure(); // Ignore certificate verification
    HTTPClient http;
    http.begin(client, loginServerName);  // Use the updated API

    http.addHeader("Content-Type", "application/json");
    String payload = "{\"username\":\"" + String(username) + "\",\"password\":\"" + String(password) + "\"}";
    Serial.print("Payload: ");
    Serial.println(payload);
    int httpResponseCode = http.POST(payload);

    Serial.print("HTTP Response Code: ");
    Serial.println(httpResponseCode);

    if (httpResponseCode == 200) {
      String response = http.getString();
      Serial.println("Login response:");
      Serial.println(response);

      // Extract the JWT token from the response
      int tokenIndex = response.indexOf("access_token\":\"") + 15;
      int tokenEndIndex = response.indexOf("\"", tokenIndex);
      jwtToken = response.substring(tokenIndex, tokenEndIndex);

      http.end();
      return true;
    } else {
      Serial.print("Error during POST: ");
      Serial.println(httpResponseCode);
      String response = http.getString();
      Serial.println("Server response:");
      Serial.println(response);
      http.end();
      return false;
    }
  } else {
    Serial.println("WiFi connection error");
    return false;
  }
}

bool sendMovementDetected() {
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Sending movement data...");
    client.setInsecure(); // Ignore certificate verification
    HTTPClient http;
    http.begin(client, movementsServerName);  // Use the updated API

    http.addHeader("Content-Type", "application/json");
    http.addHeader("Authorization", "Bearer " + jwtToken);
    String payload = "{\"movement\": \"detected\"}";
    int httpResponseCode = http.POST(payload);

    Serial.print("HTTP Response Code: ");
    Serial.println(httpResponseCode);

    if (httpResponseCode == 200) {
      String response = http.getString();
      Serial.println(httpResponseCode);   // Print the response code
      Serial.println(response);           // Print the server response
      http.end();  // Close the connection
      return true;
    } else if (httpResponseCode == 401) {
      String response = http.getString();
      Serial.println(httpResponseCode);   // Print the response code
      Serial.println(response);           // Print the server response
      if (response.indexOf("Token has expired") >= 0) {
        Serial.println("Token expired, need to login again");
      }
    } else {
      Serial.print("Error during POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();  // Close the connection
  } else {
    Serial.println("WiFi connection error");
  }
  return false;
}
