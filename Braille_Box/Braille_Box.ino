#include <Servo.h>
char state;
Servo servo_1 , servo_2 , servo_3 , servo_4 , servo_5 , servo_6;
void process(int value){
  int state_bit[10];
  Serial.println(value);
  for(int i=6;i>=1;i--){
     int bit = value & 1;
     state_bit[i] = bit;
     value>>=1;
  }
  // Mặc định 90 là đẩy lên , 0 là đẩy xuống
  if(state_bit[1]) servo_6.write(85); else servo_6.write(90);
  if(state_bit[2]) servo_3.write(85); else servo_3.write(80);
  if(state_bit[3]) servo_2.write(110); else servo_2.write(115);
  if(state_bit[4]) servo_5.write(135); else servo_5.write(130);
  if(state_bit[5]) servo_4.write(95); else servo_4.write(100);
  if(state_bit[6]) servo_1.write(120); else servo_1.write(115);
  delay(1000);
  servo_1.write(85);
  servo_2.write(85);
  servo_3.write(110);
  servo_4.write(135);
  servo_5.write(95);
  servo_6.write(120);
}
void setup() {
  Serial.begin(9600);
  servo_1.attach(3);
  servo_2.attach(7);
  servo_3.attach(11);
  servo_4.attach(2);
  servo_5.attach(13);
  servo_6.attach(8);


  servo_1.write(100);
  servo_2.write(85);
  servo_3.write(110);
  servo_4.write(135);
  servo_5.write(95);
  servo_6.write(130);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
   state = Serial.read();
  Serial.println(state); 
  process(state - '0');
 } else  state = 0;
 
}
