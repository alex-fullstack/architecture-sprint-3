```puml
@startuml
title Telemetry Component Diagram

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
!$ICONURL = "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/v3.0.0/icons"
!include $ICONURL/common.puml
!include $ICONURL/devicons2/apachekafka_original.puml
!include $ICONURL/devicons/java.puml
!include $ICONURL/devicons/postgresql.puml

ContainerQueue(eventBroker, "Event Broker", "Apache Kafka", "Стриминг событий в системе", $sprite="apachekafka_original")
Container(gateway, "API Gateway", "Kong", "Балансировка нагрузки и маршрутизация")
Container_Boundary(deviceData, "Telemetry App") {
    Component(sensorController, "Sensor Controller", "/telemetry/api/sensor", "Обслуживание HTTP запросов с данными датчика")
    Component(sensorService, "Sensor Service", "getTelemetry, bulkCreate, bulkUpdate, updateCurrentTemperature", "Добавление/обновление данных датчиков устройства, получение данных телеметрии")
    Component(sensorRepository, "Sensor Repository", "sensor entity", "Чтение/запись данных сущности Sensor")
    Component(unitRepository, "Unit Repository", "unit entity", "Чтение данных сущности Unit")
    Component(sensorTypeRepository, "Sensor Type Repository", "sensorType entity", "Чтение данных сущности SensorType")
    Component(temperatureTelemetryRepository, "Temperature Telemetry Repository", "temperatureTelemetry entity", "Чтение/запись данных сущности TemperatureTelemetry")
 
    Component(kafkaPublisher, "Kafka Publisher", "sensor.bulk.created.topic sensor.bulk.create.failed.topic sensor.bulk.update.failed.topic", "Публикация событий ЖЦ датчиков")
    Component(kafkaSubscriber, "Kafka Subscriber", "device.created.topic device.updated.topic temperature.updated.topic", "Подписка на события подсистемы Сontrol Panel")

    Rel(kafkaSubscriber, sensorService, "sensor usecases")
    Rel(sensorController, sensorService, "sensor usecases")
    Rel(sensorService, kafkaPublisher, "sensor events")
    BiRel(sensorService, sensorRepository, "query&save ops")
    BiRel(sensorService, temperatureTelemetryRepository, "query&save ops")
    Rel(sensorTypeRepository, sensorService, "query ops")
    Rel(unitRepository, sensorService, "query ops")
}
ContainerDb(sensorDB, "Sensor DB", "postgresql", "Данные датчиков", $sprite="postgresql")

Rel(gateway, sensorController, "RESTFull API")
Rel(kafkaPublisher, eventBroker, "Event API")
Rel(eventBroker, kafkaSubscriber, "Event API")
BiRel(sensorRepository, sensorDB, "SQL TABLE sensors")
Rel(sensorDB, sensorTypeRepository, "SQL TABLE sensor_types")
Rel(sensorDB, unitRepository, "SQL TABLE units")
BiRel(temperatureTelemetryRepository, sensorDB, "SQL TABLE temperatures")
@enduml
``` 