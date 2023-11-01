# CDC Debezium Server
This is a simple CDC Debezium Server that can be used to capture changes from a MySQL database and send them to a Kafka topic.

## How to use
1.Rename file application_${sink}.properties to application.properties

2.Run
```bash
quarkus dev
```