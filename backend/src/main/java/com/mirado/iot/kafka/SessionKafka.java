package com.mirado.iot.kafka;

import com.mirado.iot.event.SessionEvent;
import com.mirado.iot.model.SessionId;
import com.mirado.iot.service.SessionService;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;

@Singleton
public class SessionKafka extends Kafka<SessionId, SessionEvent> {
    private final SessionService sessionService;

    public SessionKafka(@Value("kafka.bootstrap-server") String bootstrapServers,
                        @Value("kafka.consumer.group-value") String consumerGroupId,
                        @Value("kafka.topic.session") String topic,
                        SessionEventSerde sessionEventSerde,
                        SessionIdSerde sessionIdSerde,
                        SessionService repository) {
        super(bootstrapServers, consumerGroupId, topic,
                sessionIdSerde.serializer(), sessionEventSerde.serializer(),
                sessionIdSerde.deserializer(), sessionEventSerde.deserializer());
        this.sessionService = repository;
    }

    @Override
    protected void consume(SessionId key, SessionEvent value) {
        sessionService.put(value);
    }

}
