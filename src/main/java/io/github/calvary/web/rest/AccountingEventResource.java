package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.AccountingEventRepository;
import io.github.calvary.service.AccountingEventQueryService;
import io.github.calvary.service.AccountingEventService;
import io.github.calvary.service.criteria.AccountingEventCriteria;
import io.github.calvary.service.dto.AccountingEventDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.AccountingEvent}.
 */
@RestController
@RequestMapping("/api")
public class AccountingEventResource {

    private final Logger log = LoggerFactory.getLogger(AccountingEventResource.class);

    private static final String ENTITY_NAME = "accountingEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountingEventService accountingEventService;

    private final AccountingEventRepository accountingEventRepository;

    private final AccountingEventQueryService accountingEventQueryService;

    public AccountingEventResource(
        AccountingEventService accountingEventService,
        AccountingEventRepository accountingEventRepository,
        AccountingEventQueryService accountingEventQueryService
    ) {
        this.accountingEventService = accountingEventService;
        this.accountingEventRepository = accountingEventRepository;
        this.accountingEventQueryService = accountingEventQueryService;
    }

    /**
     * {@code POST  /accounting-events} : Create a new accountingEvent.
     *
     * @param accountingEventDTO the accountingEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountingEventDTO, or with status {@code 400 (Bad Request)} if the accountingEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/accounting-events")
    public ResponseEntity<AccountingEventDTO> createAccountingEvent(@Valid @RequestBody AccountingEventDTO accountingEventDTO)
        throws URISyntaxException {
        log.debug("REST request to save AccountingEvent : {}", accountingEventDTO);
        if (accountingEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new accountingEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AccountingEventDTO result = accountingEventService.save(accountingEventDTO);
        return ResponseEntity
            .created(new URI("/api/accounting-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /accounting-events/:id} : Updates an existing accountingEvent.
     *
     * @param id the id of the accountingEventDTO to save.
     * @param accountingEventDTO the accountingEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountingEventDTO,
     * or with status {@code 400 (Bad Request)} if the accountingEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountingEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/accounting-events/{id}")
    public ResponseEntity<AccountingEventDTO> updateAccountingEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccountingEventDTO accountingEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AccountingEvent : {}, {}", id, accountingEventDTO);
        if (accountingEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountingEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountingEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountingEventDTO result = accountingEventService.update(accountingEventDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accountingEventDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /accounting-events/:id} : Partial updates given fields of an existing accountingEvent, field will ignore if it is null
     *
     * @param id the id of the accountingEventDTO to save.
     * @param accountingEventDTO the accountingEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountingEventDTO,
     * or with status {@code 400 (Bad Request)} if the accountingEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accountingEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountingEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/accounting-events/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccountingEventDTO> partialUpdateAccountingEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AccountingEventDTO accountingEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AccountingEvent partially : {}, {}", id, accountingEventDTO);
        if (accountingEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountingEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountingEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountingEventDTO> result = accountingEventService.partialUpdate(accountingEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accountingEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /accounting-events} : get all the accountingEvents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountingEvents in body.
     */
    @GetMapping("/accounting-events")
    public ResponseEntity<List<AccountingEventDTO>> getAllAccountingEvents(
        AccountingEventCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get AccountingEvents by criteria: {}", criteria);

        Page<AccountingEventDTO> page = accountingEventQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /accounting-events/count} : count all the accountingEvents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/accounting-events/count")
    public ResponseEntity<Long> countAccountingEvents(AccountingEventCriteria criteria) {
        log.debug("REST request to count AccountingEvents by criteria: {}", criteria);
        return ResponseEntity.ok().body(accountingEventQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /accounting-events/:id} : get the "id" accountingEvent.
     *
     * @param id the id of the accountingEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountingEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/accounting-events/{id}")
    public ResponseEntity<AccountingEventDTO> getAccountingEvent(@PathVariable Long id) {
        log.debug("REST request to get AccountingEvent : {}", id);
        Optional<AccountingEventDTO> accountingEventDTO = accountingEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accountingEventDTO);
    }

    /**
     * {@code DELETE  /accounting-events/:id} : delete the "id" accountingEvent.
     *
     * @param id the id of the accountingEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/accounting-events/{id}")
    public ResponseEntity<Void> deleteAccountingEvent(@PathVariable Long id) {
        log.debug("REST request to delete AccountingEvent : {}", id);
        accountingEventService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/accounting-events?query=:query} : search for the accountingEvent corresponding
     * to the query.
     *
     * @param query the query of the accountingEvent search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/accounting-events")
    public ResponseEntity<List<AccountingEventDTO>> searchAccountingEvents(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of AccountingEvents for query {}", query);
        try {
            Page<AccountingEventDTO> page = accountingEventService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
