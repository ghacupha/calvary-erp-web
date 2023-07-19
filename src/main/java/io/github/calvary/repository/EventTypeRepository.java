package io.github.calvary.repository;

import io.github.calvary.domain.EventType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long>, JpaSpecificationExecutor<EventType> {}
