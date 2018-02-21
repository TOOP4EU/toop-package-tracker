package eu.toop.tooppackagetracker.parallax.cards;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;

public class Login extends VerticalLayout{

  public Login() {
    Resource res = new ThemeResource("img/sample-login.png");
    Image image = new Image(null, res);
    image.setStyleName("loginAnimation");
    addComponent(image);
  }
}
