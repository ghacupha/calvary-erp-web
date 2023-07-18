package io.github.calvary.service.mapper;


import org.junit.jupiter.api.BeforeEach;

class TransactionAccountMapperTest {

    private TransactionAccountMapper transactionAccountMapper;

    @BeforeEach
    public void setUp() {
        transactionAccountMapper = new TransactionAccountMapperImpl();
    }
}
