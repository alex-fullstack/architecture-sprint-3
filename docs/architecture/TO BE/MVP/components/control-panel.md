```puml
@startuml
title Control Panel Component Diagram

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
!$ICONURL = "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/v3.0.0/icons"
!include $ICONURL/common.puml
!include $ICONURL/devicons2/apachekafka_original.puml
!include $ICONURL/devicons/java.puml
!include $ICONURL/devicons/postgresql.puml

ContainerQueue(eventBroker, "Event Broker", "Apache Kafka", "Стриминг событий в системе", $sprite="apachekafka_original")
Container(gateway, "API Gateway", "Kong", "Балансировка нагрузки и маршрутизация")
Container_Boundary(deviceCtl, "Control Panel App") {
    Component(deviceController, "Device Controller", "/control-panel/api/device", "Обслуживание HTTP запросов с данными устройства")
    Component(houseController, "House Controller", "/control-panel/api/house/", "Обслуживание HTTP запросов с данными дома")
    Component(deviceService, "Device Service", "get, create, update, toggle, execute, activate, sync", "Добавление/обновление данных устройства, Выдача команд и включение/выключение устройства")
    Component(houseService, "House Service", "get, create", "Добавление/получение данных дома")
    Component(deviceRepository, "Device Repository", "device entity", "Чтение/запись данных сущности Device")
    Component(deviceStatusRepository, "Device Status Repository", "deviceStatus entity", "Чтение данных сущности DeviceStatus")
    Component(deviceTypeRepository, "Device Type Repository", "deviceType entity", "Чтение данных сущности DeviceType")
    Component(temperatureModuleRepository, "Temperature Module Repository", "temperatureModule entity", "Чтение/запись данных сущности TemperatureModule")

    Component(houseRepository, "House Repository", "house entity", "Чтение/запись данных сущности House")
    Component(houseTypeRepository, "House Type Repository", "houseType entity", "Чтение данных сущности HouseType")
 
    Component(kafkaPublisher, "Kafka Publisher", "device.created.topic device.updated.topic device.status.updated.topic device.command.executed.topic", "Публикация событий ЖЦ устройства")
    Component(kafkaSubscriber, "Kafka Subscriber", "monolith.sync.topic, sensor.bulk.created.topic sensor.bulk.create.failed.topic", "Подписка на события подсистемы telemetry")

    Rel(kafkaSubscriber, deviceService, "device usecases")
    Rel(kafkaSubscriber, deviceService, "device&monolith syncronization")
    Rel(deviceController, deviceService, "device usecases")
    Rel(houseController, houseService, "house usecases")
    Rel(deviceService, kafkaPublisher, "device events")
    BiRel(deviceService, deviceRepository, "query&save ops")
    BiRel(deviceService, temperatureModuleRepository, "query&save ops")
    Rel(deviceTypeRepository, deviceService, "query ops")
    Rel(deviceStatusRepository, deviceService, "query ops")
    BiRel(houseService, houseRepository, "query&save ops")
    Rel(houseTypeRepository, houseService, "query ops")
}
ContainerDb(deviceDB, "Device DB", "postgresql", "Данные устройств", $sprite="postgresql")

Rel(gateway, deviceController, "RESTFull API")
Rel(gateway, houseController, "RESTFull API")
Rel(kafkaPublisher, eventBroker, "Event API")
Rel(eventBroker, kafkaSubscriber, "Event API")
BiRel(deviceRepository, deviceDB, "SQL TABLE devices")
Rel(deviceDB, deviceTypeRepository, "SQL TABLE device_types")
Rel(deviceDB, deviceStatusRepository, "SQL TABLE device_statuses")
BiRel(temperatureModuleRepository, deviceDB, "SQL TABLE temperature_modules")
BiRel(houseRepository, deviceDB, "SQL TABLE houses")
Rel(deviceDB, houseTypeRepository, "SQL TABLE house_types")
@enduml
``` 