package eu.toop.tooppackagetracker;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import eu.toop.tooppackagetracker.parallax.ParallaxLayout;
import eu.toop.tooppackagetracker.parallax.Slice;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

@StyleSheet({
                /*
                 * JQuery UI
                 */
//        "vaadin://jquery/jquery-ui-1.9.2.custom/css/ui-darkness/jquery-ui-1.9.2.custom.min.css",
})

@com.vaadin.annotations.JavaScript({
                /*
                 * JQuery
                 */
        "vaadin://jquery/jquery-3.3.1.js",
        "vaadin://js/package-tracker.js",

                /*
                 * JQuery UI
                 */
//        "vaadin://jquery/jquery-ui-1.9.2.custom/js/jquery-ui-1.9.2.custom.min.js",
})

@Theme("PackageTrackerUITheme")
@SpringUI
@SuppressWarnings("serial")
@Push
@PreserveOnRefresh
public class PackageTrackerUI extends UI implements Receiver.Listener {

  private static String BOOT_TOPIC = "boot.t";

  final VerticalLayout mainLayout = new VerticalLayout();
  final ParallaxLayout parallaxLayout = new ParallaxLayout();
  final VerticalLayout controlLayout = new VerticalLayout();
  JavaScript javaScript;

  @Autowired
  private Sender sender;

  @Autowired
  private Receiver receiver;

  @Override
  public void receive(ConsumerRecord<?, ?> consumerRecord) {
    String message = consumerRecord.value().toString();
    parallaxLayout.newSlice(message);
    javaScript.execute("newSlice()");
    this.access(new Runnable() {
      @Override
      public void run() {
        push();
      }
    });
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
    mainLayout.setHeight("100%");
    mainLayout.setWidth("100000px");
    mainLayout.setStyleName("mainLayout");
    setContent(mainLayout);

    receiver.addListener(this);
    javaScript = JavaScript.getCurrent();

    //mainLayout.addComponent(controlLayout);
    mainLayout.addComponent(parallaxLayout);

    Button mockButton = new Button("Mock input");
    mockButton.addClickListener(new Button.ClickListener() {
      public void buttonClick(Button.ClickEvent event) {
        sender.send("toop", "A toop message");
      }
    });
    controlLayout.addComponent(mockButton);
  }
}