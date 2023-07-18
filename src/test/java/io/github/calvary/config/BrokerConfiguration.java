package io.github.calvary.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfiguration {

    @Bean
    public BlockingQueue<String> testInputQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public BlockingQueue<String> testOutputQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public Supplier<String> testStringSupplier(BlockingQueue<String> testInputQueue) {
        return testInputQueue::poll;
    }

    @Bean
    public Consumer<String> testStringConsumer(BlockingQueue<String> testOutputQueue) {
        return testOutputQueue::offer;
    }
}
