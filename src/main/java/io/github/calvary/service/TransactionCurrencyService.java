package io.github.calvary.service;

import io.github.calvary.service.dto.TransactionCurrencyDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransactionCurrency}.
 */
public interface TransactionCurrencyService {
    /**
     * Save a transactionCurrency.
     *
     * @param transactionCurrencyDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionCurrencyDTO save(TransactionCurrencyDTO transactionCurrencyDTO);

    /**
     * Updates a transactionCurrency.
     *
     * @param transactionCurrencyDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionCurrencyDTO update(TransactionCurrencyDTO transactionCurrencyDTO);

    /**
     * Partially updates a transactionCurrency.
     *
     * @param transactionCurrencyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionCurrencyDTO> partialUpdate(TransactionCurrencyDTO transactionCurrencyDTO);

    /**
     * Get all the transactionCurrencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionCurrencyDTO> findAll(Pageable pageable);

    /**
     * Get the "id" transactionCurrency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionCurrencyDTO> findOne(Long id);

    /**
     * Delete the "id" transactionCurrency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transactionCurrency corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionCurrencyDTO> search(String query, Pageable pageable);
}
