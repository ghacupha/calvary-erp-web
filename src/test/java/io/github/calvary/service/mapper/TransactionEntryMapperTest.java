package io.github.calvary.service.mapper;


import org.junit.jupiter.api.BeforeEach;

class TransactionEntryMapperTest {

    private TransactionEntryMapper transactionEntryMapper;

    @BeforeEach
    public void setUp() {
        transactionEntryMapper = new TransactionEntryMapperImpl();
    }
}
