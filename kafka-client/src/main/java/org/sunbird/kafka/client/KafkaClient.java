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
 * Helper class for creating a Kafka consumer and producer.
 *
 * @author Mahesh Kumar Gangula
 */
public class KafkaClient {

  /**
   * Creates a Kafka producer.
   *
   * @param bootstrapServers Comma-separated list of host and port pairs that are the addresses
   *                         of the Kafka brokers in a "bootstrap" Kafka cluster that a Kafka
   *                         client connects to initially to bootstrap itself.
   *                         e.g. localhost:9092,localhost:9093,localhost:9094
   * @param clientId Identifier for Kafka producer
   * @return A Kafka producer for given configuration.
   */
  public static Producer<Long, String> createProducer(String bootstrapServers, String clientId) {
    return new KafkaProducer<Long, String>(createProperties(bootstrapServers, clientId));
  }

  /**
   * Creates a Kafka consumer.
   *
   * @param bootstrapServers Comma-separated list of host and port pairs that are the addresses
   *                         of the Kafka brokers in a "bootstrap" Kafka cluster that a Kafka
   *                         client connects to initially to bootstrap itself.
   *                         e.g. localhost:9092,localhost:9093,localhost:9094
   * @param clientId Identifier for Kafka consumer
   * @return A Kafka consumer for given configuration.
   */
  public static Consumer<Long, String> createConsumer(String bootstrapServers, String clientId) {
    return new KafkaConsumer<>(createProperties(bootstrapServers, clientId));
  }

  /**
   * Creates a properties instance required for creating a Kafka producer or consumer.
   *
   * @param bootstrapServers Comma-separated list of host and port pairs that are the addresses
   *                         of the Kafka brokers in a "bootstrap" Kafka cluster that a Kafka
   *                         client connects to initially to bootstrap itself.
   *                         e.g. localhost:9092,localhost:9093,localhost:9094
   * @param clientId Identifier for Kafka producer or consumer
   * @return Properties required for instantiating a Kafka producer or consumer.
   */
  private static Properties createProperties(String bootstrapServers, String clientId) {
    ProjectLogger.log("KafkaClient: createProperties called with bootstrapServers = " + bootstrapServers + " clientId = " + clientId, LoggerEnum.INFO.name());
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    return props;
  }
}
