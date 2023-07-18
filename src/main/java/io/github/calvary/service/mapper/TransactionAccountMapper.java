package io.github.calvary.service.mapper;

import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.service.dto.TransactionAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionAccount} and its DTO {@link TransactionAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionAccountMapper extends EntityMapper<TransactionAccountDTO, TransactionAccount> {
    @Mapping(target = "parentAccount", source = "parentAccount", qualifiedByName = "transactionAccountAccountName")
    TransactionAccountDTO toDto(TransactionAccount s);

    @Named("transactionAccountAccountName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountName", source = "accountName")
    TransactionAccountDTO toDtoTransactionAccountAccountName(TransactionAccount transactionAccount);
}
