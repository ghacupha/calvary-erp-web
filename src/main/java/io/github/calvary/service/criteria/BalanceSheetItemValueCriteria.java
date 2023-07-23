package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.BalanceSheetItemValue} entity. This class is used
 * in {@link io.github.calvary.web.rest.BalanceSheetItemValueResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /balance-sheet-item-values?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BalanceSheetItemValueCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter shortDescription;

    private LocalDateFilter effectiveDate;

    private BigDecimalFilter itemAmount;

    private LongFilter itemTypeId;

    private Boolean distinct;

    public BalanceSheetItemValueCriteria() {}

    public BalanceSheetItemValueCriteria(BalanceSheetItemValueCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.shortDescription = other.shortDescription == null ? null : other.shortDescription.copy();
        this.effectiveDate = other.effectiveDate == null ? null : other.effectiveDate.copy();
        this.itemAmount = other.itemAmount == null ? null : other.itemAmount.copy();
        this.itemTypeId = other.itemTypeId == null ? null : other.itemTypeId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BalanceSheetItemValueCriteria copy() {
        return new BalanceSheetItemValueCriteria(this);
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

    public LocalDateFilter getEffectiveDate() {
        return effectiveDate;
    }

    public LocalDateFilter effectiveDate() {
        if (effectiveDate == null) {
            effectiveDate = new LocalDateFilter();
        }
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateFilter effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimalFilter getItemAmount() {
        return itemAmount;
    }

    public BigDecimalFilter itemAmount() {
        if (itemAmount == null) {
            itemAmount = new BigDecimalFilter();
        }
        return itemAmount;
    }

    public void setItemAmount(BigDecimalFilter itemAmount) {
        this.itemAmount = itemAmount;
    }

    public LongFilter getItemTypeId() {
        return itemTypeId;
    }

    public LongFilter itemTypeId() {
        if (itemTypeId == null) {
            itemTypeId = new LongFilter();
        }
        return itemTypeId;
    }

    public void setItemTypeId(LongFilter itemTypeId) {
        this.itemTypeId = itemTypeId;
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
        final BalanceSheetItemValueCriteria that = (BalanceSheetItemValueCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(shortDescription, that.shortDescription) &&
            Objects.equals(effectiveDate, that.effectiveDate) &&
            Objects.equals(itemAmount, that.itemAmount) &&
            Objects.equals(itemTypeId, that.itemTypeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortDescription, effectiveDate, itemAmount, itemTypeId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BalanceSheetItemValueCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (shortDescription != null ? "shortDescription=" + shortDescription + ", " : "") +
            (effectiveDate != null ? "effectiveDate=" + effectiveDate + ", " : "") +
            (itemAmount != null ? "itemAmount=" + itemAmount + ", " : "") +
            (itemTypeId != null ? "itemTypeId=" + itemTypeId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
