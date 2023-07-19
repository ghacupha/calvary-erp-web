package io.github.calvary.service.mapper;

import io.github.calvary.domain.EventType;
import io.github.calvary.service.dto.EventTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventType} and its DTO {@link EventTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventTypeMapper extends EntityMapper<EventTypeDTO, EventType> {}
