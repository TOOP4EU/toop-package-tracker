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

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import eu.toop.tooppackagetracker.detail.DetailView;
import eu.toop.tooppackagetracker.parallax.ParallaxLayout;
import eu.toop.tooppackagetracker.parallax.ParallaxView;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Theme("PackageTrackerUITheme")
@SpringUI
@SuppressWarnings("serial")
@Push
//@PreserveOnRefresh
public class PackageTrackerUI extends UI implements Receiver.Listener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PackageTrackerUI.class);

  private Navigator navigator;

  @Autowired
  private Receiver kafkaConsumer;

  @Override
  public void receive(ConsumerRecord<?, ?> consumerRecord) {

  }

  @Override
  protected void init(VaadinRequest request) {
    //kafkaConsumer.addListener(this);
    ParallaxView parallaxView = new ParallaxView (this, JavaScript.getCurrent ());
    DetailView detailView = new DetailView (this);

    navigator = new Navigator (this, this);
    navigator.addView("", parallaxView);
    navigator.addView(ParallaxView.class.getName(), parallaxView);
    navigator.addView("detail", detailView);

    kafkaConsumer.addListener(parallaxView);
    kafkaConsumer.addListener(detailView);
  }


  @Override
  public void attach() {
    super.attach();
  }

  @Override
  public void detach() {
    super.detach();
  }
}