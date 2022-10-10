#include <Servo.h>
char state;
Servo servo_1 , servo_2 , servo_3 , servo_4 , servo_5 , servo_6;
void process(int value){
  int state_bit[10];
  for(int i=6;i>=1;i--){
     int bit = value & 1;
     state_bit[i] = bit;
     value>>=1;
  }
  // Mặc định 90 là đẩy lên , 0 là đẩy xuống
  if(state_bit[1]) servo_1.write(90); else servo_1.write(0);
  if(state_bit[2]) servo_2.write(90); else servo_2.write(0);
  if(state_bit[3]) servo_3.write(90); else servo_3.write(0);
  if(state_bit[4]) servo_4.write(90); else servo_4.write(0);
  if(state_bit[5]) servo_5.write(90); else servo_5.write(0);
  if(state_bit[6]) servo_6.write(90); else servo_6.write(0);
}
void setup() {
  Serial.begin(9600);
  servo_1.attach(3);
  servo_2.attach(4);
  servo_3.attach(5);
  servo_4.attach(6);
  servo_5.attach(7);
  servo_6.attach(8);

  // Chỉnh lại góc đóng 
  servo_1.write(0);
  servo_2.write(0);
  servo_3.write(0);
  servo_4.write(0);
  servo_5.write(0);
  servo_6.write(0);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
   state = Serial.read();
 } else  state = 0;
 
 Serial.println(state); 
 process(state - '0');
}
