package eu.toop.tooppackagetracker.detail;

import com.vaadin.navigator.View;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.toop.tooppackagetracker.Receiver;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailView extends VerticalLayout implements View, Receiver.Listener {

  private UI _ui;
  private VerticalLayout logLayout = new VerticalLayout ();

  public DetailView (UI ui) {
    _ui = ui;

    setSizeUndefined ();
    setWidth ("100%");

    logLayout.setSizeUndefined ();
    logLayout.setWidth ("100%");

    Label titleLabel = new Label ("Playground Package Tracker Detail log");
    titleLabel.setStyleName ("titleLabel");
    addComponent (titleLabel);

    logLayout.setStyleName ("logLayout");
    addComponent (logLayout);
  }

  @Override
  public void receive (ConsumerRecord<?, ?> consumerRecord) {
    String message = consumerRecord.value().toString();

    DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();

    Label logLabel = new Label (dateFormat.format(date) + " : " + message);
    logLabel.setStyleName ("logLabel");
    logLayout.addComponent (logLabel);

    _ui.access(new Runnable() {
      @Override
      public void run() {
        _ui.getCurrent ().push();
      }
    });
  }
}
