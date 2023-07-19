package io.github.calvary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.calvary.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DealerTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DealerType.class);
        DealerType dealerType1 = new DealerType();
        dealerType1.setId(1L);
        DealerType dealerType2 = new DealerType();
        dealerType2.setId(dealerType1.getId());
        assertThat(dealerType1).isEqualTo(dealerType2);
        dealerType2.setId(2L);
        assertThat(dealerType1).isNotEqualTo(dealerType2);
        dealerType1.setId(null);
        assertThat(dealerType1).isNotEqualTo(dealerType2);
    }
}
