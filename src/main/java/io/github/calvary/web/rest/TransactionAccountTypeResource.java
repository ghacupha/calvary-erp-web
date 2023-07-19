package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.TransactionAccountTypeRepository;
import io.github.calvary.service.TransactionAccountTypeQueryService;
import io.github.calvary.service.TransactionAccountTypeService;
import io.github.calvary.service.criteria.TransactionAccountTypeCriteria;
import io.github.calvary.service.dto.TransactionAccountTypeDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.TransactionAccountType}.
 */
@RestController
@RequestMapping("/api")
public class TransactionAccountTypeResource {

    private final Logger log = LoggerFactory.getLogger(TransactionAccountTypeResource.class);

    private static final String ENTITY_NAME = "transactionAccountType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionAccountTypeService transactionAccountTypeService;

    private final TransactionAccountTypeRepository transactionAccountTypeRepository;

    private final TransactionAccountTypeQueryService transactionAccountTypeQueryService;

    public TransactionAccountTypeResource(
        TransactionAccountTypeService transactionAccountTypeService,
        TransactionAccountTypeRepository transactionAccountTypeRepository,
        TransactionAccountTypeQueryService transactionAccountTypeQueryService
    ) {
        this.transactionAccountTypeService = transactionAccountTypeService;
        this.transactionAccountTypeRepository = transactionAccountTypeRepository;
        this.transactionAccountTypeQueryService = transactionAccountTypeQueryService;
    }

    /**
     * {@code POST  /transaction-account-types} : Create a new transactionAccountType.
     *
     * @param transactionAccountTypeDTO the transactionAccountTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionAccountTypeDTO, or with status {@code 400 (Bad Request)} if the transactionAccountType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-account-types")
    public ResponseEntity<TransactionAccountTypeDTO> createTransactionAccountType(
        @Valid @RequestBody TransactionAccountTypeDTO transactionAccountTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to save TransactionAccountType : {}", transactionAccountTypeDTO);
        if (transactionAccountTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new transactionAccountType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionAccountTypeDTO result = transactionAccountTypeService.save(transactionAccountTypeDTO);
        return ResponseEntity
            .created(new URI("/api/transaction-account-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-account-types/:id} : Updates an existing transactionAccountType.
     *
     * @param id the id of the transactionAccountTypeDTO to save.
     * @param transactionAccountTypeDTO the transactionAccountTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionAccountTypeDTO,
     * or with status {@code 400 (Bad Request)} if the transactionAccountTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionAccountTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-account-types/{id}")
    public ResponseEntity<TransactionAccountTypeDTO> updateTransactionAccountType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransactionAccountTypeDTO transactionAccountTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionAccountType : {}, {}", id, transactionAccountTypeDTO);
        if (transactionAccountTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionAccountTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionAccountTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionAccountTypeDTO result = transactionAccountTypeService.update(transactionAccountTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionAccountTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-account-types/:id} : Partial updates given fields of an existing transactionAccountType, field will ignore if it is null
     *
     * @param id the id of the transactionAccountTypeDTO to save.
     * @param transactionAccountTypeDTO the transactionAccountTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionAccountTypeDTO,
     * or with status {@code 400 (Bad Request)} if the transactionAccountTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transactionAccountTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionAccountTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-account-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionAccountTypeDTO> partialUpdateTransactionAccountType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransactionAccountTypeDTO transactionAccountTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionAccountType partially : {}, {}", id, transactionAccountTypeDTO);
        if (transactionAccountTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionAccountTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionAccountTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionAccountTypeDTO> result = transactionAccountTypeService.partialUpdate(transactionAccountTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionAccountTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transaction-account-types} : get all the transactionAccountTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionAccountTypes in body.
     */
    @GetMapping("/transaction-account-types")
    public ResponseEntity<List<TransactionAccountTypeDTO>> getAllTransactionAccountTypes(
        TransactionAccountTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TransactionAccountTypes by criteria: {}", criteria);

        Page<TransactionAccountTypeDTO> page = transactionAccountTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-account-types/count} : count all the transactionAccountTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/transaction-account-types/count")
    public ResponseEntity<Long> countTransactionAccountTypes(TransactionAccountTypeCriteria criteria) {
        log.debug("REST request to count TransactionAccountTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(transactionAccountTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transaction-account-types/:id} : get the "id" transactionAccountType.
     *
     * @param id the id of the transactionAccountTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionAccountTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-account-types/{id}")
    public ResponseEntity<TransactionAccountTypeDTO> getTransactionAccountType(@PathVariable Long id) {
        log.debug("REST request to get TransactionAccountType : {}", id);
        Optional<TransactionAccountTypeDTO> transactionAccountTypeDTO = transactionAccountTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionAccountTypeDTO);
    }

    /**
     * {@code DELETE  /transaction-account-types/:id} : delete the "id" transactionAccountType.
     *
     * @param id the id of the transactionAccountTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-account-types/{id}")
    public ResponseEntity<Void> deleteTransactionAccountType(@PathVariable Long id) {
        log.debug("REST request to delete TransactionAccountType : {}", id);
        transactionAccountTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/transaction-account-types?query=:query} : search for the transactionAccountType corresponding
     * to the query.
     *
     * @param query the query of the transactionAccountType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-account-types")
    public ResponseEntity<List<TransactionAccountTypeDTO>> searchTransactionAccountTypes(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionAccountTypes for query {}", query);
        try {
            Page<TransactionAccountTypeDTO> page = transactionAccountTypeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
