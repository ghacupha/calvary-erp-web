package io.github.calvary.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.calvary.domain.Dealer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DealerDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private DealerTypeDTO dealerType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DealerTypeDTO getDealerType() {
        return dealerType;
    }

    public void setDealerType(DealerTypeDTO dealerType) {
        this.dealerType = dealerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DealerDTO)) {
            return false;
        }

        DealerDTO dealerDTO = (DealerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dealerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DealerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", dealerType=" + getDealerType() +
            "}";
    }
}
