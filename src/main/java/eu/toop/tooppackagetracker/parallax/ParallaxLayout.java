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
