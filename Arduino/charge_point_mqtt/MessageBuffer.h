#include <Arduino.h>

struct Msg {
  String topic;
  String body;
} ;

void enqueue(Msg msg);

void flush();
