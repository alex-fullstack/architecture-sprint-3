## Экосистема Умный дом - Диаграмма контейнеров

Экосистема представляет собой информационную систему (ИС) с распределенной архитектурой, выполненной в микросервисном стиле. Каждый микросервис обслуживает одну из предметных областей в рамках контекста ИС, представленного на [диаграмме контекстов](http://127.0.0.1:8081/architecture/TO%20BE/context/). Для качественного взаимодействия микросервисов в состав ИС таже входят служебные (инфраструктурные) модули, обслуживающие потоки данных между микросервисами, а также информационный обмен с пользователями и умными устройствами IoT типа. Управление и хранение операционных данных обеспечивается СУБД Postgresql OLTP типа


```puml
@startuml

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
BiRel(gateway, users, "Регистрация и авторизация пользователей по HTTP")
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

## Cостав микросервисов

### 1 Панель управления
[Приложение на JAVA](http://127.0.0.1:8081/architecture/TO%20BE/MVP/components/control-panel/), которое обеспечивает обслуживание ЖЦ умных устройств, включая их регистрацию по протоколу HTTP, а также дистанционное командное управление по асинхронному протоколу MQTT

### 2 Телеметрия
[Приложение на JAVA](http://127.0.0.1:8081/architecture/TO%20BE/MVP/components/telemetry/) по сбору и хранению данных телеметрии датчиковой аппаратуры, включая датчики температуры, освещенности, движения и контроля состояния ворот

### 3 Пользователи
Приложение на JAVA по регистарции/авторизации пользователей и учету их личных данных

### 4 Медиасервер
Приложение на JAVA, которое обспечивает прием по протоколу RTMP видеопотока с камер с его последующей обработкой и хранением в файловой системе приложения. Также обеспечивает передачу метаданных сохраненных файлов для последующего хранения в БД Панели управления

## Cостав инфраструктуры

### Kafka
Брокер сообщений который обеспечивает асинхронный информационный обмен между микросервисами и шиной устройств на основе паттернов Domain Event и Messaging

### Mosquitto
Шина устройств, посредством которой организовано асинхронное взаимодействие ИС с умными устройствами по стандартному протоколу IoT - MQTT

### Кong
Шлюз, через который синхронные API микросервисов типа REST обслуживают запросы пользователей по протоколу HTTP в соответствии с паттерном API Gateway