#include <DS3231.h>

DS3231 rtc(SDA,SCL);

Time t;

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

data_t data;

bool flag;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);

  //start real time clock
  rtc.begin();

  //delay
  delay(5000);
  
  //rtc.setTime(5,30,0);
  //rtc.setDate(3,11,2018);
}

void loop() {
  // reset flag
  flag = true;
  
  // put your main code here, to run repeatedly:
  
  // read/save data from pH sensor
  data.pH = 1.0;
  
  // read/save data from tds sensor
  data.tds = 1.0;
  
  // read/save data from temp sensor
  data.temp = 1.0;
  
  // read/save data from flow meter
  data.flow = 1.0;
  
  // read time from rtc and insert into packet
  t = rtc.getTime();
  data.h = t.hour;
  data.m = t.min;
  data.s = t.sec;
  data.y = t.year;
  data.mth = t.mon;
  data.d = t.date;

  char buff[PACKET_SIZE];

  memcpy(buff, &data, PACKET_SIZE);
  
  Serial.print("sending packet..");
  Serial1.write(buff, PACKET_SIZE);

  // wait for signal back
  while(!Serial1.available()){
    };

  // error check
  String msg = Serial1.readString();
  while(flag){
    if(msg == "bad"){
      // resend data
      Serial.print("sending packet..");
      Serial1.write(buff, PACKET_SIZE);
      // wait for response
      while(!Serial1.available()){
      };
      msg = Serial1.readString(); 
    } else if(msg == "sent"){
      flag = false; 
    } else {
      Serial.println("something is badddd");
      flag = false;
    }
  }
  
  // Wait three seconds before repeating :)
  delay (3000); 
}
