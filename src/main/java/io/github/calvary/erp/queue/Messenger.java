package io.github.calvary.erp.queue;

public interface Messenger<T> {

    void sendMessage(T message);
}
