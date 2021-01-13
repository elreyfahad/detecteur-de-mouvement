#include <SoftwareSerial.h>
const int trigPin=2;
const int echoPin=3;
long duration;
int distance;

SoftwareSerial BTserial(9,10); // TX | RX
void setup() {

pinMode(trigPin,OUTPUT);
pinMode(echoPin,INPUT);
Serial.begin(9600);
BTserial.begin(9600); 
}

void loop(){
  digitalWrite(trigPin,LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin,HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin,LOW); 
  duration=pulseIn(echoPin,HIGH);
  distance=duration*0.034/2;
  
  if(distance<50){
    Serial.print("distance :");
    Serial.println(distance);
    
    BTserial.print(" ");
    BTserial.print(distance);
     delay(60000);
    }


}
