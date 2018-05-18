package org.sunbird.kafka.client;

import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

/**
 * @author Mahesh Kumar Gangula This class will create Kafka consumer and producer object. and
 *     provide to caller. For each call it will create new consumer and producer object.
 */
public class KafkaClient {

  private static Producer<Long, String> createProducer(String bootstrapServers, String clientId) {
    return new KafkaProducer<Long, String>(createPropertiesObj(bootstrapServers, clientId));
  }

  private static Consumer<Long, String> createConsumer(String bootstrapServers, String clientId) {
    return new KafkaConsumer<>(createPropertiesObj(bootstrapServers, clientId));
  }

  /**
   * his method will create a producer object and return to caller.
   *
   * @param bootstrapServers String EX:localhost:9092,localhost:9093,localhost:9094
   * @param clientId String unique string value
   * @return Producer<Long, String>
   */
  public static Producer<Long, String> initProducer(String bootstrapServers, String clientId) {
    return createProducer(bootstrapServers, clientId);
  }

  /**
   * This method will create a consumer object and return to caller.
   *
   * @param bootstrapServers String EX:localhost:9092,localhost:9093,localhost:9094
   * @param clientId String unique string value
   * @return Consumer<Long, String>
   */
  public static Consumer<Long, String> initConsumer(String bootstrapServers, String clientId) {
    return createConsumer(bootstrapServers, clientId);
  }

  /**
   * This method will generate properties object based on passed bootstrapServer and clientId
   * details.
   *
   * @param bootstrapServers String EX:localhost:9092,localhost:9093,localhost:9094
   * @param clientId String unique string value
   * @return Properties
   */
  private static Properties createPropertiesObj(String bootstrapServers, String clientId) {
    ProjectLogger.log(
        "Kafka server config: and topic name " + bootstrapServers + " Topic--" + clientId,
        LoggerEnum.INFO.name());
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    return props;
  }
}
