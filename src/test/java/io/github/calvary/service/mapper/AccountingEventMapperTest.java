package io.github.calvary.service.mapper;


import org.junit.jupiter.api.BeforeEach;

class AccountingEventMapperTest {

    private AccountingEventMapper accountingEventMapper;

    @BeforeEach
    public void setUp() {
        accountingEventMapper = new AccountingEventMapperImpl();
    }
}
