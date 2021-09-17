package com.mirado.iot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mirado.iot.model.SessionId;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import javax.inject.Singleton;

@Singleton
class SessionIdSerde implements Serde<SessionId> {
    private final ObjectMapper objectMapper;

    public SessionIdSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Serializer<SessionId> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsString(data).getBytes();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<SessionId> deserializer() {
        return (topic, data) -> {
            try {
                return objectMapper.readValue(new String(data), SessionId.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
