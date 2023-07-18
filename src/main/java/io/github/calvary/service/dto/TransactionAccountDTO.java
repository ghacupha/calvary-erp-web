package io.github.calvary.service.dto;

import io.github.calvary.domain.enumeration.TransactionAccountType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link io.github.calvary.domain.TransactionAccount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String accountName;

    private String accountNumber;

    @NotNull
    private TransactionAccountType transactionAccountType;

    private BigDecimal openingBalance;

    private TransactionAccountDTO parentAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public TransactionAccountType getTransactionAccountType() {
        return transactionAccountType;
    }

    public void setTransactionAccountType(TransactionAccountType transactionAccountType) {
        this.transactionAccountType = transactionAccountType;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public TransactionAccountDTO getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(TransactionAccountDTO parentAccount) {
        this.parentAccount = parentAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionAccountDTO)) {
            return false;
        }

        TransactionAccountDTO transactionAccountDTO = (TransactionAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionAccountDTO{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", transactionAccountType='" + getTransactionAccountType() + "'" +
            ", openingBalance=" + getOpeningBalance() +
            ", parentAccount=" + getParentAccount() +
            "}";
    }
}
