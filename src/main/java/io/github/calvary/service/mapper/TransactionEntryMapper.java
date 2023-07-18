package io.github.calvary.service.mapper;

import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.service.dto.TransactionAccountDTO;
import io.github.calvary.service.dto.TransactionEntryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionEntry} and its DTO {@link TransactionEntryDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionEntryMapper extends EntityMapper<TransactionEntryDTO, TransactionEntry> {
    @Mapping(target = "transactionAccount", source = "transactionAccount", qualifiedByName = "transactionAccountAccountName")
    @Mapping(target = "accountTransaction", source = "accountTransaction", qualifiedByName = "accountTransactionReferenceNumber")
    TransactionEntryDTO toDto(TransactionEntry s);

    @Named("transactionAccountAccountName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    TransactionAccountDTO toDtoTransactionAccountAccountName(TransactionAccount transactionAccount);

    @Named("accountTransactionReferenceNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "referenceNumber", source = "referenceNumber")
    AccountTransactionDTO toDtoAccountTransactionReferenceNumber(AccountTransaction accountTransaction);
}
