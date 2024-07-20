#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClientSecure.h>

// Define the pin for PIR sensor
const int pirPin = D2;   // Pin of the PIR sensor

// WiFi configuration
const char* ssid = "your_ssid";
const char* password = "your_password";

// Flask endpoints
const char* loginServerName = "https://yourserver.com/login";
const char* movementsServerName = "https://yourserver.com/api/movements";

// SSL fingerprint of your server (use your server's SSL fingerprint)
const char* fingerprint = "your_server_fingerprint";

// Store JWT token
String jwtToken;

// Create a secure WiFiClient
WiFiClientSecure client;

void setup() {
  pinMode(pirPin, INPUT);    // Set the PIR pin as input
  Serial.begin(115200);      // Start the serial communication

  // Connect to WiFi
  WiFi.begin(ssid, password);
  Serial.println("Connecting to WiFi...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi");

  // Set the SSL fingerprint
  client.setFingerprint(fingerprint);

  // Perform login to obtain JWT token
  if (performLogin("your_username", "your_password")) {
    Serial.println("Login successful");
  } else {
    Serial.println("Login failed");
  }
}

void loop() {
  int pirState = digitalRead(pirPin);  // Read the state of the PIR sensor

  if (pirState == HIGH) {
    if (!sendMovementDetected()) {
      // If sending movement data fails due to expired token, try logging in again
      if (performLogin("your_username", "your_password")) {
        Serial.println("Re-login successful");
        // Try sending the movement data again after re-login
        sendMovementDetected();
      } else {
        Serial.println("Re-login failed");
      }
    }
    Serial.println("Movement detected!");  // Print message to serial
  } else {
    Serial.println("No movement.");  // Print message to serial
  }

  delay(1000);  // Pause for 10 seconds between requests
}

bool performLogin(const char* username, const char* password) {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(client, loginServerName);  // Use updated API

    http.addHeader("Content-Type", "application/json");
    String payload = "{\"username\":\"" + String(username) + "\",\"password\":\"" + String(password) + "\"}";
    int httpResponseCode = http.POST(payload);

    if (httpResponseCode == 200) {
      String response = http.getString();
      Serial.println("Login response:");
      Serial.println(response);

      // Extract JWT token from the response
      int tokenIndex = response.indexOf("access_token\":\"") + 15;
      int tokenEndIndex = response.indexOf("\"", tokenIndex);
      jwtToken = response.substring(tokenIndex, tokenEndIndex);

      http.end();
      return true;
    } else {
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
      http.end();
      return false;
    }
  } else {
    Serial.println("Error in WiFi connection");
    return false;
  }
}

bool sendMovementDetected() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(client, movementsServerName);  // Use updated API

    http.addHeader("Content-Type", "application/json");
    http.addHeader("Authorization", "Bearer " + jwtToken);
    String payload = "{\"movement\": \"detected\"}";
    int httpResponseCode = http.POST(payload);

    if (httpResponseCode == 200) {
      String response = http.getString();
      Serial.println(httpResponseCode);   // Print response code
      Serial.println(response);           // Print server response
      http.end();  // Close connection
      return true;
    } else if (httpResponseCode == 401) {
      String response = http.getString();
      Serial.println(httpResponseCode);   // Print response code
      Serial.println(response);           // Print server response
      if (response.indexOf("Token has expired") >= 0) {
        Serial.println("Token has expired, need to re-login");
      }
    } else {
      Serial.print("Error on sending POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();  // Close connection
  } else {
    Serial.println("Error in WiFi connection");
  }
  return false;
}
