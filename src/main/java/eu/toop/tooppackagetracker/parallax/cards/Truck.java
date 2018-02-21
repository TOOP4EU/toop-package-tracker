package eu.toop.tooppackagetracker.parallax.cards;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

public class Truck extends VerticalLayout{

  public Truck() {
    Resource res = new ThemeResource("img/sample-truck.png");
    Image image = new Image(null, res);
    image.setStyleName("truckAnimation");
    addComponent(image);
  }
}
