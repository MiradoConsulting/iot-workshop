# IoT Workshop Setup Guide #

## Setup ##

### Software to install ###

(If not already installed) you will need one of each of the following software applications installed. For each one 
there are graphical and command-line variants according to what you fancy.

#### Arduino IDE ####
- https://www.arduino.cc/en/software
  - comes in 1.8 Stable and 2.0 RC versions. I recommend 2.0
  - command line alternative: https://github.com/arduino/arduino-cli 
    - (Note that I can give no support for this option!)

#### Your favourite http client: ####
For example:
- https://curl.se/download.html - curl for command-line ninjas
- https://www.postman.com/downloads/ - Postman for GUI masters
  
#### MQTT client: ####
- http://mqtt-explorer.com/ - is a great graphical option (and what I use)
- curl works as an MQTT client too, but isn't lovely to use
- https://mosquitto.org/download/ - mosquitto includes decent command-line clients. 
  - In Ubuntu's apt at least, I had to grab a separate package called mosquitto-clients for the clients, though it seems
    to be included in the binary downloads. Your favourite package manager may vary.
    
### Getting started ###

Follow the Quickstart guide [here](https://www.arduino.cc/en/Guide/MKR1000) for getting set up with the right Arduino IDE configuration, 
and uploading and running your first sketch to the board.
- it may be that you need to have the physical board connected to complete this step...

### Installing Libraries ### 

You need the following libraries installed to create a WiFi connection and to publish/subscribe in MQTT. Find these 
libraries in the Arduino IDE, by clicking `Tools -> Library Manager` and searching for:

- `WiFi101` 0.16.1 - by Arduino
- `MQTT` 2.5.0 - by Joel Gaehwiler (there are many MQTT libraries - this one worked best for me).

Now you're ready to get cracking! 
