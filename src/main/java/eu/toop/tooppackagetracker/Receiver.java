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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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
  private static final Map <String, Object> PROPS = new HashMap <> ();

  static
  {
    try
    {
      final Properties p = new Properties ();
      p.load (Receiver.class.getClassLoader ().getResourceAsStream ("application.properties"));
      PROPS.put (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, p.get ("kafka.bootstrap-servers"));
      PROPS.put (ConsumerConfig.GROUP_ID_CONFIG, p.get ("kafka.consumer.group-id"));
      PROPS.put (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, p.get ("kafka.consumer.auto-offset-reset"));
      PROPS.put (ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, p.get ("kafka.consumer.enable-auto-commit"));
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }

    LOGGER.info ("Loaded Kafka receiver properties " + PROPS);
  }

  private final List <IReceiverListener> listeners = new ArrayList <> ();
  private final ConcurrentMessageListenerContainer <String, String> container;

  public Receiver (final String topic)
  {
    final Map <String, Object> props;
    if (true)
    {
      props = new HashMap <> ();
      props.put (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "toop-tracker.dsv.su.se:7073");
      props.put (ConsumerConfig.GROUP_ID_CONFIG, "toop-group");
      props.put (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
      props.put (ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    }
    else
      props = Collections.unmodifiableMap (PROPS);

    final DefaultKafkaConsumerFactory <String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory <> (props,
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
    final Map <String, Object> props;
    if (true)
    {
      props = new HashMap <> ();
      props.put (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "toop-tracker.dsv.su.se:7073");
      props.put (ConsumerConfig.GROUP_ID_CONFIG, "toop-group");
      props.put (ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
      props.put (ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    }
    else
      props = Collections.unmodifiableMap (PROPS);

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
