/**
 * Copyright (C) 2018-2021 toop.eu. All rights reserved.
 *
 * This project is dual licensed under Apache License, Version 2.0
 * and the EUPL 1.2.
 *
 *  = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
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
 *
 *  = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *         https://joinup.ec.europa.eu/software/page/eupl
 */
package eu.toop.tooppackagetracker.parallax;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.vaadin.navigator.View;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.toop.tooppackagetracker.IReceiverListener;

@com.vaadin.annotations.JavaScript ({ "vaadin://jquery/jquery-3.3.1.js", "vaadin://js/package-tracker.js", })
public class ParallaxView extends VerticalLayout implements View, IReceiverListener
{
  private final UI _ui;
  JavaScript _javaScript;

  final VerticalLayout mainLayout = new VerticalLayout ();
  final ParallaxLayout parallaxLayout = new ParallaxLayout ();

  public ParallaxView (final UI ui, final JavaScript javascript)
  {
    _ui = ui;
    _javaScript = javascript;

    setStyleName ("ParallaxView");
    setWidth ("100000px");
    setHeight ("100%");

    mainLayout.setHeight ("100%");
    mainLayout.setWidth ("100000px");
    mainLayout.setStyleName ("mainLayout");
    addComponent (mainLayout);
    mainLayout.addComponent (parallaxLayout);
  }

  @Override
  public void receive (final ConsumerRecord <?, ?> consumerRecord)
  {
    final String message = consumerRecord.value ().toString ();
    parallaxLayout.newSlice (message);
    _javaScript.execute ("newSlice()");
    _ui.access ( () -> UI.getCurrent ().push ());
  }
}
