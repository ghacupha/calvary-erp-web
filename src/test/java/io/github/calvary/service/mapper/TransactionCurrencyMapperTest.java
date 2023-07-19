package io.github.calvary.service.mapper;


import org.junit.jupiter.api.BeforeEach;

class TransactionCurrencyMapperTest {

    private TransactionCurrencyMapper transactionCurrencyMapper;

    @BeforeEach
    public void setUp() {
        transactionCurrencyMapper = new TransactionCurrencyMapperImpl();
    }
}
