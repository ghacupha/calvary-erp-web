package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import io.github.calvary.service.BalanceSheetItemTypeQueryService;
import io.github.calvary.service.BalanceSheetItemTypeService;
import io.github.calvary.service.criteria.BalanceSheetItemTypeCriteria;
import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.BalanceSheetItemType}.
 */
@RestController
@RequestMapping("/api")
public class BalanceSheetItemTypeResource {

    private final Logger log = LoggerFactory.getLogger(BalanceSheetItemTypeResource.class);

    private static final String ENTITY_NAME = "balanceSheetItemType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BalanceSheetItemTypeService balanceSheetItemTypeService;

    private final BalanceSheetItemTypeRepository balanceSheetItemTypeRepository;

    private final BalanceSheetItemTypeQueryService balanceSheetItemTypeQueryService;

    public BalanceSheetItemTypeResource(
        BalanceSheetItemTypeService balanceSheetItemTypeService,
        BalanceSheetItemTypeRepository balanceSheetItemTypeRepository,
        BalanceSheetItemTypeQueryService balanceSheetItemTypeQueryService
    ) {
        this.balanceSheetItemTypeService = balanceSheetItemTypeService;
        this.balanceSheetItemTypeRepository = balanceSheetItemTypeRepository;
        this.balanceSheetItemTypeQueryService = balanceSheetItemTypeQueryService;
    }

    /**
     * {@code POST  /balance-sheet-item-types} : Create a new balanceSheetItemType.
     *
     * @param balanceSheetItemTypeDTO the balanceSheetItemTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new balanceSheetItemTypeDTO, or with status {@code 400 (Bad Request)} if the balanceSheetItemType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/balance-sheet-item-types")
    public ResponseEntity<BalanceSheetItemTypeDTO> createBalanceSheetItemType(
        @Valid @RequestBody BalanceSheetItemTypeDTO balanceSheetItemTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to save BalanceSheetItemType : {}", balanceSheetItemTypeDTO);
        if (balanceSheetItemTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new balanceSheetItemType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BalanceSheetItemTypeDTO result = balanceSheetItemTypeService.save(balanceSheetItemTypeDTO);
        return ResponseEntity
            .created(new URI("/api/balance-sheet-item-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /balance-sheet-item-types/:id} : Updates an existing balanceSheetItemType.
     *
     * @param id the id of the balanceSheetItemTypeDTO to save.
     * @param balanceSheetItemTypeDTO the balanceSheetItemTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated balanceSheetItemTypeDTO,
     * or with status {@code 400 (Bad Request)} if the balanceSheetItemTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the balanceSheetItemTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/balance-sheet-item-types/{id}")
    public ResponseEntity<BalanceSheetItemTypeDTO> updateBalanceSheetItemType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BalanceSheetItemTypeDTO balanceSheetItemTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BalanceSheetItemType : {}, {}", id, balanceSheetItemTypeDTO);
        if (balanceSheetItemTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, balanceSheetItemTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!balanceSheetItemTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BalanceSheetItemTypeDTO result = balanceSheetItemTypeService.update(balanceSheetItemTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, balanceSheetItemTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /balance-sheet-item-types/:id} : Partial updates given fields of an existing balanceSheetItemType, field will ignore if it is null
     *
     * @param id the id of the balanceSheetItemTypeDTO to save.
     * @param balanceSheetItemTypeDTO the balanceSheetItemTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated balanceSheetItemTypeDTO,
     * or with status {@code 400 (Bad Request)} if the balanceSheetItemTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the balanceSheetItemTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the balanceSheetItemTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/balance-sheet-item-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BalanceSheetItemTypeDTO> partialUpdateBalanceSheetItemType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BalanceSheetItemTypeDTO balanceSheetItemTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BalanceSheetItemType partially : {}, {}", id, balanceSheetItemTypeDTO);
        if (balanceSheetItemTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, balanceSheetItemTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!balanceSheetItemTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BalanceSheetItemTypeDTO> result = balanceSheetItemTypeService.partialUpdate(balanceSheetItemTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, balanceSheetItemTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /balance-sheet-item-types} : get all the balanceSheetItemTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of balanceSheetItemTypes in body.
     */
    @GetMapping("/balance-sheet-item-types")
    public ResponseEntity<List<BalanceSheetItemTypeDTO>> getAllBalanceSheetItemTypes(
        BalanceSheetItemTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get BalanceSheetItemTypes by criteria: {}", criteria);

        Page<BalanceSheetItemTypeDTO> page = balanceSheetItemTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /balance-sheet-item-types/count} : count all the balanceSheetItemTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/balance-sheet-item-types/count")
    public ResponseEntity<Long> countBalanceSheetItemTypes(BalanceSheetItemTypeCriteria criteria) {
        log.debug("REST request to count BalanceSheetItemTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(balanceSheetItemTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /balance-sheet-item-types/:id} : get the "id" balanceSheetItemType.
     *
     * @param id the id of the balanceSheetItemTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the balanceSheetItemTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/balance-sheet-item-types/{id}")
    public ResponseEntity<BalanceSheetItemTypeDTO> getBalanceSheetItemType(@PathVariable Long id) {
        log.debug("REST request to get BalanceSheetItemType : {}", id);
        Optional<BalanceSheetItemTypeDTO> balanceSheetItemTypeDTO = balanceSheetItemTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(balanceSheetItemTypeDTO);
    }

    /**
     * {@code DELETE  /balance-sheet-item-types/:id} : delete the "id" balanceSheetItemType.
     *
     * @param id the id of the balanceSheetItemTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/balance-sheet-item-types/{id}")
    public ResponseEntity<Void> deleteBalanceSheetItemType(@PathVariable Long id) {
        log.debug("REST request to delete BalanceSheetItemType : {}", id);
        balanceSheetItemTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/balance-sheet-item-types?query=:query} : search for the balanceSheetItemType corresponding
     * to the query.
     *
     * @param query the query of the balanceSheetItemType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/balance-sheet-item-types")
    public ResponseEntity<List<BalanceSheetItemTypeDTO>> searchBalanceSheetItemTypes(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of BalanceSheetItemTypes for query {}", query);
        try {
            Page<BalanceSheetItemTypeDTO> page = balanceSheetItemTypeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
