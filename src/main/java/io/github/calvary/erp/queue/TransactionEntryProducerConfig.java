package io.github.calvary.erp.queue;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class TransactionEntryProducerConfig {

    @Value("${queue.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Set Kafka producer properties
    @Bean
    public ProducerFactory<String, TransactionEntryMessage> producerFactory() throws ClassNotFoundException {
        DefaultKafkaProducerFactory<String, TransactionEntryMessage> producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs());
        producerFactory.setKeySerializer(new StringSerializer());
        producerFactory.setValueSerializer(new TransactionEntryMessageSerializer());
        return producerFactory;
    }

    // Define Kafka producer configurations
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Add additional properties as needed
        return properties;
    }

    // Create KafkaTemplate for producing messages
    @Bean("transactionEntryMessageKafkaTemplate")
    public KafkaTemplate<String, TransactionEntryMessage> kafkaTemplate() throws ClassNotFoundException {
        return new KafkaTemplate<>(producerFactory());
    }
}
