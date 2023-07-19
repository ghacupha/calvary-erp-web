package io.github.calvary.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DealerTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DealerTypeDTO.class);
        DealerTypeDTO dealerTypeDTO1 = new DealerTypeDTO();
        dealerTypeDTO1.setId(1L);
        DealerTypeDTO dealerTypeDTO2 = new DealerTypeDTO();
        assertThat(dealerTypeDTO1).isNotEqualTo(dealerTypeDTO2);
        dealerTypeDTO2.setId(dealerTypeDTO1.getId());
        assertThat(dealerTypeDTO1).isEqualTo(dealerTypeDTO2);
        dealerTypeDTO2.setId(2L);
        assertThat(dealerTypeDTO1).isNotEqualTo(dealerTypeDTO2);
        dealerTypeDTO1.setId(null);
        assertThat(dealerTypeDTO1).isNotEqualTo(dealerTypeDTO2);
    }
}
