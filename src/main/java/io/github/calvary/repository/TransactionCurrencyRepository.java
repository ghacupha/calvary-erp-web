package io.github.calvary.repository;

import io.github.calvary.domain.TransactionCurrency;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionCurrency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionCurrencyRepository
    extends JpaRepository<TransactionCurrency, Long>, JpaSpecificationExecutor<TransactionCurrency> {}
