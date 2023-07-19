package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionCurrencyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionCurrencyDTO.class);
        TransactionCurrencyDTO transactionCurrencyDTO1 = new TransactionCurrencyDTO();
        transactionCurrencyDTO1.setId(1L);
        TransactionCurrencyDTO transactionCurrencyDTO2 = new TransactionCurrencyDTO();
        assertThat(transactionCurrencyDTO1).isNotEqualTo(transactionCurrencyDTO2);
        transactionCurrencyDTO2.setId(transactionCurrencyDTO1.getId());
        assertThat(transactionCurrencyDTO1).isEqualTo(transactionCurrencyDTO2);
        transactionCurrencyDTO2.setId(2L);
        assertThat(transactionCurrencyDTO1).isNotEqualTo(transactionCurrencyDTO2);
        transactionCurrencyDTO1.setId(null);
        assertThat(transactionCurrencyDTO1).isNotEqualTo(transactionCurrencyDTO2);
    }
}
