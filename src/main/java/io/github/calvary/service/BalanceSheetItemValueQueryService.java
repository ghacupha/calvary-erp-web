package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.BalanceSheetItemValue;
import io.github.calvary.repository.BalanceSheetItemValueRepository;
import io.github.calvary.repository.search.BalanceSheetItemValueSearchRepository;
import io.github.calvary.service.criteria.BalanceSheetItemValueCriteria;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import io.github.calvary.service.mapper.BalanceSheetItemValueMapper;
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
 * Service for executing complex queries for {@link BalanceSheetItemValue} entities in the database.
 * The main input is a {@link BalanceSheetItemValueCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BalanceSheetItemValueDTO} or a {@link Page} of {@link BalanceSheetItemValueDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BalanceSheetItemValueQueryService extends QueryService<BalanceSheetItemValue> {

    private final Logger log = LoggerFactory.getLogger(BalanceSheetItemValueQueryService.class);

    private final BalanceSheetItemValueRepository balanceSheetItemValueRepository;

    private final BalanceSheetItemValueMapper balanceSheetItemValueMapper;

    private final BalanceSheetItemValueSearchRepository balanceSheetItemValueSearchRepository;

    public BalanceSheetItemValueQueryService(
        BalanceSheetItemValueRepository balanceSheetItemValueRepository,
        BalanceSheetItemValueMapper balanceSheetItemValueMapper,
        BalanceSheetItemValueSearchRepository balanceSheetItemValueSearchRepository
    ) {
        this.balanceSheetItemValueRepository = balanceSheetItemValueRepository;
        this.balanceSheetItemValueMapper = balanceSheetItemValueMapper;
        this.balanceSheetItemValueSearchRepository = balanceSheetItemValueSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BalanceSheetItemValueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BalanceSheetItemValueDTO> findByCriteria(BalanceSheetItemValueCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BalanceSheetItemValue> specification = createSpecification(criteria);
        return balanceSheetItemValueMapper.toDto(balanceSheetItemValueRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BalanceSheetItemValueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BalanceSheetItemValueDTO> findByCriteria(BalanceSheetItemValueCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BalanceSheetItemValue> specification = createSpecification(criteria);
        return balanceSheetItemValueRepository.findAll(specification, page).map(balanceSheetItemValueMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BalanceSheetItemValueCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BalanceSheetItemValue> specification = createSpecification(criteria);
        return balanceSheetItemValueRepository.count(specification);
    }

    /**
     * Function to convert {@link BalanceSheetItemValueCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BalanceSheetItemValue> createSpecification(BalanceSheetItemValueCriteria criteria) {
        Specification<BalanceSheetItemValue> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BalanceSheetItemValue_.id));
            }
            if (criteria.getShortDescription() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getShortDescription(), BalanceSheetItemValue_.shortDescription));
            }
            if (criteria.getEffectiveDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getEffectiveDate(), BalanceSheetItemValue_.effectiveDate));
            }
            if (criteria.getItemAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getItemAmount(), BalanceSheetItemValue_.itemAmount));
            }
            if (criteria.getItemTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getItemTypeId(),
                            root -> root.join(BalanceSheetItemValue_.itemType, JoinType.LEFT).get(BalanceSheetItemType_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
