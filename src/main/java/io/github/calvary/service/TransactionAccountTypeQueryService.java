package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransactionAccountType;
import io.github.calvary.repository.TransactionAccountTypeRepository;
import io.github.calvary.repository.search.TransactionAccountTypeSearchRepository;
import io.github.calvary.service.criteria.TransactionAccountTypeCriteria;
import io.github.calvary.service.dto.TransactionAccountTypeDTO;
import io.github.calvary.service.mapper.TransactionAccountTypeMapper;
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
 * Service for executing complex queries for {@link TransactionAccountType} entities in the database.
 * The main input is a {@link TransactionAccountTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionAccountTypeDTO} or a {@link Page} of {@link TransactionAccountTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionAccountTypeQueryService extends QueryService<TransactionAccountType> {

    private final Logger log = LoggerFactory.getLogger(TransactionAccountTypeQueryService.class);

    private final TransactionAccountTypeRepository transactionAccountTypeRepository;

    private final TransactionAccountTypeMapper transactionAccountTypeMapper;

    private final TransactionAccountTypeSearchRepository transactionAccountTypeSearchRepository;

    public TransactionAccountTypeQueryService(
        TransactionAccountTypeRepository transactionAccountTypeRepository,
        TransactionAccountTypeMapper transactionAccountTypeMapper,
        TransactionAccountTypeSearchRepository transactionAccountTypeSearchRepository
    ) {
        this.transactionAccountTypeRepository = transactionAccountTypeRepository;
        this.transactionAccountTypeMapper = transactionAccountTypeMapper;
        this.transactionAccountTypeSearchRepository = transactionAccountTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionAccountTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionAccountTypeDTO> findByCriteria(TransactionAccountTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionAccountType> specification = createSpecification(criteria);
        return transactionAccountTypeMapper.toDto(transactionAccountTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionAccountTypeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionAccountTypeDTO> findByCriteria(TransactionAccountTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionAccountType> specification = createSpecification(criteria);
        return transactionAccountTypeRepository.findAll(specification, page).map(transactionAccountTypeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionAccountTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionAccountType> specification = createSpecification(criteria);
        return transactionAccountTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionAccountTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionAccountType> createSpecification(TransactionAccountTypeCriteria criteria) {
        Specification<TransactionAccountType> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionAccountType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TransactionAccountType_.name));
            }
        }
        return specification;
    }
}
