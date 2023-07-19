package io.github.calvary.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionAccountType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionAccountTypeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionAccountTypeDTO)) {
            return false;
        }

        TransactionAccountTypeDTO transactionAccountTypeDTO = (TransactionAccountTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionAccountTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionAccountTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
