package io.github.calvary.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.calvary.domain.AccountTransaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountTransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate transactionDate;

    private String description;

    private String referenceNumber;

    private Boolean posted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Boolean getPosted() {
        return posted;
    }

    public void setPosted(Boolean posted) {
        this.posted = posted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountTransactionDTO)) {
            return false;
        }

        AccountTransactionDTO accountTransactionDTO = (AccountTransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, accountTransactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountTransactionDTO{" +
            "id=" + getId() +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", referenceNumber='" + getReferenceNumber() + "'" +
            ", posted='" + getPosted() + "'" +
            "}";
    }
}
