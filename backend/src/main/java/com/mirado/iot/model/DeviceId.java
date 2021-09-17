package com.mirado.iot.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record DeviceId(UUID value) {
    @JsonValue
    public UUID value() {
        return value;
    }
}
