package io.github.calvary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test for the Spring Cloud Stream Pulsar Binder.
 */
@IntegrationTest
public class PulsarIT {

    @Autowired
    BlockingQueue<String> testInputQueue;

    @Autowired
    BlockingQueue<String> testOutputQueue;

    @Test
    public void testPulsar() throws Exception {
        String value = UUID.randomUUID().toString();
        testInputQueue.put(value);
        assertThat(testOutputQueue.poll(5, TimeUnit.SECONDS)).isEqualTo(value);
    }
}
