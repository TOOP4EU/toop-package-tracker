package eu.toop.tooppackagetracker.parallax;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import eu.toop.tooppackagetracker.parallax.cards.Login;
import eu.toop.tooppackagetracker.parallax.cards.Truck;

public class Slice extends VerticalLayout {
  public Slice() {
    this.setStyleName("pt-slot");
  }

  public void addSlotItem(String message) {
    setStyleName("slice");

    addComponent(new Label(message));

    if (message.equals("truck")) {
      addComponent(new Truck());
    } else if (message.equals("login")) {
      addComponent(new Login());
    }
  }
}
