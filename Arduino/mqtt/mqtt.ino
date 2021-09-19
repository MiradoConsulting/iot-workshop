// This example uses an Arduino/Genuino Zero together with
// a WiFi101 Shield or a MKR1000 to connect to shiftr.io.
//
// IMPORTANT: This example uses the new WiFi101 library.
//
// You can check on your device after a successful
// connection here: https://www.shiftr.io/try.
//
// by Gilberto Conti
// https://github.com/256dpi/arduino-mqtt

#include <MQTT.h>
#include "MessageBuffer.h"

String deviceId = "your-device-id-here";
MQTTClient mqtt;
int outputBrightness = 0;
int previousInput = 0;
int input = 0;
int ledPin = 1;
int measurePin = A1;

void setup() {
  Serial.begin(115200);
  initNetwork(); 
  
  // set the callback for received messages
  mqtt.onMessage(messageReceived);
  // subscribe to the led topic
  mqtt.subscribe(deviceId + "/led");
}

void loop() {
  // trigger the MQTT loop (keep this first!)
  loopMqtt();
  // read the current value of the potentiometer
  input = analogRead(measurePin);
  // if it has updated, enqueue a published update
  if (previousInput != input) {
    previousInput = input;
    enqueue((Msg) {deviceId + "/measurement", String(input)});
  }
    
  // set the output LED brightness 
  analogWrite(1, outputBrightness);
}

void messageReceived(String &topic, String &payload) {
  Serial.println("Incoming: " + topic + " -> " + payload);
  // Set the output brightness of the LED
  outputBrightness = payload.toInt();
  
  // Note: Do not use the mqtt directly in the callback to publish, subscribe or
  // unsubscribe as it may cause deadlocks when other things arrive while
  // sending and receiving acknowledgments. Instead, set some state, and trigger
  // make the call in loop() after calling mqtt.loop(). MessageBuffer.h is
  // provided for this purpose.
}