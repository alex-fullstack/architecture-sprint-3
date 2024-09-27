package ru.yandex.practicum.telemetry.publisher;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class KafkaPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);
    public void sendBulkCreatedEvent(String deviceId) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("SENSOR_BULK_CREATED_TOPIC"), deviceId, mapper.writeValueAsString(deviceId));
        } catch (JsonProcessingException e) {
            log.info("Can't send bulk created event [{}]", e.getMessage());
        }
    }

    public void sendBulkCreateFailedEvent(String deviceId) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("SENSOR_BULK_CREATE_FAILED_TOPIC"), deviceId, mapper.writeValueAsString(deviceId));
        } catch (JsonProcessingException e) {
            log.info("Can't send bulk create failed event [{}]", e.getMessage());
        }
    }

    public void sendBulkUpdateFailedEvent(String deviceId) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("SENSOR_BULK_UPDATE_FAILED_TOPIC"), deviceId, mapper.writeValueAsString(deviceId));
        } catch (JsonProcessingException e) {
            log.info("Can't send bulk update failed event [{}]", e.getMessage());
        }
    }

    private void send(String topic, String key, String payload) {
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", env.get("KAFKA_BOOTSTRAP_SERVERS"));
        properties.put("linger.ms", 1);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(new ProducerRecord<>(topic, key, payload));
        }
    }
}