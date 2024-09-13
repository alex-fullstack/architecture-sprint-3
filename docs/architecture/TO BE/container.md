```puml
@startuml
title SmartHome Container Diagram

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml
!$ICONURL = "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/v3.0.0/icons"
!include $ICONURL/common.puml
!include $ICONURL/devicons2/apachekafka_original.puml
!include $ICONURL/devicons/java.puml
!include $ICONURL/devicons/postgresql.puml

Person(user, "User", "Пользователь экосистемы")
System_Boundary(c1, "Экосистема Умный дом") {
    ContainerQueue(eventBroker, "Event Broker", "Apache Kafka", "Стриминг событий в системе", $sprite="apachekafka_original")
    ContainerQueue(deviceBroker, "Device Broker", "MQTT broker Mosquitto", "Шина устройств")
    Container(gateway, "API Gateway", "Kong", "Балансировка нагрузки и маршрутизация")
    Container(deviceCtl, "Control Panel App", "Панель управления устройствами", $sprite="java")
    ContainerDb(deviceDB, "Device DB", "Данные устройств", $sprite="postgresql")
    Container(users, "Users App", "Сервис пользователей", $sprite="java")
    ContainerDb(usersDB, "Users DB", "Данные пользователей", $sprite="postgresql")
    Container(deviceData, "Telemetry App", "Монитор состояния устройств", $sprite="java")
    ContainerDb(telemetryDB, "Telemetry DB", "Данные телеметрии устройств", $sprite="postgresql")
    Container(mediaServer, "Media App", "Система воспроизведения, записи и хранения данных с IP камер", $sprite="java")
}

System_Ext(HeatingDevice, "Heating Smart Device", Устройство отопления")
System_Ext(VideoRegistrator, "Video Registrator", Камера с датчиком движения")
System_Ext(LightingDevice, "Lighting Smart Device", Устройство освещения")
System_Ext(GateConroller, "Gate Controller", Устройство управления воротами")

BiRel_L(gateway, user, "Управление и мониторинг устройств умного дома по HTTP")
Rel(deviceData, gateway, "Получение данных телеметрии по HTTP")
Rel(gateway, deviceCtl, "Управление устройствами по HTTP")
Rel(mediaServer, gateway, "Получение потокового видео по HTTP")

BiRel(deviceBroker, HeatingDevice, "Обмен по протоколу MQTT")
BiRel_U(deviceBroker, LightingDevice, "Обмен по протоколу MQTT")
BiRel_R(deviceBroker, GateConroller, "Обмен по протоколу MQTT")
BiRel_L(deviceBroker, VideoRegistrator, "Обмен сообщениями по протоколу MQTT")
Rel(VideoRegistrator, mediaServer, "Медиапоток с камер по протоколу RTMP")
Rel_R(mediaServer, eventBroker, "Публикация событий Media")
Rel(deviceCtl, eventBroker, "Публикация событий Control Panel")
Rel_L(deviceBroker, eventBroker, "Публикация событий Telemetry")
Rel(eventBroker, deviceBroker, "Подписка на события Control Panel")
Rel(eventBroker, deviceData, "Подписка на события Telemetry")
Rel(eventBroker, deviceData, "Подписка на события Control Panel")
BiRel(users, usersDB, "SQL")
BiRel(deviceCtl, deviceDB, "SQL")
BiRel(deviceData, telemetryDB, "NoSQL")
Rel(users, eventBroker, "Публикация событий User")
Rel(eventBroker, deviceCtl, "Подписка на события User")
Rel(eventBroker, deviceCtl, "Подписка на события Media")
@enduml
``` 