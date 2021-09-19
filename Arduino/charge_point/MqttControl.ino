/*
 * Controls for connecting and providing connection status info.
 */
#include <WiFi101.h>
#include "arduino_secrets.h"
#include "src/Queue.h"


// Network parameters
char ssid[] = SSID;
char password[] = PASS;
char mqttHost[] = MQTT_BROKER_HOST;

WiFiClient net;

// Status
int status = WL_IDLE_STATUS;

void initNetwork() {
  // Connect to WiFi
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(ssid);
    status = WiFi.begin(ssid, password);

    // Wait up to 10 seconds for connection:
    int i = 0;
    while (i < 10 && WiFi.status() != WL_CONNECTED) {
      delay(1000);
    }
  }

  Serial.println("WiFi connected");

  // Print the IP address
  IPAddress ip = WiFi.localIP();
  Serial.print("My IP Address: ");
  Serial.println(ip);

  mqtt.begin(mqttHost, net);
  connect();
}

void checkWiFi() {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi disconnected. Reconnecting...");
    initNetwork();
  }
}

void connect() {
  Serial.print("Attempting to connect to MQTT broker: ");
  Serial.println(mqttHost);
  while (!mqtt.connect("arduino", "public", "public")) {
    Serial.print(".");
    delay(1000);
  }

  Serial.println("Connected!");
}

void loopMqtt() {
  mqtt.loop();
  if (!mqtt.connected()) {
    connect();
  }
  flush();  
}

/**
 * Buffer MQTT messages for sending on the next loop. Note that a maximum
 * of 16 messages can be queued between flushes. Any messages after the 16th
 * will be dropped. 
 */
Queue<Msg> messageQueue = Queue<Msg>(16);

void enqueue(Msg m) {  
  messageQueue.push(m);
}

void flush() {
  Msg message;
  while (messageQueue.count() > 0) {
    Serial.print("Sending message: ");
    message = messageQueue.pop();
    Serial.println(message.topic + " -> " + message.body);
    mqtt.publish(message.topic, message.body);
  }
}