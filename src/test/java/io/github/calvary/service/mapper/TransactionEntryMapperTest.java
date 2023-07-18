package io.github.calvary.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionEntryMapperTest {

    private TransactionEntryMapper transactionEntryMapper;

    @BeforeEach
    public void setUp() {
        transactionEntryMapper = new TransactionEntryMapperImpl();
    }
}
