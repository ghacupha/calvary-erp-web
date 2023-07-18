package io.github.calvary.service.impl;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.repository.TransactionEntryRepository;
import io.github.calvary.repository.search.TransactionEntrySearchRepository;
import io.github.calvary.service.TransactionEntryService;
import io.github.calvary.service.dto.TransactionEntryDTO;
import io.github.calvary.service.mapper.TransactionEntryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionEntry}.
 */
@Service
@Transactional
public class TransactionEntryServiceImpl implements TransactionEntryService {

    private final Logger log = LoggerFactory.getLogger(TransactionEntryServiceImpl.class);

    private final TransactionEntryRepository transactionEntryRepository;

    private final TransactionEntryMapper transactionEntryMapper;

    private final TransactionEntrySearchRepository transactionEntrySearchRepository;

    public TransactionEntryServiceImpl(
        TransactionEntryRepository transactionEntryRepository,
        TransactionEntryMapper transactionEntryMapper,
        TransactionEntrySearchRepository transactionEntrySearchRepository
    ) {
        this.transactionEntryRepository = transactionEntryRepository;
        this.transactionEntryMapper = transactionEntryMapper;
        this.transactionEntrySearchRepository = transactionEntrySearchRepository;
    }

    @Override
    public TransactionEntryDTO save(TransactionEntryDTO transactionEntryDTO) {
        log.debug("Request to save TransactionEntry : {}", transactionEntryDTO);
        TransactionEntry transactionEntry = transactionEntryMapper.toEntity(transactionEntryDTO);
        transactionEntry = transactionEntryRepository.save(transactionEntry);
        TransactionEntryDTO result = transactionEntryMapper.toDto(transactionEntry);
        transactionEntrySearchRepository.index(transactionEntry);
        return result;
    }

    @Override
    public TransactionEntryDTO update(TransactionEntryDTO transactionEntryDTO) {
        log.debug("Request to update TransactionEntry : {}", transactionEntryDTO);
        TransactionEntry transactionEntry = transactionEntryMapper.toEntity(transactionEntryDTO);
        transactionEntry = transactionEntryRepository.save(transactionEntry);
        TransactionEntryDTO result = transactionEntryMapper.toDto(transactionEntry);
        transactionEntrySearchRepository.index(transactionEntry);
        return result;
    }

    @Override
    public Optional<TransactionEntryDTO> partialUpdate(TransactionEntryDTO transactionEntryDTO) {
        log.debug("Request to partially update TransactionEntry : {}", transactionEntryDTO);

        return transactionEntryRepository
            .findById(transactionEntryDTO.getId())
            .map(existingTransactionEntry -> {
                transactionEntryMapper.partialUpdate(existingTransactionEntry, transactionEntryDTO);

                return existingTransactionEntry;
            })
            .map(transactionEntryRepository::save)
            .map(savedTransactionEntry -> {
                transactionEntrySearchRepository.index(savedTransactionEntry);
                return savedTransactionEntry;
            })
            .map(transactionEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionEntryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionEntries");
        return transactionEntryRepository.findAll(pageable).map(transactionEntryMapper::toDto);
    }

    public Page<TransactionEntryDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transactionEntryRepository.findAllWithEagerRelationships(pageable).map(transactionEntryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionEntryDTO> findOne(Long id) {
        log.debug("Request to get TransactionEntry : {}", id);
        return transactionEntryRepository.findOneWithEagerRelationships(id).map(transactionEntryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionEntry : {}", id);
        transactionEntryRepository.deleteById(id);
        transactionEntrySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionEntryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionEntries for query {}", query);
        return transactionEntrySearchRepository.search(query, pageable).map(transactionEntryMapper::toDto);
    }
}
