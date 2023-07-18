package io.github.calvary.service.mapper;

import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.service.dto.AccountTransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccountTransaction} and its DTO {@link AccountTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccountTransactionMapper extends EntityMapper<AccountTransactionDTO, AccountTransaction> {}
