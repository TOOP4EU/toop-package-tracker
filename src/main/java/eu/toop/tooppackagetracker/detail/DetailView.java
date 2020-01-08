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
package eu.toop.tooppackagetracker.detail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import eu.toop.tooppackagetracker.IReceiverListener;
import eu.toop.tooppackagetracker.KafkaConsumerManager;
import eu.toop.tooppackagetracker.PackageTrackerUI;
import eu.toop.tooppackagetracker.Receiver;

@com.vaadin.annotations.JavaScript ({ "vaadin://jquery/jquery-3.3.1.js", "vaadin://js/package-tracker.js", })
public class DetailView extends VerticalLayout implements View, IReceiverListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DetailView.class);

  private final PackageTrackerUI _ui;
  @SuppressWarnings ("unused")
  private final JavaScript _javaScript;

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
    final List <String> aSortedTopics = new ArrayList <> (allTopics.size ());
    aSortedTopics.addAll (allTopics.keySet ());
    aSortedTopics.sort ( (o1, o2) -> o1.compareToIgnoreCase (o2));

    final ListSelect <String> topicSelector = new ListSelect <> ("Select which topics to view:", aSortedTopics);
    topicSelector.setStyleName ("topicSelector");
    topicSelector.setHeight (100, Unit.PERCENTAGE);
    topicSelector.setWidth (100.0f, Unit.PERCENTAGE);
    left.addComponent (topicSelector);

    topicSelector.addValueChangeListener (event -> {
      logLayout.removeAllComponents ();
      for (final String topic : aSortedTopics)
      {
        final boolean bSelectedTopic = event.getValue ().contains (topic);
        if (bSelectedTopic)
        {
          if (!isSubscribedToKafkaTopic (topic))
            trySubscribeToKafkaTopic (topic);
        }
        else
        {
          if (isSubscribedToKafkaTopic (topic))
            tryUnsubscribeToKafkaTopic (topic);
        }
      }
    });

    right.addComponent (logLayout);
  }

  @Override
  public void receive (final ConsumerRecord <?, ?> consumerRecord)
  {
    final String topic = "[" + consumerRecord.topic() + "] ";
    final String message = consumerRecord.value ().toString ();

    final DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    final Date date = new Date ();

    final Label logLabel = new Label (dateFormat.format (date) + " : " + topic + message);
    logLabel.setStyleName ("logLabel");
    logLabel.addStyleName (parseLogLevelColor(message));
    logLabel.setSizeUndefined ();
    logLayout.addComponent (logLabel);

    _ui.access ( () -> UI.getCurrent ().push ());
  }

  private String parseLogLevelColor(String message)
  {
    if(message == null)
      return null;
    if(message.startsWith("[SUCCESS]"))
      return "successLogLabel";
    else if(message.startsWith("[INFO]"))
      return "infoLogLabel";
    else if(message.startsWith("[WARN]"))
      return "warnLogLabel";
    else if(message.startsWith("[ERROR]"))
      return "errorLogLabel";
    else if(message.startsWith("[FATAL_ERROR]"))
      return "fatalErrorLogLabel";
    return null;
  }

  public boolean isSubscribedToKafkaTopic (final String topic)
  {
    final Receiver kafkaConsumer = KafkaConsumerManager.getKafkaConsumers ().get (topic);
    return kafkaConsumer != null && kafkaConsumer.listeners ().contains (this);
  }

  public void trySubscribeToKafkaTopic (final String sTopic)
  {
    Receiver kafkaConsumer = KafkaConsumerManager.getKafkaConsumers ().get (sTopic);
    if (kafkaConsumer == null)
    {
      LOGGER.info ("Creating a new receiver for topic '" + sTopic + "'.");
      kafkaConsumer = new Receiver (sTopic);
      kafkaConsumer.listeners ().add (this);
      KafkaConsumerManager.getKafkaConsumers ().put (sTopic, kafkaConsumer);
    }
    else
    {
      if (kafkaConsumer.listeners ().contains (this))
      {
        LOGGER.info ("This component is already listening to the receiver for topic '" + sTopic + "'.");
      }
      else
      {
        LOGGER.info ("Re-using existing receiver for topic '" + sTopic + "'.");
        kafkaConsumer.listeners ().add (this);
      }
    }
  }

  public void tryUnsubscribeToKafkaTopic (final String sTopic)
  {
    final Receiver kafkaConsumer = KafkaConsumerManager.getKafkaConsumers ().get (sTopic);
    if (kafkaConsumer != null && kafkaConsumer.listeners ().contains (this))
    {
      LOGGER.info ("This component is now unsubscribing from receiver for topic '" + sTopic + "'.");
      kafkaConsumer.listeners ().remove (this);
    }
  }
}
