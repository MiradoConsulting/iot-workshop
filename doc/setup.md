# IoT Workshop Setup Guide #

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
