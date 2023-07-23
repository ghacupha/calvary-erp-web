package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BalanceSheetItemValue.
 */
@Entity
@Table(name = "balance_sheet_item_value")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "balancesheetitemvalue")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BalanceSheetItemValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "short_description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String shortDescription;

    @NotNull
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @NotNull
    @Column(name = "item_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal itemAmount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "transactionAccount", "parentItem" }, allowSetters = true)
    private BalanceSheetItemType itemType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BalanceSheetItemValue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public BalanceSheetItemValue shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public LocalDate getEffectiveDate() {
        return this.effectiveDate;
    }

    public BalanceSheetItemValue effectiveDate(LocalDate effectiveDate) {
        this.setEffectiveDate(effectiveDate);
        return this;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimal getItemAmount() {
        return this.itemAmount;
    }

    public BalanceSheetItemValue itemAmount(BigDecimal itemAmount) {
        this.setItemAmount(itemAmount);
        return this;
    }

    public void setItemAmount(BigDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }

    public BalanceSheetItemType getItemType() {
        return this.itemType;
    }

    public void setItemType(BalanceSheetItemType balanceSheetItemType) {
        this.itemType = balanceSheetItemType;
    }

    public BalanceSheetItemValue itemType(BalanceSheetItemType balanceSheetItemType) {
        this.setItemType(balanceSheetItemType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BalanceSheetItemValue)) {
            return false;
        }
        return id != null && id.equals(((BalanceSheetItemValue) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BalanceSheetItemValue{" +
            "id=" + getId() +
            ", shortDescription='" + getShortDescription() + "'" +
            ", effectiveDate='" + getEffectiveDate() + "'" +
            ", itemAmount=" + getItemAmount() +
            "}";
    }
}
