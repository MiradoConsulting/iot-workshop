package com.mirado.iot.mqtt;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.DeviceId;
import com.mirado.iot.model.SessionId;
import com.mirado.iot.service.ChargepointClient;
import io.micronaut.mqtt.annotation.Topic;
import io.micronaut.mqtt.v5.annotation.MqttPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Singleton
public class MqttChargepointClient implements ChargepointClient {
    private static final Logger LOG = LoggerFactory.getLogger(MqttChargepointClient.class);
    private final Sender publisher;

    public MqttChargepointClient(Sender publisher) {
        this.publisher = publisher;
    }

    @MqttPublisher
    public interface Sender {
        CompletableFuture send(@Topic String topic, UUID body);
    }

    private void blockingSend(String topic, UUID body) {
        // workaround for a bug in the Micronaut MQTT impl causing all message sends to deadlock while waiting for an
        // ack from the broker (even when the qos is set to 0). The message is still sent successfully, so timing out
        // and swallowing the exception will have to be good enough for now...
        try {
            publisher.send(topic, body).get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // do nothing
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void wakeup(ChargePointId chargepointId, DeviceId deviceId) {
        LOG.debug("Sending wakeup to " + deviceId + "/" + chargepointId);
        blockingSend("wakeup/" + deviceId.value(), chargepointId.value());
    }

    @Override
    public void start(ChargePointId chargePointId, SessionId sessionId) {
        blockingSend("chargepoint/" + chargePointId.value() + "/session/start", sessionId.value());
    }

    @Override
    public void stop(ChargePointId chargePointId, SessionId sessionId) {
        blockingSend("chargepoint/" + chargePointId.value() + "/session/stop", sessionId.value());
    }

    @Override
    public void emergencyStop(ChargePointId chargePointId) {
        blockingSend("chargepoint/" + chargePointId.value() + "/stop", chargePointId.value());
    }
}
