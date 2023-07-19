package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.TransactionCurrencyRepository;
import io.github.calvary.service.TransactionCurrencyQueryService;
import io.github.calvary.service.TransactionCurrencyService;
import io.github.calvary.service.criteria.TransactionCurrencyCriteria;
import io.github.calvary.service.dto.TransactionCurrencyDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import io.github.calvary.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.github.calvary.domain.TransactionCurrency}.
 */
@RestController
@RequestMapping("/api")
public class TransactionCurrencyResource {

    private final Logger log = LoggerFactory.getLogger(TransactionCurrencyResource.class);

    private static final String ENTITY_NAME = "transactionCurrency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionCurrencyService transactionCurrencyService;

    private final TransactionCurrencyRepository transactionCurrencyRepository;

    private final TransactionCurrencyQueryService transactionCurrencyQueryService;

    public TransactionCurrencyResource(
        TransactionCurrencyService transactionCurrencyService,
        TransactionCurrencyRepository transactionCurrencyRepository,
        TransactionCurrencyQueryService transactionCurrencyQueryService
    ) {
        this.transactionCurrencyService = transactionCurrencyService;
        this.transactionCurrencyRepository = transactionCurrencyRepository;
        this.transactionCurrencyQueryService = transactionCurrencyQueryService;
    }

    /**
     * {@code POST  /transaction-currencies} : Create a new transactionCurrency.
     *
     * @param transactionCurrencyDTO the transactionCurrencyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionCurrencyDTO, or with status {@code 400 (Bad Request)} if the transactionCurrency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-currencies")
    public ResponseEntity<TransactionCurrencyDTO> createTransactionCurrency(
        @Valid @RequestBody TransactionCurrencyDTO transactionCurrencyDTO
    ) throws URISyntaxException {
        log.debug("REST request to save TransactionCurrency : {}", transactionCurrencyDTO);
        if (transactionCurrencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionCurrency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionCurrencyDTO result = transactionCurrencyService.save(transactionCurrencyDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-currencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-currencies/:id} : Updates an existing transactionCurrency.
     *
     * @param id the id of the transactionCurrencyDTO to save.
     * @param transactionCurrencyDTO the transactionCurrencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionCurrencyDTO,
     * or with status {@code 400 (Bad Request)} if the transactionCurrencyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionCurrencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-currencies/{id}")
    public ResponseEntity<TransactionCurrencyDTO> updateTransactionCurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionCurrencyDTO transactionCurrencyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionCurrency : {}, {}", id, transactionCurrencyDTO);
        if (transactionCurrencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionCurrencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionCurrencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionCurrencyDTO result = transactionCurrencyService.update(transactionCurrencyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionCurrencyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-currencies/:id} : Partial updates given fields of an existing transactionCurrency, field will ignore if it is null
     *
     * @param id the id of the transactionCurrencyDTO to save.
     * @param transactionCurrencyDTO the transactionCurrencyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionCurrencyDTO,
     * or with status {@code 400 (Bad Request)} if the transactionCurrencyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionCurrencyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionCurrencyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-currencies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionCurrencyDTO> partialUpdateTransactionCurrency(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionCurrencyDTO transactionCurrencyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionCurrency partially : {}, {}", id, transactionCurrencyDTO);
        if (transactionCurrencyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionCurrencyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionCurrencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionCurrencyDTO> result = transactionCurrencyService.partialUpdate(transactionCurrencyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionCurrencyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-currencies} : get all the transactionCurrencies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionCurrencies in body.
     */
    @GetMapping("/transaction-currencies")
    public ResponseEntity<List<TransactionCurrencyDTO>> getAllTransactionCurrencies(
        TransactionCurrencyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionCurrencies by criteria: {}", criteria);

        Page<TransactionCurrencyDTO> page = transactionCurrencyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-currencies/count} : count all the transactionCurrencies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-currencies/count")
    public ResponseEntity<Long> countTransactionCurrencies(TransactionCurrencyCriteria criteria) {
        log.debug("REST request to count TransactionCurrencies by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionCurrencyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-currencies/:id} : get the "id" transactionCurrency.
     *
     * @param id the id of the transactionCurrencyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionCurrencyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-currencies/{id}")
    public ResponseEntity<TransactionCurrencyDTO> getTransactionCurrency(@PathVariable Long id) {
        log.debug("REST request to get TransactionCurrency : {}", id);
        Optional<TransactionCurrencyDTO> transactionCurrencyDTO = transactionCurrencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionCurrencyDTO);
    }

    /**
     * {@code DELETE  /transaction-currencies/:id} : delete the "id" transactionCurrency.
     *
     * @param id the id of the transactionCurrencyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-currencies/{id}")
    public ResponseEntity<Void> deleteTransactionCurrency(@PathVariable Long id) {
        log.debug("REST request to delete TransactionCurrency : {}", id);
        transactionCurrencyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-currencies?query=:query} : search for the transactionCurrency corresponding
     * to the query.
     *
     * @param query the query of the transactionCurrency search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-currencies")
    public ResponseEntity<List<TransactionCurrencyDTO>> searchTransactionCurrencies(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionCurrencies for query {}", query);
        try {
            Page<TransactionCurrencyDTO> page = transactionCurrencyService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
