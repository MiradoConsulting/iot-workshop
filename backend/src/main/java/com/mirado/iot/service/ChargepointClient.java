package com.mirado.iot.service;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.DeviceId;
import com.mirado.iot.model.SessionId;

public interface ChargepointClient {
    void wakeup(ChargePointId chargepointId, DeviceId deviceId);

    void start(ChargePointId chargePointId, SessionId sessionId);

    void stop(ChargePointId cpid, SessionId sessionId);

    void emergencyStop(ChargePointId cpid);
}
