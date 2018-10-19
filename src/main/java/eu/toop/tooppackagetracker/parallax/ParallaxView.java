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
package eu.toop.tooppackagetracker.parallax;

import com.vaadin.navigator.View;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.toop.tooppackagetracker.Receiver;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@com.vaadin.annotations.JavaScript({
  "vaadin://jquery/jquery-3.3.1.js",
  "vaadin://js/package-tracker.js",
})

public class ParallaxView extends VerticalLayout implements View, Receiver.Listener {

  private UI _ui;
  JavaScript _javaScript;

  final VerticalLayout mainLayout = new VerticalLayout();
  final ParallaxLayout parallaxLayout = new ParallaxLayout();

  public ParallaxView (UI ui, JavaScript javascript) {
    _ui = ui;
    _javaScript = javascript;

    setStyleName ("ParallaxView");
    setWidth ("100000px");
    setHeight ("100%");

    mainLayout.setHeight("100%");
    mainLayout.setWidth("100000px");
    mainLayout.setStyleName("mainLayout");
    addComponent (mainLayout);
    mainLayout.addComponent(parallaxLayout);
  }

  @Override
  public void receive (ConsumerRecord<?, ?> consumerRecord) {
    String message = consumerRecord.value().toString();
    parallaxLayout.newSlice(message);
    _javaScript.execute("newSlice()");
    _ui.access(new Runnable() {
      @Override
      public void run() {
        _ui.getCurrent ().push();
      }
    });
  }
}
