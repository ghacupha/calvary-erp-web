package io.github.calvary.domain;

import org.hibernate.annotations.Check;

import java.io.Serializable;

/**
 * Contains entity methods for maintaining accounting rules
 */
public class AccountingDomainEntity implements Serializable {

    @Check(constraints = "SELECT SUM(CASE WHEN transaction_entry_type = DEBIT THEN entry_amount ELSE - entry_amount END) = O FROM TransactionEntry WHERE accountTransaction_id = id")
    protected void validateDebitCreditBalance() {
        // Hibernate validation with @Check annotation
    }
}
