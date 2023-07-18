package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionEntryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionEntryDTO.class);
        TransactionEntryDTO transactionEntryDTO1 = new TransactionEntryDTO();
        transactionEntryDTO1.setId(1L);
        TransactionEntryDTO transactionEntryDTO2 = new TransactionEntryDTO();
        assertThat(transactionEntryDTO1).isNotEqualTo(transactionEntryDTO2);
        transactionEntryDTO2.setId(transactionEntryDTO1.getId());
        assertThat(transactionEntryDTO1).isEqualTo(transactionEntryDTO2);
        transactionEntryDTO2.setId(2L);
        assertThat(transactionEntryDTO1).isNotEqualTo(transactionEntryDTO2);
        transactionEntryDTO1.setId(null);
        assertThat(transactionEntryDTO1).isNotEqualTo(transactionEntryDTO2);
    }
}
