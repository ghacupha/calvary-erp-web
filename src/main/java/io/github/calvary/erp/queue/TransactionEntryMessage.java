package io.github.calvary.erp.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntryMessage implements Serializable {

    private Long id;

    private BigDecimal entryAmount;

    private String transactionEntryType;

    private String description;

    private Boolean wasProposed;

    private Boolean wasPosted;

    private Boolean wasDeleted;

    private Boolean wasApproved;

    private Long transactionAccountId;

    private Long accountTransactionId;
}
