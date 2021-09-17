package com.mirado.iot.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record SessionId(UUID value) {
    public static SessionId create() {
        return new SessionId(UUID.randomUUID());
    }

    @JsonValue
    @Override
    public UUID value() {
        return value;
    }
}
