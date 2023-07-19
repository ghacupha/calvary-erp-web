package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.Dealer;
import io.github.calvary.repository.DealerRepository;
import io.github.calvary.repository.search.DealerSearchRepository;
import io.github.calvary.service.criteria.DealerCriteria;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.mapper.DealerMapper;
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
 * Service for executing complex queries for {@link Dealer} entities in the database.
 * The main input is a {@link DealerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DealerDTO} or a {@link Page} of {@link DealerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DealerQueryService extends QueryService<Dealer> {

    private final Logger log = LoggerFactory.getLogger(DealerQueryService.class);

    private final DealerRepository dealerRepository;

    private final DealerMapper dealerMapper;

    private final DealerSearchRepository dealerSearchRepository;

    public DealerQueryService(DealerRepository dealerRepository, DealerMapper dealerMapper, DealerSearchRepository dealerSearchRepository) {
        this.dealerRepository = dealerRepository;
        this.dealerMapper = dealerMapper;
        this.dealerSearchRepository = dealerSearchRepository;
    }

    /**
     * Return a {@link List} of {@link DealerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DealerDTO> findByCriteria(DealerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Dealer> specification = createSpecification(criteria);
        return dealerMapper.toDto(dealerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DealerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DealerDTO> findByCriteria(DealerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Dealer> specification = createSpecification(criteria);
        return dealerRepository.findAll(specification, page).map(dealerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DealerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Dealer> specification = createSpecification(criteria);
        return dealerRepository.count(specification);
    }

    /**
     * Function to convert {@link DealerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Dealer> createSpecification(DealerCriteria criteria) {
        Specification<Dealer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Dealer_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Dealer_.name));
            }
            if (criteria.getDealerTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDealerTypeId(),
                            root -> root.join(Dealer_.dealerType, JoinType.LEFT).get(DealerType_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
