package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransactionCurrency;
import io.github.calvary.repository.TransactionCurrencyRepository;
import io.github.calvary.repository.search.TransactionCurrencySearchRepository;
import io.github.calvary.service.criteria.TransactionCurrencyCriteria;
import io.github.calvary.service.dto.TransactionCurrencyDTO;
import io.github.calvary.service.mapper.TransactionCurrencyMapper;
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
 * Service for executing complex queries for {@link TransactionCurrency} entities in the database.
 * The main input is a {@link TransactionCurrencyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionCurrencyDTO} or a {@link Page} of {@link TransactionCurrencyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionCurrencyQueryService extends QueryService<TransactionCurrency> {

    private final Logger log = LoggerFactory.getLogger(TransactionCurrencyQueryService.class);

    private final TransactionCurrencyRepository transactionCurrencyRepository;

    private final TransactionCurrencyMapper transactionCurrencyMapper;

    private final TransactionCurrencySearchRepository transactionCurrencySearchRepository;

    public TransactionCurrencyQueryService(
        TransactionCurrencyRepository transactionCurrencyRepository,
        TransactionCurrencyMapper transactionCurrencyMapper,
        TransactionCurrencySearchRepository transactionCurrencySearchRepository
    ) {
        this.transactionCurrencyRepository = transactionCurrencyRepository;
        this.transactionCurrencyMapper = transactionCurrencyMapper;
        this.transactionCurrencySearchRepository = transactionCurrencySearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionCurrencyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionCurrencyDTO> findByCriteria(TransactionCurrencyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionCurrency> specification = createSpecification(criteria);
        return transactionCurrencyMapper.toDto(transactionCurrencyRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionCurrencyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionCurrencyDTO> findByCriteria(TransactionCurrencyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionCurrency> specification = createSpecification(criteria);
        return transactionCurrencyRepository.findAll(specification, page).map(transactionCurrencyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionCurrencyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionCurrency> specification = createSpecification(criteria);
        return transactionCurrencyRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionCurrencyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionCurrency> createSpecification(TransactionCurrencyCriteria criteria) {
        Specification<TransactionCurrency> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionCurrency_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TransactionCurrency_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), TransactionCurrency_.code));
            }
        }
        return specification;
    }
}
