# MEChI - Mirado EV Charging Interface # 

For communicating between Chargepoints and charge-point management platforms or clients.

Built on MQTT. Charge point subscribes to the following topics and implements responses with specified behaviours to each of the following.

Two sides of the protocol - Platform -> CP, and CP -> Platform.

=============================================

## Platform -> CP ##

### Wakeup ###
 
- Topic: `wakeup/{device-id}`
- Body: chargepoint id (UUID)

A command to the device to adopt a new chargepoint id. All future comms from the backend to the chargepoint will use this chargepoint id.

### Start a charging session ### 

- Topic: `chargepoint/{chargepoint-id}/session/start` 
- Body:  session id (UUID)

Remotely start a new session with the given session id. May be ignored if a session is currently ongoing, or no vehicle is plugged in, or the chargepoint is not in a state to start.

### Stop a charging session ###

- Topic: `chargepoint/{chargepoint-id}/session/stop`
- Body: session id (UUID)

Remotely stop an ongoing session with the given session id. May be ignored if the given session is not ongoing. 

### Emergency stop charging ### 

- Topic: `chargepoint/{chargepoint-id}/stop`
- Body: `{}`

Stop charging (if currently charging) and release the cable, if one is plugged in. Intended to be used in emergencies, to release a stuck car. Can result in the chargepoint and the server getting into a bad state. 

=============================================

## CP -> Platform ##

### Register ###

- Topic: `register`
- Body: device id (UUID)

This message is how the device registers itself as a chargepoint with the backend. The backend will respond with a wakeup message, informing the device of its assigned chargepoint id. 

### Hearthbeat ###  

- Topic: `heartbeat/{chargepoint-id}`
- Body: `{}`

This message is how the chargepoint informs the platform/client that the chargepoint is online. A heartbeat should be sent once every 60 seconds. Any chargepoint without a heartbeat more recent than 60 seconds is considered offline by the platform/client.

### Authorise session ### 

- Topic: `auth-session/{chargepoint-id}`
- Body: rfid (hexadecimal string)

Request authorisation to start a new session. If the rfid is authorised to start a session, then a start request will follow from the platform/client to the chargepoint. You should only send this message once for a given attempt by the user to authorise - every request sent to the server is liable to result in a start request sent back to the device! 

### Authorise stop ###

- Topic: `auth-stop/{session-id}`
- Body: rfid (hexadecimal string)

Request authorisation to stop an ongoing session. If the rfid is authorised to stop the ongoing session, then a stop requset will follow from the platform/client to the chargepoint.

=============================================

Implementation notes:

Each chargepoint has 2 ids:
- device id: 
    - a UUID managed by the chargepoint itself. In practice, this probably involves you generating a random UUID from https://www.uuidgenerator.net/ and including it as the device id as a constant in the sketch. 
    - associated with the physical hardware, and doesn't change
- chargepoint id:
    - a UUID "managed" by the platform/server. This is obtained by the device posting a register message and receiving the wakeup response from the server.
    - it's up to you how often to reregister your chargepoint - can be every time you restart the device, or you could have some sort of manual control for it to do it more often
    
The backend considers itself the authority on sessions - the session starts at the moment the backend sends the start signal, and stops when the backend sends the stop.

Note that this protocol is _terrible_. It's built mostly to be simple to implement for both backend and devices, with no thought for robustness or real-world usability. You will probably find many times the backend and device getting out of sync. To resolve these sorts of problems, your only recourse for now is to reset your device with a new id. 

The backend makes no attempt to manage or enforce state. It will let you do things like start or stop a session as many times as you like, without any coherent policy on what it will do with such things. Maybe the newest event will win, maybe oldest, maybe one will be picked arbitrarily. 

In addition to this MEChI integration, there is also a RESTish interface for web/mobile applications. Its swagger is available at https://ec2-13-53-227-93.eu-north-1.compute.amazonaws.com:8080/swagger-ui






