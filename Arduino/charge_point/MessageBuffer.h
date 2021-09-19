#include <Arduino.h>

struct Msg {
  String topic;
  String body;
} ;

/**
 * Buffer MQTT messages for sending on the next loop. Note that a maximum
 * of 16 messages can be queued between flushes. Any messages after the 16th
 * will be dropped. 
 */
void enqueue(Msg msg);

void flush();
