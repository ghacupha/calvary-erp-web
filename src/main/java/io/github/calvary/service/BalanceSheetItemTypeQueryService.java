package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import io.github.calvary.repository.search.BalanceSheetItemTypeSearchRepository;
import io.github.calvary.service.criteria.BalanceSheetItemTypeCriteria;
import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import io.github.calvary.service.mapper.BalanceSheetItemTypeMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BalanceSheetItemType} entities in the database.
 * The main input is a {@link BalanceSheetItemTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BalanceSheetItemTypeDTO} or a {@link Page} of {@link BalanceSheetItemTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BalanceSheetItemTypeQueryService extends QueryService<BalanceSheetItemType> {

    private final Logger log = LoggerFactory.getLogger(BalanceSheetItemTypeQueryService.class);

    private final BalanceSheetItemTypeRepository balanceSheetItemTypeRepository;

    private final BalanceSheetItemTypeMapper balanceSheetItemTypeMapper;

    private final BalanceSheetItemTypeSearchRepository balanceSheetItemTypeSearchRepository;

    public BalanceSheetItemTypeQueryService(
        BalanceSheetItemTypeRepository balanceSheetItemTypeRepository,
        BalanceSheetItemTypeMapper balanceSheetItemTypeMapper,
        BalanceSheetItemTypeSearchRepository balanceSheetItemTypeSearchRepository
    ) {
        this.balanceSheetItemTypeRepository = balanceSheetItemTypeRepository;
        this.balanceSheetItemTypeMapper = balanceSheetItemTypeMapper;
        this.balanceSheetItemTypeSearchRepository = balanceSheetItemTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BalanceSheetItemTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BalanceSheetItemTypeDTO> findByCriteria(BalanceSheetItemTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BalanceSheetItemType> specification = createSpecification(criteria);
        return balanceSheetItemTypeMapper.toDto(balanceSheetItemTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BalanceSheetItemTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BalanceSheetItemTypeDTO> findByCriteria(BalanceSheetItemTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BalanceSheetItemType> specification = createSpecification(criteria);
        return balanceSheetItemTypeRepository.findAll(specification, page).map(balanceSheetItemTypeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BalanceSheetItemTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BalanceSheetItemType> specification = createSpecification(criteria);
        return balanceSheetItemTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link BalanceSheetItemTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BalanceSheetItemType> createSpecification(BalanceSheetItemTypeCriteria criteria) {
        Specification<BalanceSheetItemType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BalanceSheetItemType_.id));
            }
            if (criteria.getItemSequence() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getItemSequence(), BalanceSheetItemType_.itemSequence));
            }
            if (criteria.getItemNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getItemNumber(), BalanceSheetItemType_.itemNumber));
            }
            if (criteria.getShortDescription() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getShortDescription(), BalanceSheetItemType_.shortDescription));
            }
            if (criteria.getTransactionAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionAccountId(),
                            root -> root.join(BalanceSheetItemType_.transactionAccount, JoinType.LEFT).get(TransactionAccount_.id)
                        )
                    );
            }
            if (criteria.getParentItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getParentItemId(),
                            root -> root.join(BalanceSheetItemType_.parentItem, JoinType.LEFT).get(BalanceSheetItemType_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
