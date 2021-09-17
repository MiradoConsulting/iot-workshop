package com.mirado.iot.model;

import java.time.Instant;
import java.util.Optional;

public record Session (SessionId sessionId,
                       Optional<ChargePointId> chargePointId,
                       Optional<Rfid> rfid,
                       Optional<Instant> startTime,
                       Optional<Instant> stopTime,
                       Optional<Long> energyDelivered) {}
