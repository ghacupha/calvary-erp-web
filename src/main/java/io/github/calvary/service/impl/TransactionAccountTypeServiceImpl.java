package io.github.calvary.service.impl;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.domain.TransactionAccountType;
import io.github.calvary.repository.TransactionAccountTypeRepository;
import io.github.calvary.repository.search.TransactionAccountTypeSearchRepository;
import io.github.calvary.service.TransactionAccountTypeService;
import io.github.calvary.service.dto.TransactionAccountTypeDTO;
import io.github.calvary.service.mapper.TransactionAccountTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TransactionAccountType}.
 */
@Service
@Transactional
public class TransactionAccountTypeServiceImpl implements TransactionAccountTypeService {

    private final Logger log = LoggerFactory.getLogger(TransactionAccountTypeServiceImpl.class);

    private final TransactionAccountTypeRepository transactionAccountTypeRepository;

    private final TransactionAccountTypeMapper transactionAccountTypeMapper;

    private final TransactionAccountTypeSearchRepository transactionAccountTypeSearchRepository;

    public TransactionAccountTypeServiceImpl(
        TransactionAccountTypeRepository transactionAccountTypeRepository,
        TransactionAccountTypeMapper transactionAccountTypeMapper,
        TransactionAccountTypeSearchRepository transactionAccountTypeSearchRepository
    ) {
        this.transactionAccountTypeRepository = transactionAccountTypeRepository;
        this.transactionAccountTypeMapper = transactionAccountTypeMapper;
        this.transactionAccountTypeSearchRepository = transactionAccountTypeSearchRepository;
    }

    @Override
    public TransactionAccountTypeDTO save(TransactionAccountTypeDTO transactionAccountTypeDTO) {
        log.debug("Request to save TransactionAccountType : {}", transactionAccountTypeDTO);
        TransactionAccountType transactionAccountType = transactionAccountTypeMapper.toEntity(transactionAccountTypeDTO);
        transactionAccountType = transactionAccountTypeRepository.save(transactionAccountType);
        TransactionAccountTypeDTO result = transactionAccountTypeMapper.toDto(transactionAccountType);
        transactionAccountTypeSearchRepository.index(transactionAccountType);
        return result;
    }

    @Override
    public TransactionAccountTypeDTO update(TransactionAccountTypeDTO transactionAccountTypeDTO) {
        log.debug("Request to update TransactionAccountType : {}", transactionAccountTypeDTO);
        TransactionAccountType transactionAccountType = transactionAccountTypeMapper.toEntity(transactionAccountTypeDTO);
        transactionAccountType = transactionAccountTypeRepository.save(transactionAccountType);
        TransactionAccountTypeDTO result = transactionAccountTypeMapper.toDto(transactionAccountType);
        transactionAccountTypeSearchRepository.index(transactionAccountType);
        return result;
    }

    @Override
    public Optional<TransactionAccountTypeDTO> partialUpdate(TransactionAccountTypeDTO transactionAccountTypeDTO) {
        log.debug("Request to partially update TransactionAccountType : {}", transactionAccountTypeDTO);

        return transactionAccountTypeRepository
            .findById(transactionAccountTypeDTO.getId())
            .map(existingTransactionAccountType -> {
                transactionAccountTypeMapper.partialUpdate(existingTransactionAccountType, transactionAccountTypeDTO);

                return existingTransactionAccountType;
            })
            .map(transactionAccountTypeRepository::save)
            .map(savedTransactionAccountType -> {
                transactionAccountTypeSearchRepository.index(savedTransactionAccountType);
                return savedTransactionAccountType;
            })
            .map(transactionAccountTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionAccountTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionAccountTypes");
        return transactionAccountTypeRepository.findAll(pageable).map(transactionAccountTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionAccountTypeDTO> findOne(Long id) {
        log.debug("Request to get TransactionAccountType : {}", id);
        return transactionAccountTypeRepository.findById(id).map(transactionAccountTypeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TransactionAccountType : {}", id);
        transactionAccountTypeRepository.deleteById(id);
        transactionAccountTypeSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionAccountTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionAccountTypes for query {}", query);
        return transactionAccountTypeSearchRepository.search(query, pageable).map(transactionAccountTypeMapper::toDto);
    }
}
