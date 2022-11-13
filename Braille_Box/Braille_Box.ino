#include <Servo.h>
char state;
Servo servo_1 , servo_2 , servo_3 , servo_4 , servo_5 , servo_6;
int speed = 1000;
void process(int value){
  int state_bit[10];
  for(int i=6;i>=1;i--){
     int bit = value & 1;
     state_bit[i] = bit;
     value>>=1;
  }
  // Mặc định 90 là đẩy lên , 0 là đẩy xuống
  if(state_bit[1]) servo_1.write(120); else servo_1.write(115);
  if(state_bit[2]) servo_2.write(100); else servo_2.write(105);
  if(state_bit[3]) servo_3.write(160); else servo_3.write(165);
  if(state_bit[4]) servo_4.write(85); else servo_4.write(80);
  if(state_bit[5]) servo_5.write(90); else servo_5.write(85);
  if(state_bit[6]) servo_6.write(85); else servo_6.write(80);
  delay(speed);
  servo_1.write(115);
  servo_2.write(105);
  servo_3.write(165);
  servo_4.write(80);
  servo_5.write(85);
  servo_6.write(80);
}
void setup() {
  Serial.begin(9600);
  servo_1.attach(8);
  servo_2.attach(11);
  servo_3.attach(7);
  servo_4.attach(3);
  servo_5.attach(2);
  servo_6.attach(13);

  // Chỉnh lại góc xuống
  servo_1.write(115);
  servo_2.write(105);
  servo_3.write(165);
  servo_4.write(80);
  servo_5.write(85);
  servo_6.write(80);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
   state = Serial.read();
   int cnt = state - '0';
   if(state == 1){
      speed = 500;
   } else if(state==2){
      speed = 1000;
   } else if(state==3){
      speed = 1500;
   } else process(cnt);
 } else  state = 0;
 
}
