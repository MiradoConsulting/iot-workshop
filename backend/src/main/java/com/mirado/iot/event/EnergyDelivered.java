package com.mirado.iot.event;

import com.mirado.iot.model.SessionId;

import java.time.Instant;

public record EnergyDelivered (SessionId sessionId, Instant timestamp, long qty) implements SessionEvent {}
