package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionAccountTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionAccountType.class);
        TransactionAccountType transactionAccountType1 = new TransactionAccountType();
        transactionAccountType1.setId(1L);
        TransactionAccountType transactionAccountType2 = new TransactionAccountType();
        transactionAccountType2.setId(transactionAccountType1.getId());
        assertThat(transactionAccountType1).isEqualTo(transactionAccountType2);
        transactionAccountType2.setId(2L);
        assertThat(transactionAccountType1).isNotEqualTo(transactionAccountType2);
        transactionAccountType1.setId(null);
        assertThat(transactionAccountType1).isNotEqualTo(transactionAccountType2);
    }
}
