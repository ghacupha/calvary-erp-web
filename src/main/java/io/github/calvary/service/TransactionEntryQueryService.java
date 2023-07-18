package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.repository.TransactionEntryRepository;
import io.github.calvary.repository.search.TransactionEntrySearchRepository;
import io.github.calvary.service.criteria.TransactionEntryCriteria;
import io.github.calvary.service.dto.TransactionEntryDTO;
import io.github.calvary.service.mapper.TransactionEntryMapper;
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
 * Service for executing complex queries for {@link TransactionEntry} entities in the database.
 * The main input is a {@link TransactionEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionEntryDTO} or a {@link Page} of {@link TransactionEntryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionEntryQueryService extends QueryService<TransactionEntry> {

    private final Logger log = LoggerFactory.getLogger(TransactionEntryQueryService.class);

    private final TransactionEntryRepository transactionEntryRepository;

    private final TransactionEntryMapper transactionEntryMapper;

    private final TransactionEntrySearchRepository transactionEntrySearchRepository;

    public TransactionEntryQueryService(
        TransactionEntryRepository transactionEntryRepository,
        TransactionEntryMapper transactionEntryMapper,
        TransactionEntrySearchRepository transactionEntrySearchRepository
    ) {
        this.transactionEntryRepository = transactionEntryRepository;
        this.transactionEntryMapper = transactionEntryMapper;
        this.transactionEntrySearchRepository = transactionEntrySearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionEntryDTO> findByCriteria(TransactionEntryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionEntry> specification = createSpecification(criteria);
        return transactionEntryMapper.toDto(transactionEntryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionEntryDTO> findByCriteria(TransactionEntryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionEntry> specification = createSpecification(criteria);
        return transactionEntryRepository.findAll(specification, page).map(transactionEntryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionEntryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionEntry> specification = createSpecification(criteria);
        return transactionEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionEntry> createSpecification(TransactionEntryCriteria criteria) {
        Specification<TransactionEntry> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionEntry_.id));
            }
            if (criteria.getEntryAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEntryAmount(), TransactionEntry_.entryAmount));
            }
            if (criteria.getTransactionEntryType() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTransactionEntryType(), TransactionEntry_.transactionEntryType));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TransactionEntry_.description));
            }
            if (criteria.getTransactionAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionAccountId(),
                            root -> root.join(TransactionEntry_.transactionAccount, JoinType.LEFT).get(TransactionAccount_.id)
                        )
                    );
            }
            if (criteria.getAccountTransactionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAccountTransactionId(),
                            root -> root.join(TransactionEntry_.accountTransaction, JoinType.LEFT).get(AccountTransaction_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
