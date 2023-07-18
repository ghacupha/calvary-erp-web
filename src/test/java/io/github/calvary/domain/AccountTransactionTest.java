package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountTransaction.class);
        AccountTransaction accountTransaction1 = new AccountTransaction();
        accountTransaction1.setId(1L);
        AccountTransaction accountTransaction2 = new AccountTransaction();
        accountTransaction2.setId(accountTransaction1.getId());
        assertThat(accountTransaction1).isEqualTo(accountTransaction2);
        accountTransaction2.setId(2L);
        assertThat(accountTransaction1).isNotEqualTo(accountTransaction2);
        accountTransaction1.setId(null);
        assertThat(accountTransaction1).isNotEqualTo(accountTransaction2);
    }
}
