package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountingEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountingEventDTO.class);
        AccountingEventDTO accountingEventDTO1 = new AccountingEventDTO();
        accountingEventDTO1.setId(1L);
        AccountingEventDTO accountingEventDTO2 = new AccountingEventDTO();
        assertThat(accountingEventDTO1).isNotEqualTo(accountingEventDTO2);
        accountingEventDTO2.setId(accountingEventDTO1.getId());
        assertThat(accountingEventDTO1).isEqualTo(accountingEventDTO2);
        accountingEventDTO2.setId(2L);
        assertThat(accountingEventDTO1).isNotEqualTo(accountingEventDTO2);
        accountingEventDTO1.setId(null);
        assertThat(accountingEventDTO1).isNotEqualTo(accountingEventDTO2);
    }
}
