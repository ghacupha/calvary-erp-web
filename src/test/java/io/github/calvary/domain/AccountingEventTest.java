package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountingEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountingEvent.class);
        AccountingEvent accountingEvent1 = new AccountingEvent();
        accountingEvent1.setId(1L);
        AccountingEvent accountingEvent2 = new AccountingEvent();
        accountingEvent2.setId(accountingEvent1.getId());
        assertThat(accountingEvent1).isEqualTo(accountingEvent2);
        accountingEvent2.setId(2L);
        assertThat(accountingEvent1).isNotEqualTo(accountingEvent2);
        accountingEvent1.setId(null);
        assertThat(accountingEvent1).isNotEqualTo(accountingEvent2);
    }
}
