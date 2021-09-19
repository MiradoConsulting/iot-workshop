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
#include <SPI.h>
#include "src/RFID.h"

#define RFID_SS_PIN 7
#define RFID_RST_PIN 6

#define DEVICE_ID "e9d3216f-01cd-4ff6-84d5-bc0a4d30ec1b"

RFID rfid(RFID_SS_PIN, RFID_RST_PIN);
MQTTClient mqtt;

String readRfid;

void setup() {
  Serial.begin(115200);
  initNetwork(); 
  SPI.begin();
  rfid.init();  
  ledSetup();  
  
  mqtt.onMessage(messageReceived);
  mqtt.subscribe("led");

  initChargepoint();
}

void loop() {
  loopMqtt();
  rfidLoop();
  ledLoop();
}

void messageReceived(String &topic, String &payload) {
  Serial.println("Incoming: " + topic + " -> " + payload);
  if (topic.equals("led")) {
    int red = payload.substring(0,3).toInt();
    int green = payload.substring(4,7).toInt();
    int blue = payload.substring(8, 11).toInt();
    int period = payload.substring(12, 16).toInt();
    int reps = payload.substring(17).toInt();
    setFlashingColour(red, green, blue, period, reps);
  }
  if (topic.startsWith("wakeup/")) {
    wakeup(payload);
  } else if (topic.startsWith("chargepoint/")) {
    if (topic.endsWith("session/start")) {
      start(payload);
    } else if (topic.endsWith("session/stop")) {
      stop(payload);
    } else if (topic.endsWith("stop")) {
      emergencyStop();
    }
  }
}

void rfidLoop() {
  if (rfid.isCard()) {
    if (rfid.readCardSerial()) {
      Serial.print("RFID tag read: ");
      readRfid = "";
      for (uint i=0; i<sizeof(rfid.serNum); i++) {
        readRfid = readRfid + String(rfid.serNum[i], HEX);
      }      
      Serial.println(readRfid);            
      String uuid = DEVICE_ID;
      enqueue({"rfid/" + uuid, readRfid});
    }
    rfid.halt();
  }
}