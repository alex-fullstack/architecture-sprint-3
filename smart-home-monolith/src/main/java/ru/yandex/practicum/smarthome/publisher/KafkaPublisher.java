package ru.yandex.practicum.smarthome.publisher;

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

import ru.yandex.practicum.smarthome.event.SyncEvent;

@Component
public class KafkaPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);

    public void sendSyncEvent(SyncEvent event) {
        Map<String, String> env = System.getenv();
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            send(env.get("MONOLITH_SYNC_TOPIC"), "sync", mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.info("Can't send sync event [{}]", e.getMessage());
        }
    }

    private void send(String topic, String key, String payload) {
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", env.get("KAFKA_BOOTSTRAP_SERVERS"));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
       try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(new ProducerRecord<>(topic, key, payload));
            producer.close();
        }
    }
}