
# Charge Point Implementation Guide #

## Setup ##

### Software ###

(If not already installed) you will need one of each of the following software applications installed. For each one 
there are graphical and command-line variants according to what you fancy.

#### Arduino IDE ####
- https://www.arduino.cc/en/software
  - comes in 1.8 Stable and 2.0 Beta versions. 2.0 makes some significant improvements on 1.8. I've done the prep in 2.0.
  - (alternative if you prefer command line): https://github.com/arduino/arduino-cli 
    - (Note that I can give no support for this option!)

#### Your favourite http client: ####
- https://curl.se/download.html - curl for command-line ninjas
- https://www.postman.com/downloads/ - Postman for GUI masters
  
#### MQTT client: ####
- http://mqtt-explorer.com/ - is a great graphical option (and what I use)
- curl works as an MQTT client too, but isn't lovely to use
- https://mosquitto.org/download/ - mosquitto includes decent command-line clients. 
  - In Ubuntu's apt at least, I had to grab a separate package called mosquitto-clients for the clients, though it seems
    to be included in the binary downloads. Your favourite package manager may vary.
    
### Hardware ###

#### The board - [Arduino MKR 1000 WiFi](https://store.arduino.cc/arduino-mkr1000-wifi) ####

This version of the Arduino includes an onboard wireless radio chip, capable of working with WiFi and Bluetooth. In this
workshop we'll be using the WiFi capabilities for internet connectivity.

#### The kit ####

Comes with a full assortment of electronic components. A full list is on the inside of the lid of the box. Following 
this workshop guide we'll use the following, but don't feel obliged to stick to it rigidly!: 

- LEDs (either RGB or separate LEDS in red, yellow, green, blue)
- Resistors (for wiring in series with LEDs)
- Servo motor 

#### MFRC522 RFID Reader/Writer ####

The RFID card reader - implements a serial comms protocol called SPID to read data off the rfid card and send it to the 
Arduino. The wiring is a bit complex - details are in the section on setting up the RFID.

### Getting started ###

Follow the Quickstart guide [here](https://www.arduino.cc/en/Guide/MKR1000) for getting set up with the right IDE configuration, 
and uploading and running your first sketch to the board.

### Installing Libraries ### 

You need the following libraries installed to create a WiFi connection and to publish/subscribe in MQTT. Find these 
libraries in the IDE, by clicking `Tools -> Library Manager` and searching for:

- `WiFi101` 0.16.1 - by Arduino
- `MQTT` 2.5.0 - by Joel Gaehwiler (there are many MQTT libraries - this one worked best for me).

Now you're ready to get cracking on the challenges! 

## Challenges ##

Feel free to take this material any way you like. There's far more to do than is likely to get done in the 2 hours we 
have here (unless you're very familiar with Arduino development already). Some steps focus more on the hardware hacking 
side of things, others on getting stuff working with MQTT, and developing the implementation of the protocol. Feel free 
to spend more time on whatever you find more interesting!

### 1. Get an LED blinking ###

You may have got this working already in [Getting Started](#Getting started). Wire up the LED (with a 2.2KOhm resistor).
Understand the code and upload the `Blink` sketch (`File -> Examples -> Basics -> Blink`). Make sure you understand 
what's going on by trying these:

- play with the timing of the blinking LED
- print something to the Serial connection (`Serial.println(...)`) and watch it in the Serial Monitor in the IDE.
- can you wire in two LEDs on different pins and have them do different things?

### 2. Control the LED brightness over MQTT ###

Open `mqtt`. Have a read through of the code in charge_point_mqtt.ino. 

Connect your MQTT client to the broker [like this](iot-mqtt-broker-settings.png):
    protocol:   either mqtt:// or tcp://
    host:       13.53.227.93
    port:       1883
    no username, password, or encryption.
    
Publish an update to set the brightness of the LED:
  topic: <device_id>/led
  payload: any integer 0 <= n <= 255
    
Add a potentiometer into your circuit (similar to [this image](https://hackster.imgix.net/uploads/attachments/1144704/experimental_schematic_diagram_rWMevA8n2K.jpg)).
The input pin from the potentiometer and the output pin to the LED should match those defined in the program.

Open up `arduino_secrets.h`, and enter your SSID and password. We might need to set up access points for this. Enter the 
MQTT host ip (`"13.53.227.93"`). In `charge_point_mqtt.ino`, set the device id to some random [UUID](https://www.uuidgenerator.net), to 
make messages to/from your device easy to find. 

Upload the sketch and test it out. Do you understand what the code is doing?

#### Challenges ####

- See if you can use MQTT-Explorer (or your MQTT client of choice) to 
  - post updates to the device to control the brightness of the LED
  - receive updates on the current state of the potentiometer
- Notice that the input from the potentiometer is very jittery. How might you smooth this out to get closer to seeing 
only one update for each user action? There are both hardware and software solutions - but this is a software workshop ;)
  
### 3. RFID Reader ###

Open the `mfrc522` sketch. Add the VMA405 RFID Reader device to your breadboard, and wire up the pins. The Arduino's 
SPID pins are named, but the pin numbers are given in the comment at the top of `mfrc522.ino` for convenience.

Upload the sketch, open the serial monitor, and test with the RFID tag. You should get the access denied signal, and a 
printout in the serial monitor of your card's serial number. Update `cards[]` in the sketch with your card's serial 
number to "authorise" your card, reupload the sketch, and run it again. This time you should see the accepted signal. 

#### Challenges ####

- Try to combine the RFID reader into your `charge_point_mqtt` sketch:
  - Copy `RFID.cpp` and `RFID.h` directly. 
  - Adapt the code from `mfrc522.ino` to fit into your mqtt program.
  - Can you publish an mqtt message with the RFID serial number as a kind of auth request?
  - Can you subscribe to an auth response topic, and trigger the "unlock" behaviour in response to that message?
  
### 4. Charge Point Implementation ###

Here I lay out the behaviour of the physical charge point device. I leave the wiring and implementation up to you. 
You can take the steps in more or less any order. For each of the steps, I suggest implementing the hardware,
and then the integration with the backend, before moving onto the next steps.

#### Behaviour ####

##### LEDs: #####

The chargepoint displays 4 coloured lights (can be either one light that changes colour, or 4 different lights):
  - RED - when there is a fault (e.g. no network, communication failure with backend)
  - GREEN - when ready to start charging
  - BLUE - when charging is ongoing
  - YELLOW - flashes on and off when the rfid is not authorised

##### Servo motor: #####

The chargepoint locks the cable into place while charging is going on. To represent this:

- Turn the servo motor 90° clockwise when a session starts
- Turn the servo motor 90° counter-clockwise when a session stops

Take care not to try turning the servo in the wrong direction, if, e.g. you receive two start messages in a row.

##### RFID #####

The user can blip with their RFID tag to authorise a session start or stop. Once the authorisation is done, the session 
immediately starts/stops.

#### IoT Charge Point Implementation ####

Implement the chargepoint side of the MEChI protocol, and test and run it against the test backend.

Suggested implementation steps (which can be taken in any order):

1. Publish `register` request with device id on setup.
2. Subscribe to `wakeup` response to receive `chargepoint id` and save it.
3. Implement `auth-start` - sending an auth request when a user blips the RFID.
   - can you make this send only once when a user blips, so the backend doesn't receive hundreds of requests every time 
  the user blips?
4. Subscribe to receive `start` request, and start a charging session when it arrives.
5. Implement `auth-stop`.
6. Subscribe to `stop` and stop a charging session when it arrives. 
