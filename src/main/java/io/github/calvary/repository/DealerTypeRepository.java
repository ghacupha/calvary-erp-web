package io.github.calvary.repository;

import io.github.calvary.domain.DealerType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DealerType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DealerTypeRepository extends JpaRepository<DealerType, Long>, JpaSpecificationExecutor<DealerType> {}
