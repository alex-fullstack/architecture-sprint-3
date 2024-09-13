package ru.yandex.practicum.telemetry.subscriber;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.telemetry.event.CreatedDeviceEvent;
import ru.yandex.practicum.telemetry.event.TemperatureUpdatedEvent;
import ru.yandex.practicum.telemetry.event.UpdatedDeviceEvent;
import ru.yandex.practicum.telemetry.service.SensorService;

@Component
@RequiredArgsConstructor
public class KafkaSubscriber {
    private final SensorService sensorService;
    private static final Logger log = LoggerFactory.getLogger(KafkaSubscriber.class);

    @KafkaListener(groupId="${spring.kafka.group}", topics = "#{'${device.created.topic},${device.updated.topic},${temperature.updated.topic}'.split(',')}")
    public void listen(ConsumerRecord<String, String> message)
    {
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        Map<String, String> env = System.getenv();
        var topic = message.topic();
        if (topic.equals(env.get("DEVICE_CREATED_TOPIC"))) { 
            try {
                CreatedDeviceEvent event = mapper.readValue(message.value(), CreatedDeviceEvent.class);
                var sensors = event.getSensors();
                if (!sensors.isEmpty()) {
                    sensorService.bulkCreate(event.getDevice().getId(), sensors);
                }
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't process message [{}]", e.getMessage());
                sensorService.bulkCreateRollback(message.key());
            }
        }else if (topic.equals(env.get("DEVICE_UPDATED_TOPIC"))) { 
            try {
                UpdatedDeviceEvent event = mapper.readValue(message.value(), UpdatedDeviceEvent.class);
                var sensors = event.getSensors();
                if (!sensors.isEmpty()) {
                    sensorService.bulkUpdate(event.getDevice().getId(), sensors);
                }
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't process message [{}]", e.getMessage());
                sensorService.bulkUpdateRollback(message.key());
            }
            
        } else if (topic.equals(env.get("TEMPERATURE_UPDATED_TOPIC"))) {
            try {
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                TemperatureUpdatedEvent event = mapper.readValue(message.value(), TemperatureUpdatedEvent.class);
                sensorService.updateCurrentTemperature(event);
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't process message [{}]", e.getMessage());
            }
        }
        
    }
}