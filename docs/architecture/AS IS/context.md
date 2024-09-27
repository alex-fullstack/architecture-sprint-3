## Система Умный дом - Диаграмма контекстов

Система обеспечивает автоматизирование отопление в доме на основе информационного обмена с пользователем и модулем отопления, устанавливаемым в доме пользователя

```puml
@startuml

top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

Person(user, "User", "Пользователь системы Умный дом")
System(SmartHomeSystem, "SmartHome System", "Система Умный дом")


System_Ext(HeatingModule, "Heating Module", "Модуль отопления с коммутатором, реле и датчиковой аппаратурой")
BiRel(user, SmartHomeSystem, "Управление и мониторинг отопления в доме")
BiRel(SmartHomeSystem,HeatingModule,"Обмен данными с модулем отопления")
@enduml
``` 