## Система Умный дом - Диаграмма контейнеров

Для выполнения своих функций система организована в виде монолитного приложения на языке программирования JAVA, взаимодействующего про протоколу SQL с СУБД Postgresql. Взаимодействие системы с внешним миром (с пользователем и датчиковой аппаратурой) производится через монолитной приложение по синхронному протоколу HTTP.

```puml
@startuml
top to bottom direction

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml
!$ICONURL = "https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/v3.0.0/icons"
!include $ICONURL/common.puml
!include $ICONURL/devicons/java.puml
!include $ICONURL/devicons/postgresql.puml

Person(user, "User", "Пользователь системы Умный дом")
System_Boundary(c1, "Экосистема Умный дом") {
    Container(smartApp, "Smart Home App", "Приложение Умный дом", $sprite="java")
    ContainerDb(smartDB, "Smart Home DB", "Данные системы отопления", $sprite="postgresql")
}
System_Ext(HeatingModule, "Heating Module", "Модуль отопления с коммутатором, реле и датчиковой аппаратурой")

BiRel(user, smartApp, "HTTP")
BiRel(smartApp, smartDB, "SQL")
BiRel(smartApp,HeatingModule, "HTTP")
@enduml
``` 