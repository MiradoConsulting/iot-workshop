package com.mirado.iot.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record ChargePointId (UUID value) {
    @JsonValue
    @Override
    public UUID value() {
        return value;
    }
}
