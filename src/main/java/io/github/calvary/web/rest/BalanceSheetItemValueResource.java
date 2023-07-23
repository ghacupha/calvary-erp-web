package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.BalanceSheetItemValueRepository;
import io.github.calvary.service.BalanceSheetItemValueQueryService;
import io.github.calvary.service.BalanceSheetItemValueService;
import io.github.calvary.service.criteria.BalanceSheetItemValueCriteria;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.BalanceSheetItemValue}.
 */
@RestController
@RequestMapping("/api")
public class BalanceSheetItemValueResource {

    private final Logger log = LoggerFactory.getLogger(BalanceSheetItemValueResource.class);

    private static final String ENTITY_NAME = "balanceSheetItemValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BalanceSheetItemValueService balanceSheetItemValueService;

    private final BalanceSheetItemValueRepository balanceSheetItemValueRepository;

    private final BalanceSheetItemValueQueryService balanceSheetItemValueQueryService;

    public BalanceSheetItemValueResource(
        BalanceSheetItemValueService balanceSheetItemValueService,
        BalanceSheetItemValueRepository balanceSheetItemValueRepository,
        BalanceSheetItemValueQueryService balanceSheetItemValueQueryService
    ) {
        this.balanceSheetItemValueService = balanceSheetItemValueService;
        this.balanceSheetItemValueRepository = balanceSheetItemValueRepository;
        this.balanceSheetItemValueQueryService = balanceSheetItemValueQueryService;
    }

    /**
     * {@code POST  /balance-sheet-item-values} : Create a new balanceSheetItemValue.
     *
     * @param balanceSheetItemValueDTO the balanceSheetItemValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new balanceSheetItemValueDTO, or with status {@code 400 (Bad Request)} if the balanceSheetItemValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/balance-sheet-item-values")
    public ResponseEntity<BalanceSheetItemValueDTO> createBalanceSheetItemValue(
        @Valid @RequestBody BalanceSheetItemValueDTO balanceSheetItemValueDTO
    ) throws URISyntaxException {
        log.debug("REST request to save BalanceSheetItemValue : {}", balanceSheetItemValueDTO);
        if (balanceSheetItemValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new balanceSheetItemValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BalanceSheetItemValueDTO result = balanceSheetItemValueService.save(balanceSheetItemValueDTO);
        return ResponseEntity
            .created(new URI("/api/balance-sheet-item-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /balance-sheet-item-values/:id} : Updates an existing balanceSheetItemValue.
     *
     * @param id the id of the balanceSheetItemValueDTO to save.
     * @param balanceSheetItemValueDTO the balanceSheetItemValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated balanceSheetItemValueDTO,
     * or with status {@code 400 (Bad Request)} if the balanceSheetItemValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the balanceSheetItemValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/balance-sheet-item-values/{id}")
    public ResponseEntity<BalanceSheetItemValueDTO> updateBalanceSheetItemValue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BalanceSheetItemValueDTO balanceSheetItemValueDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BalanceSheetItemValue : {}, {}", id, balanceSheetItemValueDTO);
        if (balanceSheetItemValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, balanceSheetItemValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!balanceSheetItemValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BalanceSheetItemValueDTO result = balanceSheetItemValueService.update(balanceSheetItemValueDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, balanceSheetItemValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /balance-sheet-item-values/:id} : Partial updates given fields of an existing balanceSheetItemValue, field will ignore if it is null
     *
     * @param id the id of the balanceSheetItemValueDTO to save.
     * @param balanceSheetItemValueDTO the balanceSheetItemValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated balanceSheetItemValueDTO,
     * or with status {@code 400 (Bad Request)} if the balanceSheetItemValueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the balanceSheetItemValueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the balanceSheetItemValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/balance-sheet-item-values/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BalanceSheetItemValueDTO> partialUpdateBalanceSheetItemValue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BalanceSheetItemValueDTO balanceSheetItemValueDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BalanceSheetItemValue partially : {}, {}", id, balanceSheetItemValueDTO);
        if (balanceSheetItemValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, balanceSheetItemValueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!balanceSheetItemValueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BalanceSheetItemValueDTO> result = balanceSheetItemValueService.partialUpdate(balanceSheetItemValueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, balanceSheetItemValueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /balance-sheet-item-values} : get all the balanceSheetItemValues.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of balanceSheetItemValues in body.
     */
    @GetMapping("/balance-sheet-item-values")
    public ResponseEntity<List<BalanceSheetItemValueDTO>> getAllBalanceSheetItemValues(
        BalanceSheetItemValueCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get BalanceSheetItemValues by criteria: {}", criteria);

        Page<BalanceSheetItemValueDTO> page = balanceSheetItemValueQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /balance-sheet-item-values/count} : count all the balanceSheetItemValues.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/balance-sheet-item-values/count")
    public ResponseEntity<Long> countBalanceSheetItemValues(BalanceSheetItemValueCriteria criteria) {
        log.debug("REST request to count BalanceSheetItemValues by criteria: {}", criteria);
        return ResponseEntity.ok().body(balanceSheetItemValueQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /balance-sheet-item-values/:id} : get the "id" balanceSheetItemValue.
     *
     * @param id the id of the balanceSheetItemValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the balanceSheetItemValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/balance-sheet-item-values/{id}")
    public ResponseEntity<BalanceSheetItemValueDTO> getBalanceSheetItemValue(@PathVariable Long id) {
        log.debug("REST request to get BalanceSheetItemValue : {}", id);
        Optional<BalanceSheetItemValueDTO> balanceSheetItemValueDTO = balanceSheetItemValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(balanceSheetItemValueDTO);
    }

    /**
     * {@code DELETE  /balance-sheet-item-values/:id} : delete the "id" balanceSheetItemValue.
     *
     * @param id the id of the balanceSheetItemValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/balance-sheet-item-values/{id}")
    public ResponseEntity<Void> deleteBalanceSheetItemValue(@PathVariable Long id) {
        log.debug("REST request to delete BalanceSheetItemValue : {}", id);
        balanceSheetItemValueService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/balance-sheet-item-values?query=:query} : search for the balanceSheetItemValue corresponding
     * to the query.
     *
     * @param query the query of the balanceSheetItemValue search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/balance-sheet-item-values")
    public ResponseEntity<List<BalanceSheetItemValueDTO>> searchBalanceSheetItemValues(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of BalanceSheetItemValues for query {}", query);
        try {
            Page<BalanceSheetItemValueDTO> page = balanceSheetItemValueService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
