```puml
@startuml
title SmartHome Context Diagram

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

Person(user, "User", "Пользователь системы Умный дом")
System_Boundary(c1, "Экосистема Умный дом") {
    System(users, "User", "Подсистема Пользователь")
    System(deviceCtl, "Control Panel", "Подсистема Панель управления")
    System(deviceData, "Telemetry", "Подсистема Телеметрии")
    System(media, "Media", "Подсистема обработки, хранения и воспроизведения потокового видео с камер")              
}


System_Ext(HeatingDevice, "Heating Smart Device", Устройство отопления c выходом в интернет")
System_Ext(VideoRegistrator, "Video Registrator", Камера с датчиком движения и c выходом в интернет")
System_Ext(LightingDevice, "Lighting Smart Device", Устройство освещения c выходом в интернет")
System_Ext(GateConroller, "Gate Controller", Устройство управления воротами c выходом в интернет")
BiRel(user, c1, "Управление и мониторинг устройств умного дома")
BiRel(c1,HeatingDevice,"Обмен сообщениями по протоколу MQTT")
BiRel(c1,VideoRegistrator,"Обмен сообщениями по протоколу MQTT")
BiRel(c1,LightingDevice,"Обмен сообщениями по протоколу MQTT")
BiRel(c1,GateConroller,"Обмен сообщениями по протоколу MQTT")
Rel(VideoRegistrator, c1, "Медиапоток с камер по протоколу RTMP")
@enduml
``` 