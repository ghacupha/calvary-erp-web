package io.github.calvary.service.mapper;

import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.domain.BalanceSheetItemValue;
import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BalanceSheetItemValue} and its DTO {@link BalanceSheetItemValueDTO}.
 */
@Mapper(componentModel = "spring")
public interface BalanceSheetItemValueMapper extends EntityMapper<BalanceSheetItemValueDTO, BalanceSheetItemValue> {
    @Mapping(target = "itemType", source = "itemType", qualifiedByName = "balanceSheetItemTypeItemNumber")
    BalanceSheetItemValueDTO toDto(BalanceSheetItemValue s);

    @Named("balanceSheetItemTypeItemNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "itemNumber", source = "itemNumber")
    BalanceSheetItemTypeDTO toDtoBalanceSheetItemTypeItemNumber(BalanceSheetItemType balanceSheetItemType);
}
