package com.mirado.iot.kafka;

import io.micronaut.context.annotation.Value;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST;


//@Singleton
public abstract class Kafka<K, V> {
    private static final Duration CONSUMER_POLL_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);
    private final Consumer<K, V> consumer;
    private final Producer<K, V> producer;

    public Kafka(@Value("kafka.bootstrap-server") String bootstrapServers,
                 @Value("kafka.consumer.group-value") String consumerGroupId,
                 String topic,
                 Serializer<K> keySerializer, Serializer<V> valueSerializer,
                 Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {

        Map<String, Object> consumerConfig =
                Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId,
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST,
                        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        consumer = new KafkaConsumer<>(consumerConfig, keyDeserializer, valueDeserializer);
        consumer.subscribe(List.of(topic));

        Map<String, Object> producerConfig =
                Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producer = new KafkaProducer<>(producerConfig, keySerializer, valueSerializer);
    }

    @PostConstruct
    private void doPoll() {
        while (true) {
            ConsumerRecords<K, V> records = consumer.poll(CONSUMER_POLL_TIMEOUT);
            records.forEach(rec -> consume(rec.key(), rec.value()));
        }
    }

    protected abstract void consume(K key, V value);
}
