package io.github.calvary.repository;

import io.github.calvary.domain.AccountTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccountTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccountTransactionRepository
    extends JpaRepository<AccountTransaction, Long>, JpaSpecificationExecutor<AccountTransaction> {}
