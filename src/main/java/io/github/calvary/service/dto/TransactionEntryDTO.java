package io.github.calvary.service.dto;

import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionEntry} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionEntryDTO implements Serializable {

    private Long id;

    private BigDecimal entryAmount;

    @NotNull
    private TransactionEntryTypes transactionEntryType;

    private String description;

    private TransactionAccountDTO transactionAccount;

    private AccountTransactionDTO accountTransaction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getEntryAmount() {
        return entryAmount;
    }

    public void setEntryAmount(BigDecimal entryAmount) {
        this.entryAmount = entryAmount;
    }

    public TransactionEntryTypes getTransactionEntryType() {
        return transactionEntryType;
    }

    public void setTransactionEntryType(TransactionEntryTypes transactionEntryType) {
        this.transactionEntryType = transactionEntryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionAccountDTO getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(TransactionAccountDTO transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public AccountTransactionDTO getAccountTransaction() {
        return accountTransaction;
    }

    public void setAccountTransaction(AccountTransactionDTO accountTransaction) {
        this.accountTransaction = accountTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionEntryDTO)) {
            return false;
        }

        TransactionEntryDTO transactionEntryDTO = (TransactionEntryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionEntryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionEntryDTO{" +
            "id=" + getId() +
            ", entryAmount=" + getEntryAmount() +
            ", transactionEntryType='" + getTransactionEntryType() + "'" +
            ", description='" + getDescription() + "'" +
            ", transactionAccount=" + getTransactionAccount() +
            ", accountTransaction=" + getAccountTransaction() +
            "}";
    }
}
