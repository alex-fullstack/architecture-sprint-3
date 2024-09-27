package ru.yandex.practicum.smarthome.subscriber;

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
import ru.yandex.practicum.smarthome.command.SetTargetTemperature;
import ru.yandex.practicum.smarthome.event.DeviceCommandExecutedEvent;
import ru.yandex.practicum.smarthome.event.DeviceStatusUpdatedEvent;
import ru.yandex.practicum.smarthome.service.HeatingSystemService;

@Component
@RequiredArgsConstructor
public class KafkaSubscriber {
    private final HeatingSystemService heatingSystemService;
    private static final Logger log = LoggerFactory.getLogger(KafkaSubscriber.class);

    @KafkaListener(groupId="${spring.kafka.group}", topics = "#{'${device.status.updated.topic},${device.command.executed.topic}'.split(',')}")
    public void listen(ConsumerRecord<String, String> message)
    {
        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        var topic = message.topic();
        Map<String, String> env = System.getenv();
        if (topic.equals(env.get("DEVICE_STATUS_UPDATED_TOPIC"))) {     
            try {
                DeviceStatusUpdatedEvent event = mapper.readValue(message.value(), DeviceStatusUpdatedEvent.class);
                var id = event.getMonolithId();
                if (id != null) {
                    switch (event.getCode()) {
                        case "on" -> heatingSystemService.turnOn(id);
                        default -> heatingSystemService.turnOff(id);
                    }
                log.info("Syncronized!");
                }
                
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't sync [{}]", e.getMessage());
            }      
        } else if (topic.equals(env.get("DEVICE_COMMAND_EXECUTED_TOPIC"))) {
            try {
                DeviceCommandExecutedEvent event = mapper.readValue(message.value(), DeviceCommandExecutedEvent.class);
                var id = event.getMonolithId();
                if (id != null) {
                    var command = event.getCommand();
                    var code = command.getCode();
                    if (code.equals("set-temperature")) {
                        SetTargetTemperature cmdParameter = mapper.readValue(mapper.writeValueAsString(command.getParameter()), SetTargetTemperature.class);
                        log.info(cmdParameter.toString());
                        heatingSystemService.setTargetTemperature(id, cmdParameter.getTarget());
                        log.info("Syncronized!");
                    }                
                }
                
            } catch (JsonProcessingException|RuntimeException e) {
                log.info("Can't sync executed command [{}]", e.getMessage());
            }      
        }
          
    }
}

