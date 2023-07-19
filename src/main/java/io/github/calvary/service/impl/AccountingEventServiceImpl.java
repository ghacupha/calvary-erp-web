package io.github.calvary.service.impl;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.domain.AccountingEvent;
import io.github.calvary.repository.AccountingEventRepository;
import io.github.calvary.repository.search.AccountingEventSearchRepository;
import io.github.calvary.service.AccountingEventService;
import io.github.calvary.service.dto.AccountingEventDTO;
import io.github.calvary.service.mapper.AccountingEventMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AccountingEvent}.
 */
@Service
@Transactional
public class AccountingEventServiceImpl implements AccountingEventService {

    private final Logger log = LoggerFactory.getLogger(AccountingEventServiceImpl.class);

    private final AccountingEventRepository accountingEventRepository;

    private final AccountingEventMapper accountingEventMapper;

    private final AccountingEventSearchRepository accountingEventSearchRepository;

    public AccountingEventServiceImpl(
        AccountingEventRepository accountingEventRepository,
        AccountingEventMapper accountingEventMapper,
        AccountingEventSearchRepository accountingEventSearchRepository
    ) {
        this.accountingEventRepository = accountingEventRepository;
        this.accountingEventMapper = accountingEventMapper;
        this.accountingEventSearchRepository = accountingEventSearchRepository;
    }

    @Override
    public AccountingEventDTO save(AccountingEventDTO accountingEventDTO) {
        log.debug("Request to save AccountingEvent : {}", accountingEventDTO);
        AccountingEvent accountingEvent = accountingEventMapper.toEntity(accountingEventDTO);
        accountingEvent = accountingEventRepository.save(accountingEvent);
        AccountingEventDTO result = accountingEventMapper.toDto(accountingEvent);
        accountingEventSearchRepository.index(accountingEvent);
        return result;
    }

    @Override
    public AccountingEventDTO update(AccountingEventDTO accountingEventDTO) {
        log.debug("Request to update AccountingEvent : {}", accountingEventDTO);
        AccountingEvent accountingEvent = accountingEventMapper.toEntity(accountingEventDTO);
        accountingEvent = accountingEventRepository.save(accountingEvent);
        AccountingEventDTO result = accountingEventMapper.toDto(accountingEvent);
        accountingEventSearchRepository.index(accountingEvent);
        return result;
    }

    @Override
    public Optional<AccountingEventDTO> partialUpdate(AccountingEventDTO accountingEventDTO) {
        log.debug("Request to partially update AccountingEvent : {}", accountingEventDTO);

        return accountingEventRepository
            .findById(accountingEventDTO.getId())
            .map(existingAccountingEvent -> {
                accountingEventMapper.partialUpdate(existingAccountingEvent, accountingEventDTO);

                return existingAccountingEvent;
            })
            .map(accountingEventRepository::save)
            .map(savedAccountingEvent -> {
                accountingEventSearchRepository.index(savedAccountingEvent);
                return savedAccountingEvent;
            })
            .map(accountingEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountingEventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AccountingEvents");
        return accountingEventRepository.findAll(pageable).map(accountingEventMapper::toDto);
    }

    public Page<AccountingEventDTO> findAllWithEagerRelationships(Pageable pageable) {
        return accountingEventRepository.findAllWithEagerRelationships(pageable).map(accountingEventMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountingEventDTO> findOne(Long id) {
        log.debug("Request to get AccountingEvent : {}", id);
        return accountingEventRepository.findOneWithEagerRelationships(id).map(accountingEventMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AccountingEvent : {}", id);
        accountingEventRepository.deleteById(id);
        accountingEventSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountingEventDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of AccountingEvents for query {}", query);
        return accountingEventSearchRepository.search(query, pageable).map(accountingEventMapper::toDto);
    }
}
