package io.github.calvary.erp.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEntryProducer implements Messenger<TransactionEntryMessage> {

    @Value("${queue.transaction-entry.topic}")
    private String topicName;

    private static final Logger log = LoggerFactory.getLogger(TransactionEntryProducer.class);

    private final KafkaTemplate<String, TransactionEntryMessage> kafkaTemplate;

    public TransactionEntryProducer (
        @Qualifier("transactionEntryMessageKafkaTemplate") KafkaTemplate<String, TransactionEntryMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(TransactionEntryMessage transactionEntryMessage) {

        kafkaTemplate.send(topicName, transactionEntryMessage);
    }
}
