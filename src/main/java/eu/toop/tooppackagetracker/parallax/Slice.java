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

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.toop.tooppackagetracker.parallax.cards.Login;
import eu.toop.tooppackagetracker.parallax.cards.Truck;

public class Slice extends VerticalLayout
{
  public Slice ()
  {
    this.setStyleName ("pt-slot");
  }

  public void addSlotItem (final String message)
  {
    setStyleName ("slice");

    addComponent (new Label (message));

    if (message.equals ("truck"))
      addComponent (new Truck ());
    else
      if (message.equals ("login"))
        addComponent (new Login ());
  }
}
