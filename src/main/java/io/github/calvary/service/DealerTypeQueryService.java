package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.DealerType;
import io.github.calvary.repository.DealerTypeRepository;
import io.github.calvary.repository.search.DealerTypeSearchRepository;
import io.github.calvary.service.criteria.DealerTypeCriteria;
import io.github.calvary.service.dto.DealerTypeDTO;
import io.github.calvary.service.mapper.DealerTypeMapper;
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
 * Service for executing complex queries for {@link DealerType} entities in the database.
 * The main input is a {@link DealerTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DealerTypeDTO} or a {@link Page} of {@link DealerTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DealerTypeQueryService extends QueryService<DealerType> {

    private final Logger log = LoggerFactory.getLogger(DealerTypeQueryService.class);

    private final DealerTypeRepository dealerTypeRepository;

    private final DealerTypeMapper dealerTypeMapper;

    private final DealerTypeSearchRepository dealerTypeSearchRepository;

    public DealerTypeQueryService(
        DealerTypeRepository dealerTypeRepository,
        DealerTypeMapper dealerTypeMapper,
        DealerTypeSearchRepository dealerTypeSearchRepository
    ) {
        this.dealerTypeRepository = dealerTypeRepository;
        this.dealerTypeMapper = dealerTypeMapper;
        this.dealerTypeSearchRepository = dealerTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link DealerTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DealerTypeDTO> findByCriteria(DealerTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DealerType> specification = createSpecification(criteria);
        return dealerTypeMapper.toDto(dealerTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DealerTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DealerTypeDTO> findByCriteria(DealerTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DealerType> specification = createSpecification(criteria);
        return dealerTypeRepository.findAll(specification, page).map(dealerTypeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DealerTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DealerType> specification = createSpecification(criteria);
        return dealerTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link DealerTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DealerType> createSpecification(DealerTypeCriteria criteria) {
        Specification<DealerType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DealerType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), DealerType_.name));
            }
        }
        return specification;
    }
}
