package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.TransactionAccount} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionAccountResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-accounts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionAccountCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter accountName;

    private StringFilter accountNumber;

    private BigDecimalFilter accountBalance;

    private LongFilter parentAccountId;

    private Boolean distinct;

    public TransactionAccountCriteria() {}

    public TransactionAccountCriteria(TransactionAccountCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.accountName = other.accountName == null ? null : other.accountName.copy();
        this.accountNumber = other.accountNumber == null ? null : other.accountNumber.copy();
        this.accountBalance = other.accountBalance == null ? null : other.accountBalance.copy();
        this.parentAccountId = other.parentAccountId == null ? null : other.parentAccountId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionAccountCriteria copy() {
        return new TransactionAccountCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAccountName() {
        return accountName;
    }

    public StringFilter accountName() {
        if (accountName == null) {
            accountName = new StringFilter();
        }
        return accountName;
    }

    public void setAccountName(StringFilter accountName) {
        this.accountName = accountName;
    }

    public StringFilter getAccountNumber() {
        return accountNumber;
    }

    public StringFilter accountNumber() {
        if (accountNumber == null) {
            accountNumber = new StringFilter();
        }
        return accountNumber;
    }

    public void setAccountNumber(StringFilter accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimalFilter getAccountBalance() {
        return accountBalance;
    }

    public BigDecimalFilter accountBalance() {
        if (accountBalance == null) {
            accountBalance = new BigDecimalFilter();
        }
        return accountBalance;
    }

    public void setAccountBalance(BigDecimalFilter accountBalance) {
        this.accountBalance = accountBalance;
    }

    public LongFilter getParentAccountId() {
        return parentAccountId;
    }

    public LongFilter parentAccountId() {
        if (parentAccountId == null) {
            parentAccountId = new LongFilter();
        }
        return parentAccountId;
    }

    public void setParentAccountId(LongFilter parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionAccountCriteria that = (TransactionAccountCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(accountName, that.accountName) &&
            Objects.equals(accountNumber, that.accountNumber) &&
            Objects.equals(accountBalance, that.accountBalance) &&
            Objects.equals(parentAccountId, that.parentAccountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountName, accountNumber, accountBalance, parentAccountId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionAccountCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (accountName != null ? "accountName=" + accountName + ", " : "") +
            (accountNumber != null ? "accountNumber=" + accountNumber + ", " : "") +
            (accountBalance != null ? "accountBalance=" + accountBalance + ", " : "") +
            (parentAccountId != null ? "parentAccountId=" + parentAccountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
