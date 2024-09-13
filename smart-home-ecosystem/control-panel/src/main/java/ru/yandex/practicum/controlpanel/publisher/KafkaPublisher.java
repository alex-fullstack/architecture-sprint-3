package ru.yandex.practicum.controlpanel.publisher;

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

import ru.yandex.practicum.controlpanel.event.DeviceCommandExecutedEvent;
import ru.yandex.practicum.controlpanel.event.DeviceCreatedEvent;
import ru.yandex.practicum.controlpanel.event.DeviceStatusUpdatedEvent;
import ru.yandex.practicum.controlpanel.event.DeviceUpdatedEvent;

@Component
public class KafkaPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    public void sendCreatedEvent(DeviceCreatedEvent event) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("DEVICE_CREATED_TOPIC"), event.getDevice().getId(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.info("Can't send created event [{}]", e.getMessage());
        }
    }

    public void sendUpdatedEvent(DeviceUpdatedEvent event) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("DEVICE_UPDATED_TOPIC"), event.getDevice().getId(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.info("Can't send updated event [{}]", e.getMessage());
        }
    }

    public void sendStatusUpdatedEvent(DeviceStatusUpdatedEvent event) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("DEVICE_STATUS_UPDATED_TOPIC"), event.getId(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.info("Can't send status updated event [{}]", e.getMessage());
        }
    }

    public void sendCommandExecutedEvent(DeviceCommandExecutedEvent event) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("DEVICE_COMMAND_EXECUTED_TOPIC"), event.getId(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.info("Can't send command executed event [{}]", e.getMessage());
        }
    }

    private void send(String topic, Long id, String payload) {
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", env.get("KAFKA_BOOTSTRAP_SERVERS"));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
       try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(new ProducerRecord<>(topic, id.toString(), payload));
            producer.close();
        }
    }
}