# TOOP Package Tracker

This software is only deployed once by the TOOP Playground maintainers.
Pilot partners MUST not deploy this.

## Configuration

Current version: Kafka 2.11

The Kafka configuration file is located at `/opt/kafka/config/server.properties`

## Deployment

1. Close down Tracker, Kafka and Zookeeper processes.
2. Start Zookeeper server first, using `/opt/kafka/bin/zookeeper-server-start.sh &`
3. Start Kafka server using `/opt/kafka/bin/kafka-server-start.sh &`
4. Start the Tracker service (`service tracker start`)
