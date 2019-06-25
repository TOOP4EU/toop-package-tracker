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
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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

  private final CountDownLatch latch = new CountDownLatch (1);
  private final List <Listener> listeners = new ArrayList <> ();

  public Receiver (final String topic)
  {
    final DefaultKafkaConsumerFactory <String, String> kafkaConsumerFactory = new DefaultKafkaConsumerFactory <> (Collections.unmodifiableMap (PROPS),
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
        {}
      }

      latch.countDown ();
    });

    final ConcurrentMessageListenerContainer <String, String> container = new ConcurrentMessageListenerContainer <> (kafkaConsumerFactory,
                                                                                                                     containerProperties);

    container.start ();
  }

  /*
   * @KafkaListener(topics = "toop") public void receive(ConsumerRecord<?, ?>
   * consumerRecord) { LOGGER.info("received payload='{}'",
   * consumerRecord.toString()); for (Listener listener : listeners) { try {
   * listener.receive(consumerRecord); } catch (UIDetachedException e) { } }
   * latch.countDown(); }
   */
  public interface Listener extends Serializable
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
