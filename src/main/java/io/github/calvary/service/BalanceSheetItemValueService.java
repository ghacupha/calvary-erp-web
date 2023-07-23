package io.github.calvary.service;

import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.BalanceSheetItemValue}.
 */
public interface BalanceSheetItemValueService {
    /**
     * Save a balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the entity to save.
     * @return the persisted entity.
     */
    BalanceSheetItemValueDTO save(BalanceSheetItemValueDTO balanceSheetItemValueDTO);

    /**
     * Updates a balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the entity to update.
     * @return the persisted entity.
     */
    BalanceSheetItemValueDTO update(BalanceSheetItemValueDTO balanceSheetItemValueDTO);

    /**
     * Partially updates a balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BalanceSheetItemValueDTO> partialUpdate(BalanceSheetItemValueDTO balanceSheetItemValueDTO);

    /**
     * Get all the balanceSheetItemValues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemValueDTO> findAll(Pageable pageable);

    /**
     * Get all the balanceSheetItemValues with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemValueDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" balanceSheetItemValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BalanceSheetItemValueDTO> findOne(Long id);

    /**
     * Delete the "id" balanceSheetItemValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the balanceSheetItemValue corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemValueDTO> search(String query, Pageable pageable);
}
