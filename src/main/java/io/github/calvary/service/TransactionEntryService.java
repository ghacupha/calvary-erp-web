package io.github.calvary.service;

import io.github.calvary.service.dto.TransactionEntryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.TransactionEntry}.
 */
public interface TransactionEntryService {
    /**
     * Save a transactionEntry.
     *
     * @param transactionEntryDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionEntryDTO save(TransactionEntryDTO transactionEntryDTO);

    /**
     * Updates a transactionEntry.
     *
     * @param transactionEntryDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionEntryDTO update(TransactionEntryDTO transactionEntryDTO);

    /**
     * Partially updates a transactionEntry.
     *
     * @param transactionEntryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionEntryDTO> partialUpdate(TransactionEntryDTO transactionEntryDTO);

    /**
     * Get all the transactionEntries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionEntryDTO> findAll(Pageable pageable);

    /**
     * Get all the transactionEntries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionEntryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" transactionEntry.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionEntryDTO> findOne(Long id);

    /**
     * Delete the "id" transactionEntry.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the transactionEntry corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionEntryDTO> search(String query, Pageable pageable);
}
