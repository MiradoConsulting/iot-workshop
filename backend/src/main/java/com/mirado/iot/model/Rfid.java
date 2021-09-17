package com.mirado.iot.model;

import com.fasterxml.jackson.annotation.JsonValue;

public record Rfid (String value) {
    @JsonValue
    @Override
    public String value() {
        return value;
    }
}
