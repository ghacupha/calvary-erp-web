package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransactionEntry.
 */
@Entity
@Table(name = "transaction_entry")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transactionentry")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "entry_amount", precision = 21, scale = 2)
    private BigDecimal entryAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_entry_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TransactionEntryTypes transactionEntryType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "parentAccount" }, allowSetters = true)
    private TransactionAccount transactionAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionEntry id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getEntryAmount() {
        return this.entryAmount;
    }

    public TransactionEntry entryAmount(BigDecimal entryAmount) {
        this.setEntryAmount(entryAmount);
        return this;
    }

    public void setEntryAmount(BigDecimal entryAmount) {
        this.entryAmount = entryAmount;
    }

    public TransactionEntryTypes getTransactionEntryType() {
        return this.transactionEntryType;
    }

    public TransactionEntry transactionEntryType(TransactionEntryTypes transactionEntryType) {
        this.setTransactionEntryType(transactionEntryType);
        return this;
    }

    public void setTransactionEntryType(TransactionEntryTypes transactionEntryType) {
        this.transactionEntryType = transactionEntryType;
    }

    public TransactionAccount getTransactionAccount() {
        return this.transactionAccount;
    }

    public void setTransactionAccount(TransactionAccount transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public TransactionEntry transactionAccount(TransactionAccount transactionAccount) {
        this.setTransactionAccount(transactionAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionEntry)) {
            return false;
        }
        return id != null && id.equals(((TransactionEntry) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionEntry{" +
            "id=" + getId() +
            ", entryAmount=" + getEntryAmount() +
            ", transactionEntryType='" + getTransactionEntryType() + "'" +
            "}";
    }
}
