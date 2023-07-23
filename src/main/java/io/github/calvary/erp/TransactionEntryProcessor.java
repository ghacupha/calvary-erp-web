package io.github.calvary.erp;

import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import io.github.calvary.erp.queue.Messenger;
import io.github.calvary.erp.queue.TransactionEntryMessage;
import io.github.calvary.service.dto.TransactionEntryDTO;
import org.springframework.stereotype.Service;

@Service("transactionEntryProcessor")
public class TransactionEntryProcessor implements PostingProcessorService<TransactionEntryDTO>{

    private final Messenger<TransactionEntryMessage> transactionEntryMessageMessenger;

    public TransactionEntryProcessor(Messenger<TransactionEntryMessage> transactionEntryMessageMessenger) {
        this.transactionEntryMessageMessenger = transactionEntryMessageMessenger;
    }

    @Override
    public TransactionEntryDTO post(TransactionEntryDTO dto) {
        TransactionEntryMessage message = TransactionEntryMessage
            .builder()
            .id(dto.getId())
            .entryAmount(dto.getEntryAmount())
            .transactionEntryType(dto.getTransactionEntryType() == TransactionEntryTypes.DEBIT ? "DEBIT" : "CREDIT")
            .description(dto.getDescription())
            .wasProposed(dto.getWasProposed())
            .wasPosted(dto.getWasPosted())
            .wasDeleted(dto.getWasDeleted())
            .wasApproved(dto.getWasApproved())
            .transactionAccountId(dto.getTransactionAccount().getId())
            .accountTransactionId(dto.getAccountTransaction().getId())
            .build();

        transactionEntryMessageMessenger.sendMessage(message);

        return dto;
    }


}
