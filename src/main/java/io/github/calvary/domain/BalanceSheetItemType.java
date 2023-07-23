package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BalanceSheetItemType.
 */
@Entity
@Table(name = "balance_sheet_item_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "balancesheetitemtype")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BalanceSheetItemType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "item_sequence", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer itemSequence;

    @NotNull
    @Column(name = "item_number", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String itemNumber;

    @Column(name = "short_description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String shortDescription;

    @JsonIgnoreProperties(
        value = { "parentAccount", "transactionAccountType", "transactionCurrency", "balanceSheetItemType" },
        allowSetters = true
    )
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private TransactionAccount transactionAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "transactionAccount", "parentItem" }, allowSetters = true)
    private BalanceSheetItemType parentItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BalanceSheetItemType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemSequence() {
        return this.itemSequence;
    }

    public BalanceSheetItemType itemSequence(Integer itemSequence) {
        this.setItemSequence(itemSequence);
        return this;
    }

    public void setItemSequence(Integer itemSequence) {
        this.itemSequence = itemSequence;
    }

    public String getItemNumber() {
        return this.itemNumber;
    }

    public BalanceSheetItemType itemNumber(String itemNumber) {
        this.setItemNumber(itemNumber);
        return this;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public BalanceSheetItemType shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public TransactionAccount getTransactionAccount() {
        return this.transactionAccount;
    }

    public void setTransactionAccount(TransactionAccount transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public BalanceSheetItemType transactionAccount(TransactionAccount transactionAccount) {
        this.setTransactionAccount(transactionAccount);
        return this;
    }

    public BalanceSheetItemType getParentItem() {
        return this.parentItem;
    }

    public void setParentItem(BalanceSheetItemType balanceSheetItemType) {
        this.parentItem = balanceSheetItemType;
    }

    public BalanceSheetItemType parentItem(BalanceSheetItemType balanceSheetItemType) {
        this.setParentItem(balanceSheetItemType);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BalanceSheetItemType)) {
            return false;
        }
        return id != null && id.equals(((BalanceSheetItemType) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BalanceSheetItemType{" +
            "id=" + getId() +
            ", itemSequence=" + getItemSequence() +
            ", itemNumber='" + getItemNumber() + "'" +
            ", shortDescription='" + getShortDescription() + "'" +
            "}";
    }
}
