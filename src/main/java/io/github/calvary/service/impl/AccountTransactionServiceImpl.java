package io.github.calvary.service.impl;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.repository.AccountTransactionRepository;
import io.github.calvary.repository.search.AccountTransactionSearchRepository;
import io.github.calvary.service.AccountTransactionService;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.service.mapper.AccountTransactionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AccountTransaction}.
 */
@Service
@Transactional
public class AccountTransactionServiceImpl implements AccountTransactionService {

    private final Logger log = LoggerFactory.getLogger(AccountTransactionServiceImpl.class);

    private final AccountTransactionRepository accountTransactionRepository;

    private final AccountTransactionMapper accountTransactionMapper;

    private final AccountTransactionSearchRepository accountTransactionSearchRepository;

    public AccountTransactionServiceImpl(
        AccountTransactionRepository accountTransactionRepository,
        AccountTransactionMapper accountTransactionMapper,
        AccountTransactionSearchRepository accountTransactionSearchRepository
    ) {
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountTransactionMapper = accountTransactionMapper;
        this.accountTransactionSearchRepository = accountTransactionSearchRepository;
    }

    @Override
    public AccountTransactionDTO save(AccountTransactionDTO accountTransactionDTO) {
        log.debug("Request to save AccountTransaction : {}", accountTransactionDTO);
        AccountTransaction accountTransaction = accountTransactionMapper.toEntity(accountTransactionDTO);
        accountTransaction = accountTransactionRepository.save(accountTransaction);
        AccountTransactionDTO result = accountTransactionMapper.toDto(accountTransaction);
        accountTransactionSearchRepository.index(accountTransaction);
        return result;
    }

    @Override
    public AccountTransactionDTO update(AccountTransactionDTO accountTransactionDTO) {
        log.debug("Request to update AccountTransaction : {}", accountTransactionDTO);
        AccountTransaction accountTransaction = accountTransactionMapper.toEntity(accountTransactionDTO);
        accountTransaction = accountTransactionRepository.save(accountTransaction);
        AccountTransactionDTO result = accountTransactionMapper.toDto(accountTransaction);
        accountTransactionSearchRepository.index(accountTransaction);
        return result;
    }

    @Override
    public Optional<AccountTransactionDTO> partialUpdate(AccountTransactionDTO accountTransactionDTO) {
        log.debug("Request to partially update AccountTransaction : {}", accountTransactionDTO);

        return accountTransactionRepository
            .findById(accountTransactionDTO.getId())
            .map(existingAccountTransaction -> {
                accountTransactionMapper.partialUpdate(existingAccountTransaction, accountTransactionDTO);

                return existingAccountTransaction;
            })
            .map(accountTransactionRepository::save)
            .map(savedAccountTransaction -> {
                accountTransactionSearchRepository.index(savedAccountTransaction);
                return savedAccountTransaction;
            })
            .map(accountTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountTransactionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AccountTransactions");
        return accountTransactionRepository.findAll(pageable).map(accountTransactionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountTransactionDTO> findOne(Long id) {
        log.debug("Request to get AccountTransaction : {}", id);
        return accountTransactionRepository.findById(id).map(accountTransactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AccountTransaction : {}", id);
        accountTransactionRepository.deleteById(id);
        accountTransactionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountTransactionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of AccountTransactions for query {}", query);
        return accountTransactionSearchRepository.search(query, pageable).map(accountTransactionMapper::toDto);
    }
}
