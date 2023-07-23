package io.github.calvary.erp.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

class TransactionEntryMessageSerializer implements Serializer<TransactionEntryMessage> {

    public static final ObjectMapper mapper = JsonMapper.builder()
        .findAndAddModules()
        .build();

    @Override
    public byte[] serialize(String topic, TransactionEntryMessage message) {
        try {
            return mapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e);
        }
    }
}
