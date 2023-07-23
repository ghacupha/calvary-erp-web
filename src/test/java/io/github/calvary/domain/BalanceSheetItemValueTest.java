package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BalanceSheetItemValueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BalanceSheetItemValue.class);
        BalanceSheetItemValue balanceSheetItemValue1 = new BalanceSheetItemValue();
        balanceSheetItemValue1.setId(1L);
        BalanceSheetItemValue balanceSheetItemValue2 = new BalanceSheetItemValue();
        balanceSheetItemValue2.setId(balanceSheetItemValue1.getId());
        assertThat(balanceSheetItemValue1).isEqualTo(balanceSheetItemValue2);
        balanceSheetItemValue2.setId(2L);
        assertThat(balanceSheetItemValue1).isNotEqualTo(balanceSheetItemValue2);
        balanceSheetItemValue1.setId(null);
        assertThat(balanceSheetItemValue1).isNotEqualTo(balanceSheetItemValue2);
    }
}
