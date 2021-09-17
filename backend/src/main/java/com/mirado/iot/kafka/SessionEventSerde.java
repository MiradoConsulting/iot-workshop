package com.mirado.iot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mirado.iot.event.SessionEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import javax.inject.Singleton;

@Singleton
class SessionEventSerde implements Serde<SessionEvent> {
    private final ObjectMapper objectMapper;

    public SessionEventSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<SessionEvent> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsString(data).getBytes();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<SessionEvent> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(new String(data), SessionEvent.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
