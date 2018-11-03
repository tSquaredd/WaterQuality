

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);

  Serial.print("mega active");
  Serial.print("waiting for wifi");
  
  while(Serial1.available() == 0){
    Serial.print(".");
    delay(500);
  }
  Serial.print(Serial1.readString());
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial1.available())
    Serial.print(Serial1.readString());
}
