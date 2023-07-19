package io.github.calvary.service;

import io.github.calvary.domain.*; // for static metamodels
import io.github.calvary.domain.AccountingEvent;
import io.github.calvary.repository.AccountingEventRepository;
import io.github.calvary.repository.search.AccountingEventSearchRepository;
import io.github.calvary.service.criteria.AccountingEventCriteria;
import io.github.calvary.service.dto.AccountingEventDTO;
import io.github.calvary.service.mapper.AccountingEventMapper;
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
 * Service for executing complex queries for {@link AccountingEvent} entities in the database.
 * The main input is a {@link AccountingEventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AccountingEventDTO} or a {@link Page} of {@link AccountingEventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AccountingEventQueryService extends QueryService<AccountingEvent> {

    private final Logger log = LoggerFactory.getLogger(AccountingEventQueryService.class);

    private final AccountingEventRepository accountingEventRepository;

    private final AccountingEventMapper accountingEventMapper;

    private final AccountingEventSearchRepository accountingEventSearchRepository;

    public AccountingEventQueryService(
        AccountingEventRepository accountingEventRepository,
        AccountingEventMapper accountingEventMapper,
        AccountingEventSearchRepository accountingEventSearchRepository
    ) {
        this.accountingEventRepository = accountingEventRepository;
        this.accountingEventMapper = accountingEventMapper;
        this.accountingEventSearchRepository = accountingEventSearchRepository;
    }

    /**
     * Return a {@link List} of {@link AccountingEventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AccountingEventDTO> findByCriteria(AccountingEventCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AccountingEvent> specification = createSpecification(criteria);
        return accountingEventMapper.toDto(accountingEventRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AccountingEventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AccountingEventDTO> findByCriteria(AccountingEventCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AccountingEvent> specification = createSpecification(criteria);
        return accountingEventRepository.findAll(specification, page).map(accountingEventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AccountingEventCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AccountingEvent> specification = createSpecification(criteria);
        return accountingEventRepository.count(specification);
    }

    /**
     * Function to convert {@link AccountingEventCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AccountingEvent> createSpecification(AccountingEventCriteria criteria) {
        Specification<AccountingEvent> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AccountingEvent_.id));
            }
            if (criteria.getEventDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEventDate(), AccountingEvent_.eventDate));
            }
            if (criteria.getEventTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEventTypeId(),
                            root -> root.join(AccountingEvent_.eventType, JoinType.LEFT).get(EventType_.id)
                        )
                    );
            }
            if (criteria.getDealerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDealerId(),
                            root -> root.join(AccountingEvent_.dealer, JoinType.LEFT).get(Dealer_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
