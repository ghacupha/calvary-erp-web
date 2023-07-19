package io.github.calvary.service.mapper;

import io.github.calvary.domain.TransactionAccountType;
import io.github.calvary.service.dto.TransactionAccountTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransactionAccountType} and its DTO {@link TransactionAccountTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionAccountTypeMapper extends EntityMapper<TransactionAccountTypeDTO, TransactionAccountType> {}
