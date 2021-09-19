
#define FAULT 0
#define READY 1
#define CHARGING 2


int currentState = FAULT;
unsigned long lastHeartbeat = 0;
String deviceId = DEVICE_ID;
String chargepointId = "";
String ongoingSessionId = "";

void setState(int state) {
  currentState = state;
  if (state == FAULT) {
    setColour(255, 0, 0);
  } else if (state == READY) {
    setColour(0, 255, 0);
  } else if (state == CHARGING) {
    setColour(0, 0, 255);
  }
}

void initChargepoint() {
  // register the charge point with the backend
  enqueue({"register", deviceId});
  mqtt.subscribe("wakeup/" + deviceId);
  currentState = FAULT;
}

void wakeup(String cpId) {
  // set the device id 
  // subscribe to updates for the device
  // transition to the ready state
  Serial.println("Received wakeup req: " + cpId);
  chargepointId = cpId;
  mqtt.subscribe("chargepoint/" + cpId + "/#");
  currentState = READY;
}

void start(String sessionId) {
  // check the state of the device and if possible, start a session
  Serial.println("Received start req: " + sessionId);
  currentState = CHARGING;
  ongoingSessionId = sessionId;  
}

void stop(String sessionId) {
  // check the state of the device, and if possible, stop the session
  // i.e. release the cable and update the chargepoint's state
  Serial.println("Received stop req: " + sessionId);
  if (sessionId.equals(ongoingSessionId)) {
    currentState = READY;
    ongoingSessionId = "";
  } else {
    Serial.println("Igoring stop request for session which is not ongoing: " + sessionId);
  }
}

void emergencyStop() {
  // stop charging i.e. release the cable and update the chargepoint's 
  // state. 
  Serial.println("Received emergency stop req.");
  currentState = READY;
  ongoingSessionId = "";
}

void chargepointLoop() {
  // check for incoming messages and trigger proper behaviour
  // check whether it is time to send a heartbeat and send it if so
  // get input from the RFID reader and trigger authorisation of start/stop
}