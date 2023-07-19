package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionCurrencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionCurrency.class);
        TransactionCurrency transactionCurrency1 = new TransactionCurrency();
        transactionCurrency1.setId(1L);
        TransactionCurrency transactionCurrency2 = new TransactionCurrency();
        transactionCurrency2.setId(transactionCurrency1.getId());
        assertThat(transactionCurrency1).isEqualTo(transactionCurrency2);
        transactionCurrency2.setId(2L);
        assertThat(transactionCurrency1).isNotEqualTo(transactionCurrency2);
        transactionCurrency1.setId(null);
        assertThat(transactionCurrency1).isNotEqualTo(transactionCurrency2);
    }
}
