package eu.toop.tooppackagetracker;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import eu.toop.tooppackagetracker.parallax.ParallaxLayout;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;

@com.vaadin.annotations.JavaScript({
        "vaadin://jquery/jquery-3.3.1.js",
        "vaadin://js/package-tracker.js",
})

@Theme("PackageTrackerUITheme")
@SpringUI
@SuppressWarnings("serial")
@Push
@PreserveOnRefresh
public class PackageTrackerUI extends UI implements Receiver.Listener {

  final VerticalLayout mainLayout = new VerticalLayout();
  final ParallaxLayout parallaxLayout = new ParallaxLayout();
  JavaScript javaScript;

  @Autowired
  private Sender kafkaProducer;

  @Autowired
  private Receiver kafkaConsumer;

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

  @Override
  protected void init(VaadinRequest request) {
    mainLayout.setHeight("100%");
    mainLayout.setWidth("100000px");
    mainLayout.setStyleName("mainLayout");
    setContent(mainLayout);

    kafkaConsumer.addListener(this);
    javaScript = JavaScript.getCurrent();

    mainLayout.addComponent(parallaxLayout);
  }
}