# Kafka Error Handling App

This is a concept implementation of an error handling pattern to be used in the Microservices architecture.

Applications integrating with Kafka and other similar message brokers and event streaming platforms should take into consideration of patterns to reprocess information/data on failures. Failures may happen in the downstream systems/services due to any unforseen situations and as such it becomes the responsibility of the message processor to ensure the failures doesn't result in loosing the data and it can be reprocessed when the underlying dependencies are back online.

This concept implementation is built with:

- Java 17
- Spring Boot
- Apache Kafka
- Spring Kafka
- Apache Avro
- Confluent Schema Regsitry

## Architecture
![Image](docs/overview.png)

The components are:

* main-app: This is a Spring Boot microservice providing a REST interface to receive some data and has a Kafka producer and consumer implementation.
  - Will be running on port: 8090
  - The REST Controller will accept a typed data
  - The producer publishes the data to a Kafka topic.
  - The consumer will be listening to the Kafka topic and processes the incoming stream of messages. The consumer makes a RESTful API call to a downstream dependency application. This is somewhat similar to some real-world use cases and allows demonstration of the behaviour when a downstream application is not available.
  - The retry consumer responsible for processing messages from the retry topics.
  - Last but not least, a consumer to process messages from the DLT.
* dependency-app: This is a Spring Boot microservice providing a REST interfac and acts as a depdendency for the main-app
* merge-replay-app: This is a Spring Boot application responsible for reliably reprocessing messages which are not processed due to errors such as unavailability of the down stream service.
* Kafka topics: set of Kafka topics containing messages.
  - Main topic: the producer publishes messages to this topic and the consumer receives the data from the topic.
  - Set of retry topics: on different step-up intervals, the consumer retries the expected processing by calling the dependency-app and if the retry is not successful, the message is moved from one retry topic to another.
  - The retry logic is based on a step up interval. The first retry will be after 3 seconds, followed by 6, 12, 24 and 32 seconds.
  - If all the retries are exhaused and still the underlying depdency is not available, the message will be moved to a Dead Letter Topic (DLT)
  - DLT processor will receive messages from the topic and logs diagnostic info as needed. Alerting and incident management can be integrated here to alert Ops teams.

## Setup
 To run this setup, some pre-requisites are required to be installed and configured. Following are some guides.

 - [Setup Linux VM on Azure refer](https://ramamurthyk.github.io/notes/Linux%20Setup%20on%20Azure%207100b190bfac4cc2a089ec436d50a95b)
 - [Java Setup](https://ramamurthyk.github.io/notes/Linux%20Setup%20on%20Azure%207100b190bfac4cc2a089ec436d50a95b/Java%20Setup%20on%20Linux%2036774edcda1343ae9bb9cfde10cdba6c)
 - [Docker Setup](https://ramamurthyk.github.io/notes/Linux%20Setup%20on%20Azure%207100b190bfac4cc2a089ec436d50a95b/Docker%20Setup%20on%20Linux%20b60c631d0f0b40288a4bb64b6aac82c8)
 - [Kafka Setup](https://ramamurthyk.github.io/notes/Linux%20Setup%20on%20Azure%207100b190bfac4cc2a089ec436d50a95b/Kafka%20Setup%20on%20Linux%2013001e06272949d8ab85130d54c98e62)
 - Finally [VS Code Setup](https://ramamurthyk.github.io/notes/Linux%20Setup%20on%20Azure%207100b190bfac4cc2a089ec436d50a95b/VS%20Code%20Setup%20on%20Linux%20e92fb84f2c914205bb007c42a40d0d98)

Code setup:

- Clone the git repo locally
- Open in VS Code
- In the terminal
 - execute mvn clean
 - Navigate to common-lib folder and execute mvn compile install
 - Then from the root folder of the repo, execute mvn compile package

This should compile all the applications. Run the applications from the command line or via VS Code plugins.

## Demo
``` Bash
POST http://localhost:8090/api/rewards

Content-Type: application/json
 
{
    "customerId": 1006,
    "programme": "Mindfulness",
    "membershipId": "M1234"
}
```