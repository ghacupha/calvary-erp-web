package io.github.calvary.service;

import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link io.github.calvary.domain.BalanceSheetItemType}.
 */
public interface BalanceSheetItemTypeService {
    /**
     * Save a balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the entity to save.
     * @return the persisted entity.
     */
    BalanceSheetItemTypeDTO save(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO);

    /**
     * Updates a balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the entity to update.
     * @return the persisted entity.
     */
    BalanceSheetItemTypeDTO update(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO);

    /**
     * Partially updates a balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BalanceSheetItemTypeDTO> partialUpdate(BalanceSheetItemTypeDTO balanceSheetItemTypeDTO);

    /**
     * Get all the balanceSheetItemTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemTypeDTO> findAll(Pageable pageable);

    /**
     * Get all the balanceSheetItemTypes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemTypeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" balanceSheetItemType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BalanceSheetItemTypeDTO> findOne(Long id);

    /**
     * Delete the "id" balanceSheetItemType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the balanceSheetItemType corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BalanceSheetItemTypeDTO> search(String query, Pageable pageable);
}
