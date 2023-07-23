package io.github.calvary.erp.queue;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "queue.kafka.consumer")
public class TransactionEntryMessageConsumerConfig {

    @Value("${queue.transaction-entry.topic}")
    private String topicName;

    @Value("${queue.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${queue.kafka.consumer.group.id}")
    private String groupId;

    // Set Kafka consumer properties
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEntryMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionEntryMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Set other properties on the factory if needed
        return factory;
    }


    @Bean
    public ContainerProperties containerProperties() {
        ContainerProperties containerProperties = new ContainerProperties(topicName);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        // containerProperties.setListenerMode(ListenerMode.BATCH);
        // containerProperties.setAckOnError(false);
        containerProperties.setPollTimeout(3000);
        // containerProperties.setErrorHandler(new SeekToCurrentErrorHandler());
        return containerProperties;
    }

    // Define Kafka consumer configurations
    @SneakyThrows
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Add additional properties as needed
        return properties;
    }

    // Create Kafka consumer factory
    @Bean
    public DefaultKafkaConsumerFactory<String, TransactionEntryMessage> consumerFactory() {
        DefaultKafkaConsumerFactory<String, TransactionEntryMessage> consumerFactory =  new DefaultKafkaConsumerFactory<>(consumerConfigs());

        consumerFactory.setKeyDeserializer(new StringDeserializer());
        consumerFactory.setValueDeserializer(new TransactionEntryMessageDeserializer());
        return consumerFactory;
    }
}
