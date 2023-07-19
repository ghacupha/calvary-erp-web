package io.github.calvary.service.mapper;

import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.DealerType;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.DealerTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dealer} and its DTO {@link DealerDTO}.
 */
@Mapper(componentModel = "spring")
public interface DealerMapper extends EntityMapper<DealerDTO, Dealer> {
    @Mapping(target = "dealerType", source = "dealerType", qualifiedByName = "dealerTypeName")
    DealerDTO toDto(Dealer s);

    @Named("dealerTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DealerTypeDTO toDtoDealerTypeName(DealerType dealerType);
}
