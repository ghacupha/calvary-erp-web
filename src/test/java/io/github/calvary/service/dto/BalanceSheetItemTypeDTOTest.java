package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BalanceSheetItemTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BalanceSheetItemTypeDTO.class);
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO1 = new BalanceSheetItemTypeDTO();
        balanceSheetItemTypeDTO1.setId(1L);
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO2 = new BalanceSheetItemTypeDTO();
        assertThat(balanceSheetItemTypeDTO1).isNotEqualTo(balanceSheetItemTypeDTO2);
        balanceSheetItemTypeDTO2.setId(balanceSheetItemTypeDTO1.getId());
        assertThat(balanceSheetItemTypeDTO1).isEqualTo(balanceSheetItemTypeDTO2);
        balanceSheetItemTypeDTO2.setId(2L);
        assertThat(balanceSheetItemTypeDTO1).isNotEqualTo(balanceSheetItemTypeDTO2);
        balanceSheetItemTypeDTO1.setId(null);
        assertThat(balanceSheetItemTypeDTO1).isNotEqualTo(balanceSheetItemTypeDTO2);
    }
}
