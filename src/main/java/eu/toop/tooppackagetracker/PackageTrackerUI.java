package eu.toop.tooppackagetracker;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@Theme("valo")
@SpringUI
@SuppressWarnings("serial")
@Push
public class PackageTrackerUI extends UI implements Receiver.Listener {

  private static String BOOT_TOPIC = "boot.t";

  final VerticalLayout layout = new VerticalLayout();

  @Autowired
  private Sender sender;

  @Autowired
  private Receiver receiver;

  @Override
  public void receive(ConsumerRecord<?, ?> consumerRecord) {
    layout.addComponent(new Label(consumerRecord.value().toString()));
    this.push();
  }

  @WebServlet(value = "/*", asyncSupported = true)
  public static class Servlet extends SpringVaadinServlet {
  }

  @WebListener
  public static class MyContextLoaderListener extends ContextLoaderListener {
  }

  @Configuration
  @EnableVaadin
  public static class MyConfiguration {

  }

  @Override
  protected void init(VaadinRequest request) {
    receiver.addListener(this);

    layout.setMargin(true);
    setContent(layout);

    Button button = new Button("Click Me");
    button.addClickListener(new Button.ClickListener() {
      public void buttonClick(Button.ClickEvent event) {
        sender.send("toop", "Hello!");
      }
    });
    layout.addComponent(button);
  }
}