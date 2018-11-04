#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// Set these to run example.
#define FIREBASE_HOST "waterquality-3a1d7.firebaseio.com"
#define FIREBASE_AUTH "z3B4jLe7c242lvWZc9GzC63AqugVG1x8cpf3mIZG"
#define WIFI_SSID "Verizon VS995 C19F"
#define WIFI_PASSWORD "Y!3cb[2Z"

struct data_t{
  float pH;
  float tds;
  float temp;
  float flow;
  uint16_t h;
  uint16_t m;
  uint16_t s;
  uint16_t y;
  uint16_t mth;
  uint16_t d;
  };
  
#define PACKET_SIZE sizeof(data_t)


void setup() {
  Serial.begin(9600);
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  Serial.println("connected");
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

// local vars
const int capacity = JSON_OBJECT_SIZE(10);

StaticJsonBuffer<capacity> jsonBuffer;

JsonObject& root = jsonBuffer.createObject();

char read_buff[PACKET_SIZE];

data_t test_read;

// main loop
void loop(){
  
  while(!Serial.available()){
    };
    
  Serial.readBytes(read_buff, PACKET_SIZE);
  
  memcpy(&test_read, read_buff, PACKET_SIZE);
  
  root["pH"] = test_read.pH;
  root["temp"] = test_read.temp;
  root["tds"] = test_read.tds;
  root["flow"] = test_read.flow;
  root["hour"] = test_read.h;
  root["min"] = test_read.m;
  root["sec"] = test_read.s;
  root["year"] = test_read.y;
  root["month"] = test_read.mth;
  root["day"] = test_read.d;
  
  Firebase.push("DATA", root);

  if(Firebase.success()){
    Serial.print("sent");
    } else{
      Serial.print("bad");
    }
}
