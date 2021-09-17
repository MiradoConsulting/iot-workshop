package com.mirado.iot.service;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Singleton
public class ChargepointService {
    private final static Logger LOG = LoggerFactory.getLogger(ChargepointService.class);
    private final Map<DeviceId, ChargePointId> chargePoints;
    private final Map<ChargePointId, Instant> mostRecentHeartbeats;
    private final ChargepointClient wakeupPublisher;

    public ChargepointService(ChargepointClient wakeupPublisher) {
        this.wakeupPublisher = wakeupPublisher;
        this.chargePoints = new HashMap<>();
        this.mostRecentHeartbeats = new HashMap<>();
    }

    public void registerChargepoint(DeviceId deviceId) {
        ChargePointId cpId = new ChargePointId(UUID.randomUUID());
        chargePoints.put(deviceId, cpId);
        wakeupPublisher.wakeup(cpId, deviceId);
        LOG.debug("Registering device: " + deviceId + " with chargepoint id: " + cpId);
    }

    public void heartbeat(ChargePointId chargePointId) {
        mostRecentHeartbeats.put(chargePointId, now());
    }

    public List<ChargePointId> activeChargepoints() {
        final Instant cutoff = now().minus(60, ChronoUnit.MINUTES);
        return mostRecentHeartbeats.entrySet().stream()
                .filter(e -> e.getValue().isAfter(cutoff))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
