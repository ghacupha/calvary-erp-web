package io.github.calvary.erp.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class TransactionEntryMessageDeserializer implements Deserializer<TransactionEntryMessage> {

    public static final ObjectMapper mapper = JsonMapper.builder()
        .findAndAddModules()
        .build();

    @Override
    public TransactionEntryMessage deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, TransactionEntryMessage.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
