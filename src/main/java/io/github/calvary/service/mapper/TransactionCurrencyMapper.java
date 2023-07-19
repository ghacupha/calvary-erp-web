package io.github.calvary.service.mapper;

import io.github.calvary.domain.TransactionCurrency;
import io.github.calvary.service.dto.TransactionCurrencyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionCurrency} and its DTO {@link TransactionCurrencyDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionCurrencyMapper extends EntityMapper<TransactionCurrencyDTO, TransactionCurrency> {}
