package eu.toop.tooppackagetracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
  private CountDownLatch latch = new CountDownLatch(1);
  private List<Listener> listeners = new ArrayList<>();

  public CountDownLatch getLatch() {
    return latch;
  }

  @KafkaListener(topics = "toop")
  public void receive(ConsumerRecord<?, ?> consumerRecord) {
    LOGGER.info("received payload='{}'", consumerRecord.toString());

    for (Listener listener : listeners) {
      listener.receive(consumerRecord);
    }

    latch.countDown();
  }

  public interface Listener {
    void receive(ConsumerRecord<?, ?> consumerRecord);
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }
}

