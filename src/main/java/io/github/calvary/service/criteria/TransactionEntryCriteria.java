package io.github.calvary.service.criteria;

import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.TransactionEntry} entity. This class is used
 * in {@link io.github.calvary.web.rest.TransactionEntryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transaction-entries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionEntryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TransactionEntryTypes
     */
    public static class TransactionEntryTypesFilter extends Filter<TransactionEntryTypes> {

        public TransactionEntryTypesFilter() {}

        public TransactionEntryTypesFilter(TransactionEntryTypesFilter filter) {
            super(filter);
        }

        @Override
        public TransactionEntryTypesFilter copy() {
            return new TransactionEntryTypesFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter entryAmount;

    private TransactionEntryTypesFilter transactionEntryType;

    private LongFilter transactionAccountId;

    private Boolean distinct;

    public TransactionEntryCriteria() {}

    public TransactionEntryCriteria(TransactionEntryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.entryAmount = other.entryAmount == null ? null : other.entryAmount.copy();
        this.transactionEntryType = other.transactionEntryType == null ? null : other.transactionEntryType.copy();
        this.transactionAccountId = other.transactionAccountId == null ? null : other.transactionAccountId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionEntryCriteria copy() {
        return new TransactionEntryCriteria(this);
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

    public BigDecimalFilter getEntryAmount() {
        return entryAmount;
    }

    public BigDecimalFilter entryAmount() {
        if (entryAmount == null) {
            entryAmount = new BigDecimalFilter();
        }
        return entryAmount;
    }

    public void setEntryAmount(BigDecimalFilter entryAmount) {
        this.entryAmount = entryAmount;
    }

    public TransactionEntryTypesFilter getTransactionEntryType() {
        return transactionEntryType;
    }

    public TransactionEntryTypesFilter transactionEntryType() {
        if (transactionEntryType == null) {
            transactionEntryType = new TransactionEntryTypesFilter();
        }
        return transactionEntryType;
    }

    public void setTransactionEntryType(TransactionEntryTypesFilter transactionEntryType) {
        this.transactionEntryType = transactionEntryType;
    }

    public LongFilter getTransactionAccountId() {
        return transactionAccountId;
    }

    public LongFilter transactionAccountId() {
        if (transactionAccountId == null) {
            transactionAccountId = new LongFilter();
        }
        return transactionAccountId;
    }

    public void setTransactionAccountId(LongFilter transactionAccountId) {
        this.transactionAccountId = transactionAccountId;
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
        final TransactionEntryCriteria that = (TransactionEntryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(entryAmount, that.entryAmount) &&
            Objects.equals(transactionEntryType, that.transactionEntryType) &&
            Objects.equals(transactionAccountId, that.transactionAccountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entryAmount, transactionEntryType, transactionAccountId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionEntryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (entryAmount != null ? "entryAmount=" + entryAmount + ", " : "") +
            (transactionEntryType != null ? "transactionEntryType=" + transactionEntryType + ", " : "") +
            (transactionAccountId != null ? "transactionAccountId=" + transactionAccountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
