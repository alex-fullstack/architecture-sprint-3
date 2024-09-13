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
Container_Boundary(smartHomeSystem, "Smart Home Monolith App") {
    Component(heatingSystemController, "Heating System Controller", "/monolith/api/heating/sync", "Обслуживание HTTP запросов на синхронизацию с экосистемой умного дома")
    Component(heatingSystemService, "Heating System Service", "sync", "Выгрузка данных для синхронизации")
    Component(heatingSystemRepository, "Heating System Repository", "heatingSystem entity", "Чтение/запись данных сущности HeatingSystem")
 
    Component(kafkaPublisher, "Kafka Publisher", "monolith.sync.topic", "Публикация событий синхронизации")
    Component(kafkaSubscriber, "Kafka Subscriber", "device.status.updated.topic device.command.executed.topic", "Подписка на события control panel")

    Rel(kafkaSubscriber, heatingSystemService, "heatingSystem&Control Panel syncronization")
    Rel(heatingSystemController, heatingSystemService, "heatingSystem&Control Panel syncronization")
    Rel(heatingSystemService, kafkaPublisher, "heatingSystem&Control Panel syncronization")
    BiRel(heatingSystemService, heatingSystemRepository, "query&save ops")
}
ContainerDb(monolithDB, "Smart Home Monolith DB", "postgresql", "Данные умного дома", $sprite="postgresql")

Rel(gateway, heatingSystemController, "RESTFull API")
Rel(kafkaPublisher, eventBroker, "Event API")
Rel(eventBroker, kafkaSubscriber, "Event API")
BiRel(heatingSystemRepository, monolithDB, "SQL TABLE heating_systems")
@enduml
``` 