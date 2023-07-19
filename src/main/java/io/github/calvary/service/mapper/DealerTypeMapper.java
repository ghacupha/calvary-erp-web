package io.github.calvary.service.mapper;

import io.github.calvary.domain.DealerType;
import io.github.calvary.service.dto.DealerTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DealerType} and its DTO {@link DealerTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface DealerTypeMapper extends EntityMapper<DealerTypeDTO, DealerType> {}
