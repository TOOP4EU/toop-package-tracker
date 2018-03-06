package eu.toop.tooppackagetracker;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import eu.toop.tooppackagetracker.detail.DetailView;
import eu.toop.tooppackagetracker.parallax.ParallaxLayout;
import eu.toop.tooppackagetracker.parallax.ParallaxView;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Theme("PackageTrackerUITheme")
@SpringUI
@SuppressWarnings("serial")
@Push
//@PreserveOnRefresh
public class PackageTrackerUI extends UI implements Receiver.Listener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PackageTrackerUI.class);

  private Navigator navigator;

  @Autowired
  private Receiver kafkaConsumer;

  @Override
  public void receive(ConsumerRecord<?, ?> consumerRecord) {

  }

  @Override
  protected void init(VaadinRequest request) {
    //kafkaConsumer.addListener(this);
    ParallaxView parallaxView = new ParallaxView (this, JavaScript.getCurrent ());
    DetailView detailView = new DetailView (this);

    navigator = new Navigator (this, this);
    navigator.addView("", parallaxView);
    navigator.addView(ParallaxView.class.getName(), parallaxView);
    navigator.addView("detail", detailView);

    kafkaConsumer.addListener(parallaxView);
    kafkaConsumer.addListener(detailView);
  }


  @Override
  public void attach() {
    super.attach();
  }

  @Override
  public void detach() {
    super.detach();
  }
}