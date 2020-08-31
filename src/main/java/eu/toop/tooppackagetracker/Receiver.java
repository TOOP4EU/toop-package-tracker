/**
 * Copyright (C) 2018-2020 toop.eu
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import com.vaadin.ui.UIDetachedException;

public class Receiver
{
  private static final Logger LOGGER = LoggerFactory.getLogger (Receiver.class);
  private static final Map <String, Object> PROPS = new HashMap <> ();

  static
  {
    // This is different from application.properties and I don't know why
    // localhost works here, but not in application.properties
    PROPS.put (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, true ? "localhost:7073" : "tracker.acc.exchange.toop.eu:7073");
    PROPS.put (ConsumerConfig.GROUP_ID_CONFIG, KafkaConsumerManager.TOPIC_GROUP_ID);
    PROPS.put (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    PROPS.put (ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

    LOGGER.info ("Defined Kafka consumer properties " + PROPS);
  }

  private final List <IReceiverListener> listeners = new ArrayList <> ();
  private final ConcurrentMessageListenerContainer <String, String> container;

  public Receiver (final String topic)
  {
    final DefaultKafkaConsumerFactory <String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory <> (Collections.unmodifiableMap (PROPS),
                                                                                                                  new StringDeserializer (),
                                                                                                                  new StringDeserializer ());

    final ContainerProperties containerProperties = new ContainerProperties (topic);
    containerProperties.setSyncCommits (false);
    containerProperties.setMessageListener ((MessageListener <String, String>) consumerRecord -> {
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("received payload='" + consumerRecord.toString () + "'");

      for (final IReceiverListener listener : listeners)
      {
        try
        {
          listener.receive (consumerRecord);
        }
        catch (final UIDetachedException e)
        {}
      }
    });

    container = new ConcurrentMessageListenerContainer <> (kafkaConsumerFactory, containerProperties);
    container.start ();
  }

  public void stop ()
  {
    if (container.isRunning ())
      container.stop ();
  }

  public List <IReceiverListener> listeners ()
  {
    return listeners;
  }

  public static Map <String, List <PartitionInfo>> getAllTopics ()
  {
    try (final KafkaConsumer <String, String> consumer = new KafkaConsumer <> (Collections.unmodifiableMap (PROPS),
                                                                               new StringDeserializer (),
                                                                               new StringDeserializer ()))
    {
      final Map <String, List <PartitionInfo>> topics = consumer.listTopics ();
      topics.remove ("__consumer_offsets");
      return topics;
    }
  }
}
