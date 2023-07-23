package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BalanceSheetItemTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BalanceSheetItemType.class);
        BalanceSheetItemType balanceSheetItemType1 = new BalanceSheetItemType();
        balanceSheetItemType1.setId(1L);
        BalanceSheetItemType balanceSheetItemType2 = new BalanceSheetItemType();
        balanceSheetItemType2.setId(balanceSheetItemType1.getId());
        assertThat(balanceSheetItemType1).isEqualTo(balanceSheetItemType2);
        balanceSheetItemType2.setId(2L);
        assertThat(balanceSheetItemType1).isNotEqualTo(balanceSheetItemType2);
        balanceSheetItemType1.setId(null);
        assertThat(balanceSheetItemType1).isNotEqualTo(balanceSheetItemType2);
    }
}
