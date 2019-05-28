/**
 * Copyright (C) 2018 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.tooppackagetracker;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import com.vaadin.ui.UIDetachedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Component;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;

public class Receiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
  private CountDownLatch latch = new CountDownLatch(1);
  private List<Listener> listeners = new ArrayList<>();

  public CountDownLatch getLatch() {
    return latch;
  }

  public Receiver(final String topic) {

    final HashMap<String, Object> props = new HashMap<> ();
    props.put(BOOTSTRAP_SERVERS_CONFIG, "toop-tracker.dsv.su.se:7073");
    props.put(GROUP_ID_CONFIG, "toop-group");
    props.put(AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");

    Map<String, Object> consumerConfig = Collections.unmodifiableMap (props);

    DefaultKafkaConsumerFactory<String, String> kafkaConsumerFactory =
        new DefaultKafkaConsumerFactory<> (
            consumerConfig,
            new StringDeserializer(),
            new StringDeserializer ());

    ContainerProperties containerProperties = new ContainerProperties(topic);
    containerProperties.setSyncCommits (false);
    containerProperties.setMessageListener((MessageListener<String, String>) consumerRecord -> {
      //consumedMessages.add(record.value());
      LOGGER.info("received payload='{}'", consumerRecord.toString());

      for (Listener listener : listeners) {
        try {
          listener.receive(consumerRecord);
        } catch (UIDetachedException e) {

        }
      }

      latch.countDown();
    });

    ConcurrentMessageListenerContainer container =
        new ConcurrentMessageListenerContainer<>(
            kafkaConsumerFactory,
            containerProperties);


    container.start();
  }

  public void receive2() {

  }
/*
  @KafkaListener(topics = "toop")
  public void receive(ConsumerRecord<?, ?> consumerRecord) {
    LOGGER.info("received payload='{}'", consumerRecord.toString());

    for (Listener listener : listeners) {
      try {
        listener.receive(consumerRecord);
      } catch (UIDetachedException e) {
        
      }
    }

    latch.countDown();
  }
*/
  public interface Listener {
    void receive(ConsumerRecord<?, ?> consumerRecord);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public List<Listener> getListeners () {

    return listeners;
  }

  public static Map<String, List<PartitionInfo> > getAllTopics() {
    Map<String, List<PartitionInfo> > topics;

    Properties props = new Properties ();
    props.put(BOOTSTRAP_SERVERS_CONFIG, "toop-tracker.dsv.su.se:7073");
    props.put(GROUP_ID_CONFIG, "toop-group");
    props.put(AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ENABLE_AUTO_COMMIT_CONFIG, "false");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props,
        new StringDeserializer (),
        new StringDeserializer ());
    topics = consumer.listTopics();
    topics.remove ("__consumer_offsets");
    consumer.close();

    return topics;
  }
}

