package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BalanceSheetItemValueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BalanceSheetItemValueDTO.class);
        BalanceSheetItemValueDTO balanceSheetItemValueDTO1 = new BalanceSheetItemValueDTO();
        balanceSheetItemValueDTO1.setId(1L);
        BalanceSheetItemValueDTO balanceSheetItemValueDTO2 = new BalanceSheetItemValueDTO();
        assertThat(balanceSheetItemValueDTO1).isNotEqualTo(balanceSheetItemValueDTO2);
        balanceSheetItemValueDTO2.setId(balanceSheetItemValueDTO1.getId());
        assertThat(balanceSheetItemValueDTO1).isEqualTo(balanceSheetItemValueDTO2);
        balanceSheetItemValueDTO2.setId(2L);
        assertThat(balanceSheetItemValueDTO1).isNotEqualTo(balanceSheetItemValueDTO2);
        balanceSheetItemValueDTO1.setId(null);
        assertThat(balanceSheetItemValueDTO1).isNotEqualTo(balanceSheetItemValueDTO2);
    }
}
