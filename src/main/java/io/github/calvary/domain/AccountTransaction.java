package io.github.calvary.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AccountTransaction.
 */
@Entity
@Table(name = "account_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "accounttransaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Column(name = "reference_number", unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String referenceNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "accountTransaction")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "transactionAccount", "accountTransaction" }, allowSetters = true)
    private Set<TransactionEntry> transactionEntries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AccountTransaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public AccountTransaction transactionDate(LocalDate transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return this.description;
    }

    public AccountTransaction description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return this.referenceNumber;
    }

    public AccountTransaction referenceNumber(String referenceNumber) {
        this.setReferenceNumber(referenceNumber);
        return this;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Set<TransactionEntry> getTransactionEntries() {
        return this.transactionEntries;
    }

    public void setTransactionEntries(Set<TransactionEntry> transactionEntries) {
        if (this.transactionEntries != null) {
            this.transactionEntries.forEach(i -> i.setAccountTransaction(null));
        }
        if (transactionEntries != null) {
            transactionEntries.forEach(i -> i.setAccountTransaction(this));
        }
        this.transactionEntries = transactionEntries;
    }

    public AccountTransaction transactionEntries(Set<TransactionEntry> transactionEntries) {
        this.setTransactionEntries(transactionEntries);
        return this;
    }

    public AccountTransaction addTransactionEntry(TransactionEntry transactionEntry) {
        this.transactionEntries.add(transactionEntry);
        transactionEntry.setAccountTransaction(this);
        return this;
    }

    public AccountTransaction removeTransactionEntry(TransactionEntry transactionEntry) {
        this.transactionEntries.remove(transactionEntry);
        transactionEntry.setAccountTransaction(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountTransaction)) {
            return false;
        }
        return id != null && id.equals(((AccountTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountTransaction{" +
            "id=" + getId() +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", description='" + getDescription() + "'" +
            ", referenceNumber='" + getReferenceNumber() + "'" +
            "}";
    }
}
