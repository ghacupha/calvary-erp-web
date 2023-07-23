package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.repository.TransactionAccountRepository;
import io.github.calvary.repository.search.TransactionAccountSearchRepository;
import io.github.calvary.service.criteria.TransactionAccountCriteria;
import io.github.calvary.service.dto.TransactionAccountDTO;
import io.github.calvary.service.mapper.TransactionAccountMapper;
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
 * Service for executing complex queries for {@link TransactionAccount} entities in the database.
 * The main input is a {@link TransactionAccountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TransactionAccountDTO} or a {@link Page} of {@link TransactionAccountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionAccountQueryService extends QueryService<TransactionAccount> {

    private final Logger log = LoggerFactory.getLogger(TransactionAccountQueryService.class);

    private final TransactionAccountRepository transactionAccountRepository;

    private final TransactionAccountMapper transactionAccountMapper;

    private final TransactionAccountSearchRepository transactionAccountSearchRepository;

    public TransactionAccountQueryService(
        TransactionAccountRepository transactionAccountRepository,
        TransactionAccountMapper transactionAccountMapper,
        TransactionAccountSearchRepository transactionAccountSearchRepository
    ) {
        this.transactionAccountRepository = transactionAccountRepository;
        this.transactionAccountMapper = transactionAccountMapper;
        this.transactionAccountSearchRepository = transactionAccountSearchRepository;
    }

    /**
     * Return a {@link List} of {@link TransactionAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionAccountDTO> findByCriteria(TransactionAccountCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TransactionAccount> specification = createSpecification(criteria);
        return transactionAccountMapper.toDto(transactionAccountRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TransactionAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionAccountDTO> findByCriteria(TransactionAccountCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransactionAccount> specification = createSpecification(criteria);
        return transactionAccountRepository.findAll(specification, page).map(transactionAccountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionAccountCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TransactionAccount> specification = createSpecification(criteria);
        return transactionAccountRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionAccountCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransactionAccount> createSpecification(TransactionAccountCriteria criteria) {
        Specification<TransactionAccount> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TransactionAccount_.id));
            }
            if (criteria.getAccountName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountName(), TransactionAccount_.accountName));
            }
            if (criteria.getAccountNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountNumber(), TransactionAccount_.accountNumber));
            }
            if (criteria.getOpeningBalance() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getOpeningBalance(), TransactionAccount_.openingBalance));
            }
            if (criteria.getParentAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getParentAccountId(),
                            root -> root.join(TransactionAccount_.parentAccount, JoinType.LEFT).get(TransactionAccount_.id)
                        )
                    );
            }
            if (criteria.getTransactionAccountTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionAccountTypeId(),
                            root -> root.join(TransactionAccount_.transactionAccountType, JoinType.LEFT).get(TransactionAccountType_.id)
                        )
                    );
            }
            if (criteria.getTransactionCurrencyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTransactionCurrencyId(),
                            root -> root.join(TransactionAccount_.transactionCurrency, JoinType.LEFT).get(TransactionCurrency_.id)
                        )
                    );
            }
            if (criteria.getBalanceSheetItemTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBalanceSheetItemTypeId(),
                            root -> root.join(TransactionAccount_.balanceSheetItemType, JoinType.LEFT).get(BalanceSheetItemType_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
