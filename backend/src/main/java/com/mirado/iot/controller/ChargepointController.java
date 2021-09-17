package com.mirado.iot.controller;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.DeviceId;
import com.mirado.iot.mqtt.MqttChargepointClient;
import com.mirado.iot.service.AuthService;
import com.mirado.iot.service.ChargepointService;
import com.mirado.iot.service.SessionService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import java.util.List;
import java.util.UUID;

@Controller
public class ChargepointController {
    private final ChargepointService chargepointService;
    private final AuthService authService;
    private final SessionService sessionService;
    private final MqttChargepointClient chargepointClient;

    public ChargepointController(ChargepointService chargepointService, AuthService authService, SessionService sessionService, MqttChargepointClient chargepointClient) {
        this.chargepointService = chargepointService;
        this.authService = authService;
        this.sessionService = sessionService;
        this.chargepointClient = chargepointClient;
    }

    @Get("/chargepoints/active")
    public List<ChargePointId> activeChargepoints() {
        return chargepointService.activeChargepoints();
    }
}
