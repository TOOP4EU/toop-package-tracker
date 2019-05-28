# TOOP Package Tracker

This software is only deployed once by the TOOP Playground maintainers.
Pilot partners MUST not deploy this.

Public URL: 
* UI `http://tracker.acc.exchange.toop.eu`
* UI details: `http://tracker.acc.exchange.toop.eu/#!detail`

URL inside the VPN:
* UI: `http://tracker.acc.exchange.toop.eu:7074/`
* UI details: `http://tracker.acc.exchange.toop.eu:7074/#!detail`
* Zookeeper server `http://tracker.acc.exchange.toop.eu:2181/`´
* Kafka Server: `http://tracker.acc.exchange.toop.eu:7073/`´

## Configuration

Current version: Kafka 2.11

The Kafka configuration file is located at `/opt/kafka/config/server.properties`

## Deployment

1. Close down Tracker, Kafka and Zookeeper processes.
2. Start Zookeeper server first, using `/opt/kafka/bin/zookeeper-server-start.sh &`
3. Start Kafka server using `/opt/kafka/bin/kafka-server-start.sh &`
4. Start the Tracker service (`service tracker start`)
