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

Current version: Kafka 2.20

The Kafka configuration file is located at `/opt/kafka/config/server.properties`

## Deployment

1. Close down Tracker, Kafka and Zookeeper processes.
    1. `sudo service tomcat stop`
    1. Check with `ps -ef | grep java`
    1. `sudo /opt/kafka/bin/kafka-server-stop.sh`
    1. Check with `ps -ef | grep kafka`
    1. `sudo /opt/kafka/bin/zookeeper-server-stop.sh`
    1. Check with `ps -ef | grep zookeeper`
2. Start Zookeeper server first, using `sudo /opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties &`
3. Start Kafka server using `sudo /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties &`
4. Start the Tracker service (`service tracker start`)
