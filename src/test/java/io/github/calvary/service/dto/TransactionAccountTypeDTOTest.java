package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionAccountTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionAccountTypeDTO.class);
        TransactionAccountTypeDTO transactionAccountTypeDTO1 = new TransactionAccountTypeDTO();
        transactionAccountTypeDTO1.setId(1L);
        TransactionAccountTypeDTO transactionAccountTypeDTO2 = new TransactionAccountTypeDTO();
        assertThat(transactionAccountTypeDTO1).isNotEqualTo(transactionAccountTypeDTO2);
        transactionAccountTypeDTO2.setId(transactionAccountTypeDTO1.getId());
        assertThat(transactionAccountTypeDTO1).isEqualTo(transactionAccountTypeDTO2);
        transactionAccountTypeDTO2.setId(2L);
        assertThat(transactionAccountTypeDTO1).isNotEqualTo(transactionAccountTypeDTO2);
        transactionAccountTypeDTO1.setId(null);
        assertThat(transactionAccountTypeDTO1).isNotEqualTo(transactionAccountTypeDTO2);
    }
}
