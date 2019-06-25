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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.toop.tooppackagetracker.PackageTrackerUI;
import eu.toop.tooppackagetracker.Receiver;

@com.vaadin.annotations.JavaScript ({ "vaadin://jquery/jquery-3.3.1.js", "vaadin://js/package-tracker.js", })
public class DetailView extends VerticalLayout implements View, Receiver.Listener
{

  private static final Logger LOGGER = LoggerFactory.getLogger (DetailView.class);
  private final PackageTrackerUI _ui;
  JavaScript _javaScript;

  private final VerticalLayout logLayout = new VerticalLayout ();

  public DetailView (final PackageTrackerUI ui, final JavaScript javascript)
  {
    _ui = ui;
    _javaScript = javascript;

    setStyleName ("DetailView");
    // setSizeUndefined ();
    setSizeFull ();
    setWidth ("100%");

    final Label titleLabel = new Label ("Playground Package Tracker Detail log");
    titleLabel.setStyleName ("titleLabel");
    addComponent (titleLabel);
    setExpandRatio (titleLabel, 0);

    final HorizontalLayout hr = new HorizontalLayout ();
    addComponent (hr);
    setExpandRatio (hr, 1);
    hr.setSizeFull ();

    final VerticalLayout left = new VerticalLayout ();
    left.setStyleName ("leftSide");
    left.setHeight (100, Unit.PERCENTAGE);
    left.setWidth ("250px");
    hr.addComponent (left);
    hr.setExpandRatio (left, 0.0f);

    final VerticalLayout right = new VerticalLayout ();
    right.setCaption ("Log:");
    right.setStyleName ("rightSide");
    hr.addComponent (right);
    right.setSizeFull ();
    hr.setExpandRatio (right, 1.0f);

    logLayout.setSizeUndefined ();
    logLayout.setWidth ("100%");
    logLayout.setStyleName ("logLayout");

    final Map <String, List <PartitionInfo>> allTopics = Receiver.getAllTopics ();

    final ListSelect <String> topicSelector = new ListSelect <> ("Select which topics to view:", allTopics.keySet ());
    topicSelector.setStyleName ("topicSelector");
    topicSelector.setHeight (100, Unit.PERCENTAGE);
    topicSelector.setWidth (100.0f, Unit.PERCENTAGE);
    left.addComponent (topicSelector);

    topicSelector.addValueChangeListener (event -> {
      logLayout.removeAllComponents ();
      for (final String topic : allTopics.keySet ())
      {
        final boolean selectedTopic = event.getValue ().contains (topic);

        if (selectedTopic)
        {
          if (!isSubscribedToKafkaTopic (topic))
          {
            trySubscribeToKafkaTopic (topic);
          }
        }
        else
        {
          if (isSubscribedToKafkaTopic (topic))
          {
            tryUnsubscribeToKafkaTopic (topic);
          }
        }
      }
    });

    right.addComponent (logLayout);
  }

  @Override
  public void receive (final ConsumerRecord <?, ?> consumerRecord)
  {
    final String message = consumerRecord.value ().toString ();

    final DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    final Date date = new Date ();

    final Label logLabel = new Label (dateFormat.format (date) + " : " + message);
    logLabel.setStyleName ("logLabel");
    logLabel.setSizeUndefined ();
    logLayout.addComponent (logLabel);

    _ui.access (() -> UI.getCurrent ().push ());
  }

  public boolean isSubscribedToKafkaTopic (final String topic)
  {
    if (_ui.getKafkaConsumers ().containsKey (topic))
    {
      final Receiver kafkaConsumer = _ui.getKafkaConsumers ().get (topic);
      if (kafkaConsumer != null && kafkaConsumer.getListeners ().contains (this))
        return true;
    }
    return false;
  }

  public void trySubscribeToKafkaTopic (final String topic)
  {
    if (!_ui.getKafkaConsumers ().containsKey (topic))
    {

      LOGGER.info ("Creating a new receiver!");
      Receiver kafkaConsumer;
      kafkaConsumer = new Receiver (topic);
      kafkaConsumer.addListener (this);
      _ui.getKafkaConsumers ().put (topic, kafkaConsumer);
    }
    else
    {
      final Receiver kafkaConsumer = _ui.getKafkaConsumers ().get (topic);

      if (kafkaConsumer.getListeners ().contains (this))
      {
        LOGGER.info ("This component is already listening to the receiver");
      }
      else
      {
        LOGGER.info ("Re-using existing receiver");
        kafkaConsumer.addListener (this);
      }
    }
  }

  public void tryUnsubscribeToKafkaTopic (final String topic)
  {
    if (_ui.getKafkaConsumers ().containsKey (topic))
    {
      final Receiver kafkaConsumer = _ui.getKafkaConsumers ().get (topic);

      if (kafkaConsumer.getListeners ().contains (this))
      {
        LOGGER.info ("This component is now unsubscribing from receiver");
        kafkaConsumer.removeListener (this);
      }
    }
  }
}
