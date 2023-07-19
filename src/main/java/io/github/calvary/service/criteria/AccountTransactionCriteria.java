package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.AccountTransaction} entity. This class is used
 * in {@link io.github.calvary.web.rest.AccountTransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /account-transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountTransactionCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter transactionDate;

    private StringFilter description;

    private StringFilter referenceNumber;

    private BooleanFilter wasProposed;

    private BooleanFilter wasPosted;

    private BooleanFilter wasDeleted;

    private BooleanFilter wasApproved;

    private LongFilter transactionEntryId;

    private Boolean distinct;

    public AccountTransactionCriteria() {}

    public AccountTransactionCriteria(AccountTransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.transactionDate = other.transactionDate == null ? null : other.transactionDate.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.referenceNumber = other.referenceNumber == null ? null : other.referenceNumber.copy();
        this.wasProposed = other.wasProposed == null ? null : other.wasProposed.copy();
        this.wasPosted = other.wasPosted == null ? null : other.wasPosted.copy();
        this.wasDeleted = other.wasDeleted == null ? null : other.wasDeleted.copy();
        this.wasApproved = other.wasApproved == null ? null : other.wasApproved.copy();
        this.transactionEntryId = other.transactionEntryId == null ? null : other.transactionEntryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AccountTransactionCriteria copy() {
        return new AccountTransactionCriteria(this);
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

    public LocalDateFilter getTransactionDate() {
        return transactionDate;
    }

    public LocalDateFilter transactionDate() {
        if (transactionDate == null) {
            transactionDate = new LocalDateFilter();
        }
        return transactionDate;
    }

    public void setTransactionDate(LocalDateFilter transactionDate) {
        this.transactionDate = transactionDate;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getReferenceNumber() {
        return referenceNumber;
    }

    public StringFilter referenceNumber() {
        if (referenceNumber == null) {
            referenceNumber = new StringFilter();
        }
        return referenceNumber;
    }

    public void setReferenceNumber(StringFilter referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public BooleanFilter getWasProposed() {
        return wasProposed;
    }

    public BooleanFilter wasProposed() {
        if (wasProposed == null) {
            wasProposed = new BooleanFilter();
        }
        return wasProposed;
    }

    public void setWasProposed(BooleanFilter wasProposed) {
        this.wasProposed = wasProposed;
    }

    public BooleanFilter getWasPosted() {
        return wasPosted;
    }

    public BooleanFilter wasPosted() {
        if (wasPosted == null) {
            wasPosted = new BooleanFilter();
        }
        return wasPosted;
    }

    public void setWasPosted(BooleanFilter wasPosted) {
        this.wasPosted = wasPosted;
    }

    public BooleanFilter getWasDeleted() {
        return wasDeleted;
    }

    public BooleanFilter wasDeleted() {
        if (wasDeleted == null) {
            wasDeleted = new BooleanFilter();
        }
        return wasDeleted;
    }

    public void setWasDeleted(BooleanFilter wasDeleted) {
        this.wasDeleted = wasDeleted;
    }

    public BooleanFilter getWasApproved() {
        return wasApproved;
    }

    public BooleanFilter wasApproved() {
        if (wasApproved == null) {
            wasApproved = new BooleanFilter();
        }
        return wasApproved;
    }

    public void setWasApproved(BooleanFilter wasApproved) {
        this.wasApproved = wasApproved;
    }

    public LongFilter getTransactionEntryId() {
        return transactionEntryId;
    }

    public LongFilter transactionEntryId() {
        if (transactionEntryId == null) {
            transactionEntryId = new LongFilter();
        }
        return transactionEntryId;
    }

    public void setTransactionEntryId(LongFilter transactionEntryId) {
        this.transactionEntryId = transactionEntryId;
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
        final AccountTransactionCriteria that = (AccountTransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(transactionDate, that.transactionDate) &&
            Objects.equals(description, that.description) &&
            Objects.equals(referenceNumber, that.referenceNumber) &&
            Objects.equals(wasProposed, that.wasProposed) &&
            Objects.equals(wasPosted, that.wasPosted) &&
            Objects.equals(wasDeleted, that.wasDeleted) &&
            Objects.equals(wasApproved, that.wasApproved) &&
            Objects.equals(transactionEntryId, that.transactionEntryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            transactionDate,
            description,
            referenceNumber,
            wasProposed,
            wasPosted,
            wasDeleted,
            wasApproved,
            transactionEntryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountTransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (transactionDate != null ? "transactionDate=" + transactionDate + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (referenceNumber != null ? "referenceNumber=" + referenceNumber + ", " : "") +
            (wasProposed != null ? "wasProposed=" + wasProposed + ", " : "") +
            (wasPosted != null ? "wasPosted=" + wasPosted + ", " : "") +
            (wasDeleted != null ? "wasDeleted=" + wasDeleted + ", " : "") +
            (wasApproved != null ? "wasApproved=" + wasApproved + ", " : "") +
            (transactionEntryId != null ? "transactionEntryId=" + transactionEntryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
