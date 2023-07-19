package io.github.calvary.service;

import io.github.calvary.service.dto.AccountingEventDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.AccountingEvent}.
 */
public interface AccountingEventService {
    /**
     * Save a accountingEvent.
     *
     * @param accountingEventDTO the entity to save.
     * @return the persisted entity.
     */
    AccountingEventDTO save(AccountingEventDTO accountingEventDTO);

    /**
     * Updates a accountingEvent.
     *
     * @param accountingEventDTO the entity to update.
     * @return the persisted entity.
     */
    AccountingEventDTO update(AccountingEventDTO accountingEventDTO);

    /**
     * Partially updates a accountingEvent.
     *
     * @param accountingEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AccountingEventDTO> partialUpdate(AccountingEventDTO accountingEventDTO);

    /**
     * Get all the accountingEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccountingEventDTO> findAll(Pageable pageable);

    /**
     * Get all the accountingEvents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccountingEventDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" accountingEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AccountingEventDTO> findOne(Long id);

    /**
     * Delete the "id" accountingEvent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the accountingEvent corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccountingEventDTO> search(String query, Pageable pageable);
}
