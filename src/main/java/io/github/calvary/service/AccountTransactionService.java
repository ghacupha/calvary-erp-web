package io.github.calvary.service;

import io.github.calvary.service.dto.AccountTransactionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.AccountTransaction}.
 */
public interface AccountTransactionService {
    /**
     * Save a accountTransaction.
     *
     * @param accountTransactionDTO the entity to save.
     * @return the persisted entity.
     */
    AccountTransactionDTO save(AccountTransactionDTO accountTransactionDTO);

    /**
     * Updates a accountTransaction.
     *
     * @param accountTransactionDTO the entity to update.
     * @return the persisted entity.
     */
    AccountTransactionDTO update(AccountTransactionDTO accountTransactionDTO);

    /**
     * Partially updates a accountTransaction.
     *
     * @param accountTransactionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AccountTransactionDTO> partialUpdate(AccountTransactionDTO accountTransactionDTO);

    /**
     * Get all the accountTransactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccountTransactionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" accountTransaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AccountTransactionDTO> findOne(Long id);

    /**
     * Delete the "id" accountTransaction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the accountTransaction corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccountTransactionDTO> search(String query, Pageable pageable);
}
