package ru.yandex.practicum.controlpanel.subscriber;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.controlpanel.event.SyncEvent;
import ru.yandex.practicum.controlpanel.service.DeviceService;

@Component
@RequiredArgsConstructor
public class KafkaSubscriber {
    private final DeviceService deviceService;
    private static final Logger log = LoggerFactory.getLogger(KafkaSubscriber.class);

    @KafkaListener(groupId="${spring.kafka.group}", topics = "#{'${monolith.sync.topic},${sensor.bulk.created.topic},${sensor.bulk.create.failed.topic}'.split(',')}")
    public void listen(ConsumerRecord<String, String> message)
    {
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        var topic = message.topic();
        Map<String, String> env = System.getenv();
        if (topic.equals(env.get("MONOLITH_SYNC_TOPIC"))) {     
            try {
                SyncEvent event = mapper.readValue(message.value(), SyncEvent.class);
                deviceService.sync(event);
                log.info("Syncronized!");
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't sync [{}]", e.getMessage());
            }
        } else if (topic.equals(env.get("SENSOR_BULK_CREATED_TOPIC"))) {
            try {
                String deviceId = mapper.readValue(message.value(), String.class);
                deviceService.activate(Long.valueOf(deviceId));
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't sync [{}]", e.getMessage());
            }
            
        } else if (topic.equals(env.get("SENSOR_BULK_CREATE_FAILED_TOPIC"))) {
            // TODO: rollback device creation
        }
    }
}