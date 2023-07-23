package io.github.calvary.erp;

import io.github.calvary.erp.queue.TransactionEntryMessage;

public interface BalanceSheetUpdateService {
    void update(TransactionEntryMessage message);
}
