package io.github.calvary.service.impl;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.domain.Dealer;
import io.github.calvary.repository.DealerRepository;
import io.github.calvary.repository.search.DealerSearchRepository;
import io.github.calvary.service.DealerService;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.mapper.DealerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Dealer}.
 */
@Service
@Transactional
public class DealerServiceImpl implements DealerService {

    private final Logger log = LoggerFactory.getLogger(DealerServiceImpl.class);

    private final DealerRepository dealerRepository;

    private final DealerMapper dealerMapper;

    private final DealerSearchRepository dealerSearchRepository;

    public DealerServiceImpl(DealerRepository dealerRepository, DealerMapper dealerMapper, DealerSearchRepository dealerSearchRepository) {
        this.dealerRepository = dealerRepository;
        this.dealerMapper = dealerMapper;
        this.dealerSearchRepository = dealerSearchRepository;
    }

    @Override
    public DealerDTO save(DealerDTO dealerDTO) {
        log.debug("Request to save Dealer : {}", dealerDTO);
        Dealer dealer = dealerMapper.toEntity(dealerDTO);
        dealer = dealerRepository.save(dealer);
        DealerDTO result = dealerMapper.toDto(dealer);
        dealerSearchRepository.index(dealer);
        return result;
    }

    @Override
    public DealerDTO update(DealerDTO dealerDTO) {
        log.debug("Request to update Dealer : {}", dealerDTO);
        Dealer dealer = dealerMapper.toEntity(dealerDTO);
        dealer = dealerRepository.save(dealer);
        DealerDTO result = dealerMapper.toDto(dealer);
        dealerSearchRepository.index(dealer);
        return result;
    }

    @Override
    public Optional<DealerDTO> partialUpdate(DealerDTO dealerDTO) {
        log.debug("Request to partially update Dealer : {}", dealerDTO);

        return dealerRepository
            .findById(dealerDTO.getId())
            .map(existingDealer -> {
                dealerMapper.partialUpdate(existingDealer, dealerDTO);

                return existingDealer;
            })
            .map(dealerRepository::save)
            .map(savedDealer -> {
                dealerSearchRepository.index(savedDealer);
                return savedDealer;
            })
            .map(dealerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Dealers");
        return dealerRepository.findAll(pageable).map(dealerMapper::toDto);
    }

    public Page<DealerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return dealerRepository.findAllWithEagerRelationships(pageable).map(dealerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DealerDTO> findOne(Long id) {
        log.debug("Request to get Dealer : {}", id);
        return dealerRepository.findOneWithEagerRelationships(id).map(dealerMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Dealer : {}", id);
        dealerRepository.deleteById(id);
        dealerSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealerDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Dealers for query {}", query);
        return dealerSearchRepository.search(query, pageable).map(dealerMapper::toDto);
    }
}
