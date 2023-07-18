package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountTransactionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountTransactionDTO.class);
        AccountTransactionDTO accountTransactionDTO1 = new AccountTransactionDTO();
        accountTransactionDTO1.setId(1L);
        AccountTransactionDTO accountTransactionDTO2 = new AccountTransactionDTO();
        assertThat(accountTransactionDTO1).isNotEqualTo(accountTransactionDTO2);
        accountTransactionDTO2.setId(accountTransactionDTO1.getId());
        assertThat(accountTransactionDTO1).isEqualTo(accountTransactionDTO2);
        accountTransactionDTO2.setId(2L);
        assertThat(accountTransactionDTO1).isNotEqualTo(accountTransactionDTO2);
        accountTransactionDTO1.setId(null);
        assertThat(accountTransactionDTO1).isNotEqualTo(accountTransactionDTO2);
    }
}
