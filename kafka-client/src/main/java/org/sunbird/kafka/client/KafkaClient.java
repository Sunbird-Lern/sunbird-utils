package org.sunbird.kafka.client;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

/** @author Mahesh Kumar Gangula */
public class KafkaClient {

  private static String BOOTSTRAP_SERVERS = System.getenv("sunbird_telemetry_kafka_servers_config");
  private static String topic = System.getenv("sunbird_telemetry_kafka_topic");
  private static Producer<Long, String> producer;
  private static Consumer<Long, String> consumer;

  static {
    if (StringUtils.isNotBlank(BOOTSTRAP_SERVERS) && StringUtils.isNotBlank(topic)) {
      createProducer();
      createConsumer();
      listTopics();
    } else {
      ProjectLogger.log(
          "either BOOTSTRAP_SERVERS or topic is empty=="
              + StringUtils.isNotBlank(BOOTSTRAP_SERVERS)
              + "  "
              + StringUtils.isNotBlank(topic),
          LoggerEnum.INFO.name());
    }
  }

  private static void createProducer() {
    ProjectLogger.log("Kafka server config: " + BOOTSTRAP_SERVERS, LoggerEnum.INFO.name());
    ProjectLogger.log("Kafka topic name: " + topic, LoggerEnum.INFO.name());
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaClientProducer");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    producer = new KafkaProducer<Long, String>(props);
  }

  private static void createConsumer() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, "KafkaClientConsumer");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumer = new KafkaConsumer<>(props);
  }

  public static Producer<Long, String> getProducer() {
    return producer;
  }

  protected static Consumer<Long, String> getConsumer() {
    return consumer;
  }

  public static void listTopics() {
    Consumer<Long, String> consumer = getConsumer();
    Map<String, List<PartitionInfo>> topics = consumer.listTopics();
    ProjectLogger.log("Topics list from kafka: " + topics.keySet(), LoggerEnum.INFO.name());
  }

  public static String getTopic() {
    return topic;
  }
}
