package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.repository.AccountTransactionRepository;
import io.github.calvary.repository.search.AccountTransactionSearchRepository;
import io.github.calvary.service.criteria.AccountTransactionCriteria;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.service.mapper.AccountTransactionMapper;
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
 * Service for executing complex queries for {@link AccountTransaction} entities in the database.
 * The main input is a {@link AccountTransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AccountTransactionDTO} or a {@link Page} of {@link AccountTransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AccountTransactionQueryService extends QueryService<AccountTransaction> {

    private final Logger log = LoggerFactory.getLogger(AccountTransactionQueryService.class);

    private final AccountTransactionRepository accountTransactionRepository;

    private final AccountTransactionMapper accountTransactionMapper;

    private final AccountTransactionSearchRepository accountTransactionSearchRepository;

    public AccountTransactionQueryService(
        AccountTransactionRepository accountTransactionRepository,
        AccountTransactionMapper accountTransactionMapper,
        AccountTransactionSearchRepository accountTransactionSearchRepository
    ) {
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountTransactionMapper = accountTransactionMapper;
        this.accountTransactionSearchRepository = accountTransactionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link AccountTransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AccountTransactionDTO> findByCriteria(AccountTransactionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AccountTransaction> specification = createSpecification(criteria);
        return accountTransactionMapper.toDto(accountTransactionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AccountTransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AccountTransactionDTO> findByCriteria(AccountTransactionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AccountTransaction> specification = createSpecification(criteria);
        return accountTransactionRepository.findAll(specification, page).map(accountTransactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AccountTransactionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AccountTransaction> specification = createSpecification(criteria);
        return accountTransactionRepository.count(specification);
    }

    /**
     * Function to convert {@link AccountTransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AccountTransaction> createSpecification(AccountTransactionCriteria criteria) {
        Specification<AccountTransaction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AccountTransaction_.id));
            }
            if (criteria.getTransactionDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getTransactionDate(), AccountTransaction_.transactionDate));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), AccountTransaction_.description));
            }
            if (criteria.getReferenceNumber() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getReferenceNumber(), AccountTransaction_.referenceNumber));
            }
            if (criteria.getTransactionEntryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionEntryId(),
                            root -> root.join(AccountTransaction_.transactionEntries, JoinType.LEFT).get(TransactionEntry_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
