package io.github.calvary.repository;

import io.github.calvary.domain.TransactionAccountType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionAccountType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionAccountTypeRepository
    extends JpaRepository<TransactionAccountType, Long>, JpaSpecificationExecutor<TransactionAccountType> {}
