# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

# Kafka Basics commands and docker
Kafka is a distributed streaming platform that is commonly used to build real-time data pipelines and streaming applications. This README provides some basic commands to get started with Kafka.

## Prerequisites
Before you can use Kafka, you'll need to download and install it on your machine. You can find the download link and installation instructions on the official Kafka website.
You have to installed Docker Desktop.

## Starting containers to zookeeper and kafka brokers
```
docker compose up -d
```
This command run container in docker-compose.yml

## Inside to container kafka to runs all commands
```
docker exec -it container-name bash
```

## Starting Kafka
To start Kafka, you'll need to start the Kafka broker and the ZooKeeper server.

## Start the ZooKeeper Server
```
bin/zookeeper-server-start.sh config/zookeeper.properties
```
## Start the Kafka Broker

```
bin/kafka-server-start.sh config/server.properties 
```
## Creating a Topic
Before you can start sending and consuming messages in Kafka, you'll need to create a topic.
```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic my-topic
```
This command will create a topic called my-topic with a single partition and a replication factor of 1.

## Creating a Topic from container kafka
Inside from container you can create a topic too
```
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic my-topic
```
This command will create a topic called my-topic with a single partition and a replication factor of 1.

## Sending Messages
To send messages to a topic, you can use the kafka-console-producer script.
```
kafka-console-producer --broker-list localhost:9092 --topic my-topic
```
This command will start a producer that will allow you to send messages to the my-topic topic.

## Consuming Messages from a specific topic
To consume messages from a topic, you can use the kafka-console-consumer script.
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic my-topic --from-beginning
```
This command will start a consumer that will read messages from the my-topic topic. The --from-beginning option will read all the messages from the beginning of the topic.

## Consuming Messages from a specific topic and partition
To consume messages from a topic, you can use the kafka-console-consumer script.
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic my-topic --partition 0 --from-beginning
```
## Describe a topic with your partitions

```
kafka-topics --describe --bootstrap-server localhost:9092 --topic library-events
```

## Conclusion
**Clean architecture**, **Spring Boot**, **Kafka**, and **Docker** are powerful tools for building scalable and maintainable microservices.

By using Clean Architecture, we can create a modular and decoupled application that is easy to understand and maintain. **Spring Boot** provides a powerful framework for building microservices quickly and easily, with many built-in features such as autoconfiguration and **dependency injection**.

**Kafka** provides a distributed and fault-tolerant message streaming platform that allows us to build real-time data pipelines and streaming applications. With Kafka, we can easily handle high-volume, real-time data streams and ensure that messages are processed in a scalable and efficient way.

**Docker** allows us to package our application and its dependencies into a single container, making it easy to deploy and scale our application in any environment. By using Docker, we can ensure that our application runs consistently across different environments and avoid any potential issues caused by differences in runtime environments.

**Overall**, by combining these technologies, we can create a highly scalable and maintainable microservice architecture that is easy to deploy and manage. With the right design and architecture, we can build a resilient and fault-tolerant system that can handle high volumes of data and provide real-time insights into our application.

These are some basic commands that you can use to get started with Kafka. Kafka has many more features and options that you can explore by referring [official Kafka website](https://kafka.apache.org/downloads).
