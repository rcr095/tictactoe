# TicTacToe Spring Boot App

## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/java/technologies/downloads/#java11)
- [Maven 3](https://maven.apache.org)

## Running the application locally

#### Using Maven:

```shell
mvn spring-boot:run
```

#### Using Docker:

Use terminal to navigat to the project path:
```shell
docker compose up
```

#### Standalone PostgresSQL DB:

Standalone DB available in "LocalSetup" directory, run with command: 

```shell
docker compose -f LocalSetup/docker-compose.yml up
```
