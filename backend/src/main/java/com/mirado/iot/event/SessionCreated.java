package com.mirado.iot.event;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.Rfid;
import com.mirado.iot.model.SessionId;

public record SessionCreated(SessionId sessionId,
                             Rfid rfid,
                             ChargePointId chargePointId) implements SessionEvent {
}
