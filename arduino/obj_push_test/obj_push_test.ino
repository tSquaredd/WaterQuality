#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// Set these to run example.
#define FIREBASE_HOST "waterquality-3a1d7.firebaseio.com"
#define FIREBASE_AUTH "z3B4jLe7c242lvWZc9GzC63AqugVG1x8cpf3mIZG"
#define WIFI_SSID "Verizon VS995 C19F"
#define WIFI_PASSWORD "Y!3cb[2Z"



void setup() {
  Serial.begin(9600);

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}
const int capacity = JSON_OBJECT_SIZE(10);
float b = 0.0;
int n = 0;

void loop(){
  StaticJsonBuffer<capacity> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  
  b = b + 1.0;
  n++;
  
  if(n == 10){
    b = 0.0;
    n = 0;
  }
  
  root["pH"] = b;
  root["temp"] = b;
  root["tds"] = b;
  root["flow"] = b;
  root["hour"] = n;
  root["min"] = n;
  root["sec"] = n;
  root["year"] = n;
  root["month"] = n;
  root["day"] = n;
  
  Firebase.push("DATA", root);

}
