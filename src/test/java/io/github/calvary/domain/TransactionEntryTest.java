package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionEntry.class);
        TransactionEntry transactionEntry1 = new TransactionEntry();
        transactionEntry1.setId(1L);
        TransactionEntry transactionEntry2 = new TransactionEntry();
        transactionEntry2.setId(transactionEntry1.getId());
        assertThat(transactionEntry1).isEqualTo(transactionEntry2);
        transactionEntry2.setId(2L);
        assertThat(transactionEntry1).isNotEqualTo(transactionEntry2);
        transactionEntry1.setId(null);
        assertThat(transactionEntry1).isNotEqualTo(transactionEntry2);
    }
}
