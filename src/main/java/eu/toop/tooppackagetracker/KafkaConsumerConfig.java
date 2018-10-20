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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfig
{

  @Value ("${kafka.bootstrap-servers}")
  private String bootstrapServer;

  @Value ("${kafka.consumer.group-id}")
  private String groupId;

  @Bean
  public DefaultKafkaConsumerFactory <String, String> consumerFactory ()
  {
    final Map <String, Object> props = new HashMap <> ();
    props.put (ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Arrays.asList (bootstrapServer));
    props.put (ConsumerConfig.GROUP_ID_CONFIG, "toop-group-" + UUID.randomUUID ());
    props.put (ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put (ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    return new DefaultKafkaConsumerFactory <> (props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory <String, String> kafkaListenerContainerFactory ()
  {
    final ConcurrentKafkaListenerContainerFactory <String, String> factory = new ConcurrentKafkaListenerContainerFactory <> ();
    factory.setConsumerFactory (consumerFactory ());
    factory.setConcurrency (Integer.valueOf (4));
    return factory;
  }
}
