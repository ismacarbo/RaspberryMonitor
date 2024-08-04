#include "esp_camera.h"
#include <WiFi.h>
#include <WebServer.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>

// Configurazione WiFi
const char* ssid = "Telecom casa";
const char* password = "40166575";

// Endpoint del server Flask
const char* loginServerName = "https://ismacarbo.zapto.org/login";
const char* movementsServerName = "https://ismacarbo.zapto.org/api/movements";

// Token JWT
String jwtToken;

// Creare un WiFiClient sicuro
WiFiClientSecure client;

// Configurazione della fotocamera
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

camera_fb_t* previousFrame = NULL; // Memorizza il frame precedente per il rilevamento del movimento

WebServer server(80);

void startCameraServer();

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connessione a WiFi...");
  }
  Serial.println("Connesso a WiFi");
  Serial.println(WiFi.localIP());

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

  // Inizializzazione della fotocamera
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Inizializzazione fotocamera fallita con errore 0x%x", err);
    return;
  }

  // Effettua il login per ottenere il token JWT
  if (performLogin("admin", "admin")) {
    Serial.println("Login riuscito");
  } else {
    Serial.println("Login fallito");
  }

  startCameraServer();
}

void loop() {
  server.handleClient();
  // Cattura un frame
  camera_fb_t * fb = esp_camera_fb_get();
  if (!fb) {
    Serial.println("Cattura fotocamera fallita");
    return;
  }

  // Esegue il rilevamento del movimento
  bool motionDetected = detectMotion(fb);
  
  if (motionDetected) {
    if (!sendMovementDetected()) {
      // Se l'invio dei dati di movimento fallisce a causa di un token scaduto, prova a effettuare nuovamente il login
      if (performLogin("admin", "admin")) {
        Serial.println("Re-login riuscito");
        // Prova a inviare nuovamente i dati di movimento dopo il re-login
        sendMovementDetected();
      } else {
        Serial.println("Re-login fallito");
      }
    }
    Serial.println("Movimento rilevato!");  // Stampa un messaggio sul seriale
  }

  // Restituisce il buffer del frame al driver
  esp_camera_fb_return(fb);

  delay(1000);  // Pausa di 1 secondo tra i controlli
}

void handle_jpg_stream() {
  camera_fb_t * fb = NULL;
  esp_err_t res = ESP_OK;
  size_t _jpg_buf_len = 0;
  uint8_t * _jpg_buf = NULL;
  char * part_buf[64];
  static const char* _STREAM_CONTENT_TYPE = "multipart/x-mixed-replace;boundary=frame";
  static const char* _STREAM_BOUNDARY = "\r\n--frame\r\n";
  static const char* _STREAM_PART = "Content-Type: image/jpeg\r\nContent-Length: %u\r\n\r\n";

  WiFiClient client = server.client();
  if (!client.connected()) {
    return;
  }

  client.printf("HTTP/1.1 200 OK\r\n");
  client.printf("Content-Type: %s\r\n\r\n", _STREAM_CONTENT_TYPE);

  while (true) {
    fb = esp_camera_fb_get();
    if (!fb) {
      Serial.println("Camera capture failed");
      res = ESP_FAIL;
    } else {
      if (fb->format != PIXFORMAT_JPEG) {
        bool jpeg_converted = frame2jpg(fb, 80, &_jpg_buf, &_jpg_buf_len);
        if (!jpeg_converted) {
          Serial.println("JPEG compression failed");
          esp_camera_fb_return(fb);
          res = ESP_FAIL;
        }
      } else {
        _jpg_buf_len = fb->len;
        _jpg_buf = fb->buf;
      }
    }
    if (res == ESP_OK) {
      client.printf(_STREAM_BOUNDARY);
      client.printf(_STREAM_PART, _jpg_buf_len);
      client.write(_jpg_buf, _jpg_buf_len);
    }
    if (fb) {
      esp_camera_fb_return(fb);
      _jpg_buf = NULL;
    } else if (_jpg_buf) {
      free(_jpg_buf);
      _jpg_buf = NULL;
    }
    if (!client.connected()) {
      break;
    }
  }
}

void startCameraServer() {
  server.on("/", HTTP_GET, handle_jpg_stream);
  server.begin();
}

bool detectMotion(camera_fb_t * frame) {
  if (previousFrame == NULL) {
    // Se non c'è un frame precedente, memorizza il frame corrente e restituisci che non è stato rilevato alcun movimento
    previousFrame = frame;
    return false;
  }

  // Confronta il frame corrente con il frame precedente
  bool motionDetected = false;
  int threshold = 20; // Soglia per il rilevamento del movimento
  int motionCount = 0;
  int pixelCount = frame->len;

  // Metodo semplice di differenza di pixel
  for (int i = 0; i < pixelCount; i++) {
    if (abs(frame->buf[i] - previousFrame->buf[i]) > threshold) {
      motionCount++;
    }
  }

  // Imposta il rilevamento del movimento se un numero significativo di pixel differisce
  if (motionCount > pixelCount * 0.1) { // Regola la sensibilità secondo necessità
    motionDetected = true;
  }

  // Libera il frame precedente e aggiornalo al frame corrente
  esp_camera_fb_return(previousFrame);
  previousFrame = frame;

  return motionDetected;
}

bool performLogin(const char* username, const char* password) {
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Inizio login...");
    client.setInsecure(); // Ignora la verifica del certificato
    HTTPClient http;
    http.begin(client, loginServerName);  // Usa l'API aggiornata

    http.addHeader("Content-Type", "application/json");
    String payload = "{\"username\":\"" + String(username) + "\",\"password\":\"" + String(password) + "\"}";
    Serial.print("Payload: ");
    Serial.println(payload);
    int httpResponseCode = http.POST(payload);

    Serial.print("Codice di risposta HTTP: ");
    Serial.println(httpResponseCode);

    if (httpResponseCode == 200) {
      String response = http.getString();
      Serial.println("Risposta login:");
      Serial.println(response);

      // Estrai il token JWT dalla risposta
      int tokenIndex = response.indexOf("access_token\":\"") + 15;
      int tokenEndIndex = response.indexOf("\"", tokenIndex);
      jwtToken = response.substring(tokenIndex, tokenEndIndex);

      http.end();
      return true;
    } else {
      Serial.print("Errore durante l'invio del POST: ");
      Serial.println(httpResponseCode);
      String response = http.getString();
      Serial.println("Risposta dal server:");
      Serial.println(response);
      http.end();
      return false;
    }
  } else {
    Serial.println("Errore nella connessione WiFi");
    return false;
  }
}

bool sendMovementDetected() {
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Invio dati movimento...");
    client.setInsecure(); // Ignora la verifica del certificato
    HTTPClient http;
    http.begin(client, movementsServerName);  // Usa l'API aggiornata

    http.addHeader("Content-Type", "application/json");
    http.addHeader("Authorization", "Bearer " + jwtToken);
    String payload = "{\"movement\": \"detected\"}";
    int httpResponseCode = http.POST(payload);

    Serial.print("Codice di risposta HTTP: ");
    Serial.println(httpResponseCode);

    if (httpResponseCode == 200) {
      String response = http.getString();
      Serial.println(httpResponseCode);   // Stampa il codice di risposta
      Serial.println(response);           // Stampa la risposta del server
      http.end();  // Chiude la connessione
      return true;
    } else if (httpResponseCode == 401) {
      String response = http.getString();
      Serial.println(httpResponseCode);   // Stampa il codice di risposta
      Serial.println(response);           // Stampa la risposta del server
      if (response.indexOf("Token has expired") >= 0) {
        Serial.println("Token scaduto, è necessario effettuare nuovamente il login");
      }
    } else {
      Serial.print("Errore durante l'invio del POST: ");
      Serial.println(httpResponseCode);
    }
    http.end();  // Chiude la connessione
  } else {
    Serial.println("Errore nella connessione WiFi");
  }
  return false;
}
