package com.mirado.iot.mqtt;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.DeviceId;
import com.mirado.iot.model.Rfid;
import com.mirado.iot.model.SessionId;
import com.mirado.iot.service.ChargepointService;
import com.mirado.iot.service.SessionService;
import io.micronaut.context.annotation.Context;
import io.micronaut.mqtt.annotation.MqttSubscriber;
import io.micronaut.mqtt.annotation.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Context
@MqttSubscriber
public class MqttListener {
    private static final Logger LOG = LoggerFactory.getLogger(MqttListener.class);
    private static final String UUID_PATTERN = "([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})";
    private static final Pattern HEARTBEAT_PATTERN =
            Pattern.compile("heartbeat\\/" + UUID_PATTERN);
    private static final Pattern AUTH_START_PATTERN =
            Pattern.compile("auth-session\\/" + UUID_PATTERN);
    private static final Pattern AUTH_STOP_PATTERN =
            Pattern.compile("auth-stop\\/" + UUID_PATTERN);

//    private final IMqttClient mqtt;
    private final ChargepointService chargepointService;
    private final SessionService sessionService;

    public MqttListener(ChargepointService chargepointService, SessionService sessionService) {
        this.chargepointService = chargepointService;
        this.sessionService = sessionService;
//        mqtt = new MqttClient("tcp://localhost:1883", UUID.randomUUID().toString());
//        mqtt.connect();
//        mqtt.subscribe("register", (topic, msg) ->
//                register(UUID.fromString(new String(msg.getPayload()))));
//        mqtt.subscribe("heartbeat/+", (topic, msg) ->
//                heartbeat(topic, msg.getPayload()));
//        mqtt.subscribe("auth-session/+", (topic, msg) ->
//                authSession(topic, new String(msg.getPayload())));
//        mqtt.subscribe("auth-stop/+", (topic, msg) ->
//                authStop(topic, new String(msg.getPayload())));
    }

    @Topic("register")
    public void register(UUID deviceId) {
        chargepointService.registerChargepoint(new DeviceId(deviceId));
    }

    @Topic("heartbeat/#")
    public void heartbeat(String topic, byte[] body) {
        final Matcher m = HEARTBEAT_PATTERN.matcher(topic);
        if (m.matches()) {
            chargepointService.heartbeat(new ChargePointId(UUID.fromString(m.group(1))));
        } else {
            LOG.warn("Could not parse charge point id from heartbeat message: " + topic);
        }
    }

    @Topic("auth-session/#")
    public void authSession(String topic, String rawRfid) {
        final Matcher m = AUTH_START_PATTERN.matcher(topic);
        if (m.matches()) {
            ChargePointId cpid = new ChargePointId(UUID.fromString(m.group(1)));
            Rfid rfid = new Rfid(rawRfid);
            sessionService.authSession(cpid, rfid);
        } else {
            LOG.warn("Could not parse auth-session request: {} rfid: {}", topic, rawRfid);
        }
    }

    @Topic("auth-stop/#")
    public void authStop(String topic, String rawRfid) {
        final Matcher m = AUTH_STOP_PATTERN.matcher(topic);
        if (m.matches()) {
            SessionId sessionId = new SessionId(UUID.fromString(m.group(1)));
            Rfid rfid = new Rfid(rawRfid);
            sessionService.authStop(sessionId, rfid);
        } else {
            LOG.warn("Could not parse auth-stop request: {} rfid: {}", topic, rawRfid);
        }
    }
}
