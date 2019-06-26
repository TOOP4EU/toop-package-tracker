package eu.toop.tooppackagetracker;

import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerManager
{
  private static final Map <String, Receiver> kafkaConsumers = new HashMap <> ();

  private KafkaConsumerManager ()
  {}

  public static Map <String, Receiver> getKafkaConsumers ()
  {
    return kafkaConsumers;
  }
}
