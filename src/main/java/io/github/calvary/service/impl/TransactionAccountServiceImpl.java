package io.github.calvary.service.impl;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.repository.TransactionAccountRepository;
import io.github.calvary.repository.search.TransactionAccountSearchRepository;
import io.github.calvary.service.TransactionAccountService;
import io.github.calvary.service.dto.TransactionAccountDTO;
import io.github.calvary.service.mapper.TransactionAccountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionAccount}.
 */
@Service
@Transactional
public class TransactionAccountServiceImpl implements TransactionAccountService {

    private final Logger log = LoggerFactory.getLogger(TransactionAccountServiceImpl.class);

    private final TransactionAccountRepository transactionAccountRepository;

    private final TransactionAccountMapper transactionAccountMapper;

    private final TransactionAccountSearchRepository transactionAccountSearchRepository;

    public TransactionAccountServiceImpl(
        TransactionAccountRepository transactionAccountRepository,
        TransactionAccountMapper transactionAccountMapper,
        TransactionAccountSearchRepository transactionAccountSearchRepository
    ) {
        this.transactionAccountRepository = transactionAccountRepository;
        this.transactionAccountMapper = transactionAccountMapper;
        this.transactionAccountSearchRepository = transactionAccountSearchRepository;
    }

    @Override
    public TransactionAccountDTO save(TransactionAccountDTO transactionAccountDTO) {
        log.debug("Request to save TransactionAccount : {}", transactionAccountDTO);
        TransactionAccount transactionAccount = transactionAccountMapper.toEntity(transactionAccountDTO);
        transactionAccount = transactionAccountRepository.save(transactionAccount);
        TransactionAccountDTO result = transactionAccountMapper.toDto(transactionAccount);
        transactionAccountSearchRepository.index(transactionAccount);
        return result;
    }

    @Override
    public TransactionAccountDTO update(TransactionAccountDTO transactionAccountDTO) {
        log.debug("Request to update TransactionAccount : {}", transactionAccountDTO);
        TransactionAccount transactionAccount = transactionAccountMapper.toEntity(transactionAccountDTO);
        transactionAccount = transactionAccountRepository.save(transactionAccount);
        TransactionAccountDTO result = transactionAccountMapper.toDto(transactionAccount);
        transactionAccountSearchRepository.index(transactionAccount);
        return result;
    }

    @Override
    public Optional<TransactionAccountDTO> partialUpdate(TransactionAccountDTO transactionAccountDTO) {
        log.debug("Request to partially update TransactionAccount : {}", transactionAccountDTO);

        return transactionAccountRepository
            .findById(transactionAccountDTO.getId())
            .map(existingTransactionAccount -> {
                transactionAccountMapper.partialUpdate(existingTransactionAccount, transactionAccountDTO);

                return existingTransactionAccount;
            })
            .map(transactionAccountRepository::save)
            .map(savedTransactionAccount -> {
                transactionAccountSearchRepository.index(savedTransactionAccount);
                return savedTransactionAccount;
            })
            .map(transactionAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionAccountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionAccounts");
        return transactionAccountRepository.findAll(pageable).map(transactionAccountMapper::toDto);
    }

    public Page<TransactionAccountDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transactionAccountRepository.findAllWithEagerRelationships(pageable).map(transactionAccountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionAccountDTO> findOne(Long id) {
        log.debug("Request to get TransactionAccount : {}", id);
        return transactionAccountRepository.findOneWithEagerRelationships(id).map(transactionAccountMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionAccount : {}", id);
        transactionAccountRepository.deleteById(id);
        transactionAccountSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionAccountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionAccounts for query {}", query);
        return transactionAccountSearchRepository.search(query, pageable).map(transactionAccountMapper::toDto);
    }
}
