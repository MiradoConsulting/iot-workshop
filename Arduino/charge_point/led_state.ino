/**
 * Manages state for the RGB LED.
 */

bool update = false;
bool flashOn = false;
int flashReps = 0;
unsigned long flashPeriod = 0;
int redBrightness = 0;
int greenBrightness = 0;
int blueBrightness = 0;
int redPin = 5; 
int greenPin = 4; 
int bluePin = 3; 
unsigned long until = -1;

void ledSetup() {
  pinMode(redPin, OUTPUT); 
  pinMode(greenPin, OUTPUT); 
  pinMode(bluePin, OUTPUT); 
}

void ledLoop() {
  if (!update && flashReps > 0 && millis() > until) {
    if (flashOn) {
      analogWrite(redPin, 0);
      analogWrite(greenPin, 0);
      analogWrite(bluePin, 0);
      flashOn = false;
      until = millis() + flashPeriod;
    } else {
      flashReps --;
      analogWrite(redPin, redBrightness);
      analogWrite(greenPin, greenBrightness);
      analogWrite(bluePin, blueBrightness);    
      flashOn = true;
      until = millis() + flashPeriod;
    }
    
  } else {
    if (update) {
      //Serial.println("Over");
      analogWrite(redPin, redBrightness);
      analogWrite(greenPin, greenBrightness);
      analogWrite(bluePin, blueBrightness);    
      update = false;
    }
    if (millis() > until) {
      setColour(0, 0, 0);
    }
  }
}

void setColour(int red, int green, int blue) {
  setColour(red, green, blue, -1);
}

void setColour(int red, int green, int blue, unsigned long durationMillis) {
  redBrightness = red;
  greenBrightness = green;
  blueBrightness = blue;
  until = millis() + durationMillis;
  update = true;
  flashReps = 0;
}

void setFlashingColour(int red, int green, int blue, unsigned long period, int reps) {
  redBrightness = red;
  greenBrightness = green;
  blueBrightness = blue;
  until = 0;
  update = false;
  flashOn = false;  
  flashReps = reps;
  flashPeriod = period;
}