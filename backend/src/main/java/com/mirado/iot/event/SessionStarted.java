package com.mirado.iot.event;

import com.mirado.iot.model.SessionId;

import java.time.Instant;

public record SessionStarted (SessionId sessionId, Instant timestamp) implements SessionEvent {}
