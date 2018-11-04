#include <DallasTemperature.h>
#include <OneWire.h>
#include <DS3231.h>

//ph sensor
int ph_pin = A2;
int measure;
double voltage;
float po;
//tds sensor
#define TDS_SENSOR_PIN A1
#define VREF 5.0
#define SCOUNT 30
// store the analog value in the array, read from ADC
int analogBuffer[SCOUNT]; 
int analogBufferTemp[SCOUNT];
int analogBufferIndex = 0, copyIndex = 0;
float averageVoltage = 0, tdsValue = 0, temperature = 25;

// flow meter
#define FLOW_SENSOR_PIN 24
// count how many pulses!
volatile uint16_t pulses = 0;
// track the state of the pulse pin
volatile uint8_t lastflowpinstate;
// you can try to keep time of how long it is between pulses
volatile uint32_t lastflowratetimer = 0;
// and use that to calculate a flow rate
volatile float flowrate;
// Interrupt is called once a millisecond, looks for any pulses from the sensor!
SIGNAL(TIMER0_COMPA_vect) {
  uint8_t x = digitalRead(FLOW_SENSOR_PIN);
  
  if (x == lastflowpinstate) {
    lastflowratetimer++;
    return; // nothing changed!
  }
  
  if (x == HIGH) {
    //low to high transition!
    pulses++;
  }
  lastflowpinstate = x;
  flowrate = 1000.0;
  flowrate /= lastflowratetimer;  // in hertz
  lastflowratetimer = 0;
}
void useInterrupt(boolean v) {
  if (v) {
    // Timer0 is already used for millis() - we'll just interrupt somewhere
    // in the middle and call the "Compare A" function above
    OCR0A = 0xAF;
    TIMSK0 |= _BV(OCIE0A);
  } else {
    // do not call the interrupt function COMPA anymore
    TIMSK0 &= ~_BV(OCIE0A);
  }
}

// temp sensor
#define ONE_WIRE_BUS 22
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);
// real time clock
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
  
  // tds sensor setup
  pinMode(TDS_SENSOR_PIN, INPUT);
  
  // flow meter setup
  pinMode(FLOW_SENSOR_PIN, INPUT);
  digitalWrite(FLOW_SENSOR_PIN, HIGH);
  lastflowpinstate = digitalRead(FLOW_SENSOR_PIN);
  useInterrupt(true);
  
  //start real time clock
  rtc.begin();
  sensors.begin();
  
  //delay
  Serial.println("startup complete");
  
  //rtc.setTime(9,24,0);
  //rtc.setDate(4,11,2018);
}

DeviceAddress t_address;

int getMedianNum(int bArray[], int iFilterLen){
  int bTab[iFilterLen];
  for (byte i = 0; i<iFilterLen; i++)
    bTab[i] = bArray[i];
  int i, j, bTemp;
  for (j = 0; j < iFilterLen - 1; j++){
    for (i = 0; i < iFilterLen - j - 1; i++){
      if (bTab[i] > bTab[i + 1]){
        bTemp = bTab[i];
        bTab[i] = bTab[i + 1];
        bTab[i + 1] = bTemp;
      }
    }
 }
 if ((iFilterLen & 1) > 0)
  bTemp = bTab[(iFilterLen - 1) / 2];
 else
  bTemp = (bTab[iFilterLen / 2] + bTab[iFilterLen / 2 - 1]) / 2;
 return bTemp;
} 

int counter = 0;

void loop() {
  // reset flag
  flag = true;
  
  // read/save data from pH sensor
  measure = analogRead(ph_pin);
  voltage = 5 / 1024.0 * measure;
  po = 7 + ((2.5 - voltage) / 0.18);
  data.pH = po;
  Serial.print("pH: ");
  Serial.println(data.pH);

  // read/save data from temp sensor
  sensors.getAddress(t_address, 0);
  sensors.requestTemperatures();
  data.temp = sensors.getTempC(t_address);
  Serial.print("temp: ");
  Serial.print(data.temp);
  Serial.println(" C");
  temperature = data.temp;
  
  // read/save data from tds sensor
  static unsigned long analogSampleTimepoint = millis();
  //every 40 milliseconds,read the analog value from the ADC
  if(millis()-analogSampleTimepoint > 40U){
    analogSampleTimepoint = millis();
    //read the analog value and store into the buffer
    analogBuffer[analogBufferIndex] = analogRead(TDS_SENSOR_PIN); 
    analogBufferIndex++;
    if(analogBufferIndex == SCOUNT)
      analogBufferIndex = 0;
  }
  
  static unsigned long printTimepoint = millis();
  
  if(millis()-printTimepoint > 800U){
    printTimepoint = millis();
    for(copyIndex=0;copyIndex<SCOUNT;copyIndex++)
    analogBufferTemp[copyIndex]= analogBuffer[copyIndex];
    // read the analog value more stable by the median filtering algorithm, and convert to voltage value
    averageVoltage = getMedianNum(analogBufferTemp,SCOUNT) * (float)VREF / 1024.0;
    float compensationCoefficient=1.0+0.02*(temperature-25.0); 
    //temperature compensation formula: fFinalResult(25^C) = fFinalResult(current)/(1.0+0.02*(fTP-25.0));
    float compensationVolatge=averageVoltage/compensationCoefficient;
    //temperature compensation
    tdsValue=(133.42*compensationVolatge*compensationVolatge*compensationVolatge - 255.86*compensationVolatge*compensationVolatge + 857.39*compensationVolatge)*0.5; 
    //convert voltage value to tds value
    //Serial.print("voltage:");
    //Serial.print(averageVoltage,2);
    //Serial.print("V ");
    Serial.print("TDS Value:");
    Serial.print(tdsValue,0);
    Serial.println("ppm"); 
    data.tds = tdsValue;
  }
  
  
  // read/save data from flow meter
  float l_per_min = flowrate / 7.5;
  data.flow = l_per_min;
  Serial.print("Flow rate: ");
  Serial.print(data.flow);
  Serial.println(" L/min");
  
  // read time from rtc and insert into packet
  Serial.println("getting real time");
  t = rtc.getTime();
  data.h = t.hour;
  data.m = t.min;
  data.s = t.sec;
  data.y = t.year;
  data.mth = t.mon;
  data.d = t.date;
  Serial.print(data.h);
  Serial.print(":");
  Serial.print(data.m);
  Serial.print(":");
  Serial.print(data.s);
  Serial.print(" ");
  Serial.print(data.d);
  Serial.print("-");
  Serial.print(data.mth);
  Serial.print("-");
  Serial.println(data.y);
  
  char buff[PACKET_SIZE];

  memcpy(buff, &data, PACKET_SIZE);
  
  if(counter >= 20){
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
        Serial.println("sending packet..");
        Serial1.write(buff, PACKET_SIZE);
        // wait for response
        while(!Serial1.available()){
        };
        msg = Serial1.readString(); 
      } else if(msg == "sent"){
        Serial.println("send successful");
        flag = false; 
      } else {
        Serial.println("something is badddd");
        flag = false;
      }
    }
  }
  
  if(counter < 20){
    counter++;
  }
  
  
  // Wait five seconds before repeating :)
  delay (5000); 
 }
