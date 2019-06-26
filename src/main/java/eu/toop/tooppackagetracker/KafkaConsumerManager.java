package eu.toop.tooppackagetracker;

import java.util.HashMap;
import java.util.Map;

public final class KafkaConsumerManager
{
  public static final String TOPIC_GROUP_ID = "toop-group";

  private static final Map <String, Receiver> kafkaConsumers = new HashMap <> ();

  private KafkaConsumerManager ()
  {}

  public static Map <String, Receiver> getKafkaConsumers ()
  {
    return kafkaConsumers;
  }
}
