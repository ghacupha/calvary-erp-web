package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.TransactionEntryRepository;
import io.github.calvary.service.TransactionEntryQueryService;
import io.github.calvary.service.TransactionEntryService;
import io.github.calvary.service.criteria.TransactionEntryCriteria;
import io.github.calvary.service.dto.TransactionEntryDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.TransactionEntry}.
 */
@RestController
@RequestMapping("/api")
public class TransactionEntryResource {

    private final Logger log = LoggerFactory.getLogger(TransactionEntryResource.class);

    private static final String ENTITY_NAME = "transactionEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionEntryService transactionEntryService;

    private final TransactionEntryRepository transactionEntryRepository;

    private final TransactionEntryQueryService transactionEntryQueryService;

    public TransactionEntryResource(
        TransactionEntryService transactionEntryService,
        TransactionEntryRepository transactionEntryRepository,
        TransactionEntryQueryService transactionEntryQueryService
    ) {
        this.transactionEntryService = transactionEntryService;
        this.transactionEntryRepository = transactionEntryRepository;
        this.transactionEntryQueryService = transactionEntryQueryService;
    }

    /**
     * {@code POST  /transaction-entries} : Create a new transactionEntry.
     *
     * @param transactionEntryDTO the transactionEntryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionEntryDTO, or with status {@code 400 (Bad Request)} if the transactionEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-entries")
    public ResponseEntity<TransactionEntryDTO> createTransactionEntry(@Valid @RequestBody TransactionEntryDTO transactionEntryDTO)
        throws URISyntaxException {
        log.debug("REST request to save TransactionEntry : {}", transactionEntryDTO);
        if (transactionEntryDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionEntryDTO result = transactionEntryService.save(transactionEntryDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-entries/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-entries/:id} : Updates an existing transactionEntry.
     *
     * @param id the id of the transactionEntryDTO to save.
     * @param transactionEntryDTO the transactionEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionEntryDTO,
     * or with status {@code 400 (Bad Request)} if the transactionEntryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-entries/{id}")
    public ResponseEntity<TransactionEntryDTO> updateTransactionEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionEntryDTO transactionEntryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionEntry : {}, {}", id, transactionEntryDTO);
        if (transactionEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionEntryDTO result = transactionEntryService.update(transactionEntryDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionEntryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-entries/:id} : Partial updates given fields of an existing transactionEntry, field will ignore if it is null
     *
     * @param id the id of the transactionEntryDTO to save.
     * @param transactionEntryDTO the transactionEntryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionEntryDTO,
     * or with status {@code 400 (Bad Request)} if the transactionEntryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionEntryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionEntryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-entries/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionEntryDTO> partialUpdateTransactionEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionEntryDTO transactionEntryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionEntry partially : {}, {}", id, transactionEntryDTO);
        if (transactionEntryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionEntryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionEntryDTO> result = transactionEntryService.partialUpdate(transactionEntryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionEntryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-entries} : get all the transactionEntries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionEntries in body.
     */
    @GetMapping("/transaction-entries")
    public ResponseEntity<List<TransactionEntryDTO>> getAllTransactionEntries(
        TransactionEntryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionEntries by criteria: {}", criteria);

        Page<TransactionEntryDTO> page = transactionEntryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-entries/count} : count all the transactionEntries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-entries/count")
    public ResponseEntity<Long> countTransactionEntries(TransactionEntryCriteria criteria) {
        log.debug("REST request to count TransactionEntries by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionEntryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-entries/:id} : get the "id" transactionEntry.
     *
     * @param id the id of the transactionEntryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionEntryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-entries/{id}")
    public ResponseEntity<TransactionEntryDTO> getTransactionEntry(@PathVariable Long id) {
        log.debug("REST request to get TransactionEntry : {}", id);
        Optional<TransactionEntryDTO> transactionEntryDTO = transactionEntryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionEntryDTO);
    }

    /**
     * {@code DELETE  /transaction-entries/:id} : delete the "id" transactionEntry.
     *
     * @param id the id of the transactionEntryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-entries/{id}")
    public ResponseEntity<Void> deleteTransactionEntry(@PathVariable Long id) {
        log.debug("REST request to delete TransactionEntry : {}", id);
        transactionEntryService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-entries?query=:query} : search for the transactionEntry corresponding
     * to the query.
     *
     * @param query the query of the transactionEntry search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-entries")
    public ResponseEntity<List<TransactionEntryDTO>> searchTransactionEntries(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionEntries for query {}", query);
        try {
            Page<TransactionEntryDTO> page = transactionEntryService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
