#include <DS3231.h>

DS3231 rtc(SDA,SCL);

Time t;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);

  //start real time clock
  rtc.begin();
  
  //rtc.setTime(5,30,0);
  //rtc.setDate(3,11,2018);
}

float packet[12];

// set data integrity flags
packet[0] = 13.0;
packet[11] = 13.0;

void loop() {
  // put your main code here, to run repeatedly:
  
  // read/save data from pH sensor
  packet[1] = 1.0;
  
  // read/save data from tds sensor
  packet[2] = 1.0;
  
  // read/save data from temp sensor
  packet[3] = 1.0;
  
  // read/save data from flow meter
  packet[4] = 1.0;
  
  // read time from rtc and insert into packet
  t = rtc.getTime();
  
  packet[5] = t.hour;
  packet[6] = t.min;
  packet[7] = t.sec;
  packet[8] = t.year;
  packet[9] = t.mon;
  packet[10] = t.date;
  
  Serial.print("sending packet..");
  Serial1.write(
  
  // Wait one second before repeating :)
  delay (3000);



  
}
