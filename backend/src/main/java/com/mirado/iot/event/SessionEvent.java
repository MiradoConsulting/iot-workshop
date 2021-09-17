package com.mirado.iot.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mirado.iot.model.SessionId;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SessionStarted.class, name = "SessionStarted"),
        @JsonSubTypes.Type(value = SessionStopped.class, name = "SessionStopped"),
        @JsonSubTypes.Type(value = EnergyDelivered.class, name = "EnergyDelivered")})
public interface SessionEvent {
    SessionId sessionId();
}
