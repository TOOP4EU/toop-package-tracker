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
package eu.toop.tooppackagetracker.detail;

import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import eu.toop.tooppackagetracker.PackageTrackerUI;
import eu.toop.tooppackagetracker.Receiver;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@com.vaadin.annotations.JavaScript({
  "vaadin://jquery/jquery-3.3.1.js",
  "vaadin://js/package-tracker.js",
})

public class DetailView extends VerticalLayout implements View, Receiver.Listener {

  private static final Logger LOGGER = LoggerFactory.getLogger(DetailView.class);
  private PackageTrackerUI _ui;
  JavaScript _javaScript;

  private VerticalLayout logLayout = new VerticalLayout ();

  public DetailView (PackageTrackerUI ui, JavaScript javascript) {
    _ui = ui;
    _javaScript = javascript;

    setStyleName ("DetailView");
//    setSizeUndefined ();
    setSizeFull ();
    setWidth ("100%");

    Label titleLabel = new Label ("Playground Package Tracker Detail log");
    titleLabel.setStyleName ("titleLabel");
    addComponent (titleLabel);
    setExpandRatio (titleLabel, 0);

    HorizontalLayout hr = new HorizontalLayout ();
    addComponent (hr);
    setExpandRatio (hr, 1);
    hr.setSizeFull ();

    VerticalLayout left = new VerticalLayout ();
    left.setStyleName ("leftSide");
    left.setHeight (100, Unit.PERCENTAGE);
    left.setWidth ("250px");
    hr.addComponent (left);
    hr.setExpandRatio (left, 0.0f);

    VerticalLayout right = new VerticalLayout ();
    right.setCaption ("Log:");
    right.setStyleName ("rightSide");
    hr.addComponent (right);
    right.setSizeFull ();
    hr.setExpandRatio (right, 1.0f);

    logLayout.setSizeUndefined ();
    logLayout.setWidth ("100%");
    logLayout.setStyleName ("logLayout");

    final Map<String, List<PartitionInfo>> allTopics = Receiver.getAllTopics ();

    ListSelect<String> topicSelector = new ListSelect<>("Select which topics to view:", allTopics.keySet());
    topicSelector.setStyleName ("topicSelector");
    topicSelector.setHeight (100, Unit.PERCENTAGE);
    topicSelector.setWidth(100.0f, Unit.PERCENTAGE);
    left.addComponent (topicSelector);

    topicSelector.addValueChangeListener(event -> {
      logLayout.removeAllComponents ();
      for (String topic : allTopics.keySet ()) {
        boolean selectedTopic = event.getValue ().contains (topic);

        if (selectedTopic) {
          if (!isSubscribedToKafkaTopic (topic)) {
            trySubscribeToKafkaTopic(topic);
          }
        } else {
          if (isSubscribedToKafkaTopic (topic)) {
            tryUnsubscribeToKafkaTopic (topic);
          }
        }
      }
    });

    right.addComponent (logLayout);
  }

  @Override
  public void receive (ConsumerRecord<?, ?> consumerRecord) {
    String message = consumerRecord.value().toString();

    DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();

    Label logLabel = new Label (dateFormat.format(date) + " : " + message);
    logLabel.setStyleName ("logLabel");
    logLabel.setSizeUndefined ();
    logLayout.addComponent (logLabel);

    _ui.access(new Runnable() {
      @Override
      public void run() {
        _ui.getCurrent ().push();
      }
    });
  }

  public boolean isSubscribedToKafkaTopic(String topic) {
    if (_ui.getKafkaConsumers ().containsKey (topic)) {
      Receiver kafkaConsumer = _ui.getKafkaConsumers ().get (topic);

      if (kafkaConsumer != null && kafkaConsumer.getListeners ().contains (this)) {
        return true;
      }
    }
    return false;
  }

  public void trySubscribeToKafkaTopic(String topic) {

    if (!_ui.getKafkaConsumers ().containsKey (topic)) {

      LOGGER.info ("Creating a new receiver!");
      Receiver kafkaConsumer;
      kafkaConsumer = new Receiver (topic);
      kafkaConsumer.addListener(this);
      _ui.getKafkaConsumers ().put(topic, kafkaConsumer);
    } else {
      Receiver kafkaConsumer = _ui.getKafkaConsumers ().get (topic);

      if (kafkaConsumer.getListeners ().contains (this)) {
        LOGGER.info ("This component is already listening to the receiver");
      } else {
        LOGGER.info ("Re-using existing receiver");
        kafkaConsumer.addListener (this);
      }
    }
  }

  public void tryUnsubscribeToKafkaTopic(String topic) {

    if (_ui.getKafkaConsumers ().containsKey (topic)) {
      Receiver kafkaConsumer = _ui.getKafkaConsumers ().get (topic);

      if (kafkaConsumer.getListeners ().contains (this)) {
        LOGGER.info ("This component is now unsubscribing from receiver");
        kafkaConsumer.removeListener (this);
      }
    }
  }
}
