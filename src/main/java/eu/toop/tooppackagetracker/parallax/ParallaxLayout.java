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

import com.vaadin.ui.HorizontalLayout;

import java.util.ArrayList;
import java.util.List;

public class ParallaxLayout extends HorizontalLayout {

  private List<Slice> slices = new ArrayList<>();

  public ParallaxLayout() {
    setHeight("100%");
  }

  public void newSlice(String message) {
    Slice slice = new Slice();
    slice.addSlotItem(message);
    addComponent(slice);
    slices.add(slice);
  }

  public void clear() {
    for (Slice slice : slices) {
      removeComponent(slice);
    }
  }
}
