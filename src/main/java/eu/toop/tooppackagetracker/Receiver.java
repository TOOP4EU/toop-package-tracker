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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.vaadin.ui.UIDetachedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
  private CountDownLatch latch = new CountDownLatch(1);
  private List<Listener> listeners = new ArrayList<>();

  public CountDownLatch getLatch() {
    return latch;
  }

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

  public interface Listener {
    void receive(ConsumerRecord<?, ?> consumerRecord);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }
}

