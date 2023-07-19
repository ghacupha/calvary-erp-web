package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.DealerTypeRepository;
import io.github.calvary.service.DealerTypeQueryService;
import io.github.calvary.service.DealerTypeService;
import io.github.calvary.service.criteria.DealerTypeCriteria;
import io.github.calvary.service.dto.DealerTypeDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.DealerType}.
 */
@RestController
@RequestMapping("/api")
public class DealerTypeResource {

    private final Logger log = LoggerFactory.getLogger(DealerTypeResource.class);

    private static final String ENTITY_NAME = "dealerType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DealerTypeService dealerTypeService;

    private final DealerTypeRepository dealerTypeRepository;

    private final DealerTypeQueryService dealerTypeQueryService;

    public DealerTypeResource(
        DealerTypeService dealerTypeService,
        DealerTypeRepository dealerTypeRepository,
        DealerTypeQueryService dealerTypeQueryService
    ) {
        this.dealerTypeService = dealerTypeService;
        this.dealerTypeRepository = dealerTypeRepository;
        this.dealerTypeQueryService = dealerTypeQueryService;
    }

    /**
     * {@code POST  /dealer-types} : Create a new dealerType.
     *
     * @param dealerTypeDTO the dealerTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dealerTypeDTO, or with status {@code 400 (Bad Request)} if the dealerType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/dealer-types")
    public ResponseEntity<DealerTypeDTO> createDealerType(@Valid @RequestBody DealerTypeDTO dealerTypeDTO) throws URISyntaxException {
        log.debug("REST request to save DealerType : {}", dealerTypeDTO);
        if (dealerTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new dealerType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DealerTypeDTO result = dealerTypeService.save(dealerTypeDTO);
        return ResponseEntity
            .created(new URI("/api/dealer-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /dealer-types/:id} : Updates an existing dealerType.
     *
     * @param id the id of the dealerTypeDTO to save.
     * @param dealerTypeDTO the dealerTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dealerTypeDTO,
     * or with status {@code 400 (Bad Request)} if the dealerTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dealerTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/dealer-types/{id}")
    public ResponseEntity<DealerTypeDTO> updateDealerType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DealerTypeDTO dealerTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update DealerType : {}, {}", id, dealerTypeDTO);
        if (dealerTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dealerTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dealerTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DealerTypeDTO result = dealerTypeService.update(dealerTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dealerTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /dealer-types/:id} : Partial updates given fields of an existing dealerType, field will ignore if it is null
     *
     * @param id the id of the dealerTypeDTO to save.
     * @param dealerTypeDTO the dealerTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dealerTypeDTO,
     * or with status {@code 400 (Bad Request)} if the dealerTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dealerTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dealerTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/dealer-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DealerTypeDTO> partialUpdateDealerType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DealerTypeDTO dealerTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update DealerType partially : {}, {}", id, dealerTypeDTO);
        if (dealerTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dealerTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dealerTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DealerTypeDTO> result = dealerTypeService.partialUpdate(dealerTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dealerTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /dealer-types} : get all the dealerTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dealerTypes in body.
     */
    @GetMapping("/dealer-types")
    public ResponseEntity<List<DealerTypeDTO>> getAllDealerTypes(
        DealerTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get DealerTypes by criteria: {}", criteria);

        Page<DealerTypeDTO> page = dealerTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dealer-types/count} : count all the dealerTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/dealer-types/count")
    public ResponseEntity<Long> countDealerTypes(DealerTypeCriteria criteria) {
        log.debug("REST request to count DealerTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(dealerTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dealer-types/:id} : get the "id" dealerType.
     *
     * @param id the id of the dealerTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dealerTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/dealer-types/{id}")
    public ResponseEntity<DealerTypeDTO> getDealerType(@PathVariable Long id) {
        log.debug("REST request to get DealerType : {}", id);
        Optional<DealerTypeDTO> dealerTypeDTO = dealerTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dealerTypeDTO);
    }

    /**
     * {@code DELETE  /dealer-types/:id} : delete the "id" dealerType.
     *
     * @param id the id of the dealerTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/dealer-types/{id}")
    public ResponseEntity<Void> deleteDealerType(@PathVariable Long id) {
        log.debug("REST request to delete DealerType : {}", id);
        dealerTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/dealer-types?query=:query} : search for the dealerType corresponding
     * to the query.
     *
     * @param query the query of the dealerType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/dealer-types")
    public ResponseEntity<List<DealerTypeDTO>> searchDealerTypes(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of DealerTypes for query {}", query);
        try {
            Page<DealerTypeDTO> page = dealerTypeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
