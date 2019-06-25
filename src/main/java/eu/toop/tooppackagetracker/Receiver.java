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

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;

import com.vaadin.ui.UIDetachedException;

public class Receiver
{
  private static final Logger LOGGER = LoggerFactory.getLogger (Receiver.class);

  private final CountDownLatch latch = new CountDownLatch (1);
  private final List <Listener> listeners = new ArrayList <> ();

  public CountDownLatch getLatch ()
  {
    return latch;
  }

  public Receiver (final String topic)
  {
    final Map <String, Object> props = new HashMap <> ();
    props.put (BOOTSTRAP_SERVERS_CONFIG, "toop-tracker.dsv.su.se:7073");
    props.put (GROUP_ID_CONFIG, "toop-group");
    props.put (AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put (ENABLE_AUTO_COMMIT_CONFIG, "false");

    final Map <String, Object> consumerConfig = Collections.unmodifiableMap (props);

    final DefaultKafkaConsumerFactory <String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory <> (consumerConfig,
                                                                                                                  new StringDeserializer (),
                                                                                                                  new StringDeserializer ());

    final ContainerProperties containerProperties = new ContainerProperties (topic);
    containerProperties.setSyncCommits (false);
    containerProperties.setMessageListener ((MessageListener <String, String>) consumerRecord -> {
      // consumedMessages.add(record.value());
      LOGGER.info ("received payload='{}'", consumerRecord.toString ());

      for (final Listener listener : listeners)
      {
        try
        {
          listener.receive (consumerRecord);
        }
        catch (final UIDetachedException e)
        {

        }
      }

      latch.countDown ();
    });

    final ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer <> (kafkaConsumerFactory,
                                                                                                    containerProperties);

    container.start ();
  }

  public void receive2 ()
  {

  }

  /*
   * @KafkaListener(topics = "toop") public void receive(ConsumerRecord<?, ?>
   * consumerRecord) { LOGGER.info("received payload='{}'",
   * consumerRecord.toString()); for (Listener listener : listeners) { try {
   * listener.receive(consumerRecord); } catch (UIDetachedException e) { } }
   * latch.countDown(); }
   */
  public interface Listener
  {
    void receive (ConsumerRecord <?, ?> consumerRecord);
  }

  public void addListener (final Listener listener)
  {
    listeners.add (listener);
  }

  public void removeListener (final Listener listener)
  {
    listeners.remove (listener);
  }

  public List <Listener> getListeners ()
  {
    return listeners;
  }

  public static Map <String, List <PartitionInfo>> getAllTopics ()
  {
    final Properties props = new Properties ();
    props.put (BOOTSTRAP_SERVERS_CONFIG, "toop-tracker.dsv.su.se:7073");
    props.put (GROUP_ID_CONFIG, "toop-group");
    props.put (AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put (ENABLE_AUTO_COMMIT_CONFIG, "false");

    try (final KafkaConsumer <String, String> consumer = new KafkaConsumer <> (props,
                                                                               new StringDeserializer (),
                                                                               new StringDeserializer ()))
    {
      final Map <String, List <PartitionInfo>> topics = consumer.listTopics ();
      topics.remove ("__consumer_offsets");
      return topics;
    }
  }
}
