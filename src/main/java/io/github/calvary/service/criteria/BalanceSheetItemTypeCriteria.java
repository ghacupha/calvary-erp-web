package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.BalanceSheetItemType} entity. This class is used
 * in {@link io.github.calvary.web.rest.BalanceSheetItemTypeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /balance-sheet-item-types?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BalanceSheetItemTypeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter itemSequence;

    private StringFilter itemNumber;

    private StringFilter shortDescription;

    private LongFilter transactionAccountId;

    private LongFilter parentItemId;

    private Boolean distinct;

    public BalanceSheetItemTypeCriteria() {}

    public BalanceSheetItemTypeCriteria(BalanceSheetItemTypeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.itemSequence = other.itemSequence == null ? null : other.itemSequence.copy();
        this.itemNumber = other.itemNumber == null ? null : other.itemNumber.copy();
        this.shortDescription = other.shortDescription == null ? null : other.shortDescription.copy();
        this.transactionAccountId = other.transactionAccountId == null ? null : other.transactionAccountId.copy();
        this.parentItemId = other.parentItemId == null ? null : other.parentItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BalanceSheetItemTypeCriteria copy() {
        return new BalanceSheetItemTypeCriteria(this);
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

    public IntegerFilter getItemSequence() {
        return itemSequence;
    }

    public IntegerFilter itemSequence() {
        if (itemSequence == null) {
            itemSequence = new IntegerFilter();
        }
        return itemSequence;
    }

    public void setItemSequence(IntegerFilter itemSequence) {
        this.itemSequence = itemSequence;
    }

    public StringFilter getItemNumber() {
        return itemNumber;
    }

    public StringFilter itemNumber() {
        if (itemNumber == null) {
            itemNumber = new StringFilter();
        }
        return itemNumber;
    }

    public void setItemNumber(StringFilter itemNumber) {
        this.itemNumber = itemNumber;
    }

    public StringFilter getShortDescription() {
        return shortDescription;
    }

    public StringFilter shortDescription() {
        if (shortDescription == null) {
            shortDescription = new StringFilter();
        }
        return shortDescription;
    }

    public void setShortDescription(StringFilter shortDescription) {
        this.shortDescription = shortDescription;
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

    public LongFilter getParentItemId() {
        return parentItemId;
    }

    public LongFilter parentItemId() {
        if (parentItemId == null) {
            parentItemId = new LongFilter();
        }
        return parentItemId;
    }

    public void setParentItemId(LongFilter parentItemId) {
        this.parentItemId = parentItemId;
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
        final BalanceSheetItemTypeCriteria that = (BalanceSheetItemTypeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(itemSequence, that.itemSequence) &&
            Objects.equals(itemNumber, that.itemNumber) &&
            Objects.equals(shortDescription, that.shortDescription) &&
            Objects.equals(transactionAccountId, that.transactionAccountId) &&
            Objects.equals(parentItemId, that.parentItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemSequence, itemNumber, shortDescription, transactionAccountId, parentItemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BalanceSheetItemTypeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (itemSequence != null ? "itemSequence=" + itemSequence + ", " : "") +
            (itemNumber != null ? "itemNumber=" + itemNumber + ", " : "") +
            (shortDescription != null ? "shortDescription=" + shortDescription + ", " : "") +
            (transactionAccountId != null ? "transactionAccountId=" + transactionAccountId + ", " : "") +
            (parentItemId != null ? "parentItemId=" + parentItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
