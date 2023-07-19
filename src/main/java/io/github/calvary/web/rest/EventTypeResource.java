package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.EventTypeRepository;
import io.github.calvary.service.EventTypeQueryService;
import io.github.calvary.service.EventTypeService;
import io.github.calvary.service.criteria.EventTypeCriteria;
import io.github.calvary.service.dto.EventTypeDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.EventType}.
 */
@RestController
@RequestMapping("/api")
public class EventTypeResource {

    private final Logger log = LoggerFactory.getLogger(EventTypeResource.class);

    private static final String ENTITY_NAME = "eventType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventTypeService eventTypeService;

    private final EventTypeRepository eventTypeRepository;

    private final EventTypeQueryService eventTypeQueryService;

    public EventTypeResource(
        EventTypeService eventTypeService,
        EventTypeRepository eventTypeRepository,
        EventTypeQueryService eventTypeQueryService
    ) {
        this.eventTypeService = eventTypeService;
        this.eventTypeRepository = eventTypeRepository;
        this.eventTypeQueryService = eventTypeQueryService;
    }

    /**
     * {@code POST  /event-types} : Create a new eventType.
     *
     * @param eventTypeDTO the eventTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eventTypeDTO, or with status {@code 400 (Bad Request)} if the eventType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/event-types")
    public ResponseEntity<EventTypeDTO> createEventType(@Valid @RequestBody EventTypeDTO eventTypeDTO) throws URISyntaxException {
        log.debug("REST request to save EventType : {}", eventTypeDTO);
        if (eventTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new eventType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EventTypeDTO result = eventTypeService.save(eventTypeDTO);
        return ResponseEntity
            .created(new URI("/api/event-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /event-types/:id} : Updates an existing eventType.
     *
     * @param id the id of the eventTypeDTO to save.
     * @param eventTypeDTO the eventTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventTypeDTO,
     * or with status {@code 400 (Bad Request)} if the eventTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eventTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/event-types/{id}")
    public ResponseEntity<EventTypeDTO> updateEventType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventTypeDTO eventTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update EventType : {}, {}", id, eventTypeDTO);
        if (eventTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EventTypeDTO result = eventTypeService.update(eventTypeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /event-types/:id} : Partial updates given fields of an existing eventType, field will ignore if it is null
     *
     * @param id the id of the eventTypeDTO to save.
     * @param eventTypeDTO the eventTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eventTypeDTO,
     * or with status {@code 400 (Bad Request)} if the eventTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eventTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eventTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/event-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventTypeDTO> partialUpdateEventType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventTypeDTO eventTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update EventType partially : {}, {}", id, eventTypeDTO);
        if (eventTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventTypeDTO> result = eventTypeService.partialUpdate(eventTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eventTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /event-types} : get all the eventTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eventTypes in body.
     */
    @GetMapping("/event-types")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes(
        EventTypeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get EventTypes by criteria: {}", criteria);

        Page<EventTypeDTO> page = eventTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /event-types/count} : count all the eventTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/event-types/count")
    public ResponseEntity<Long> countEventTypes(EventTypeCriteria criteria) {
        log.debug("REST request to count EventTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(eventTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /event-types/:id} : get the "id" eventType.
     *
     * @param id the id of the eventTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eventTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/event-types/{id}")
    public ResponseEntity<EventTypeDTO> getEventType(@PathVariable Long id) {
        log.debug("REST request to get EventType : {}", id);
        Optional<EventTypeDTO> eventTypeDTO = eventTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventTypeDTO);
    }

    /**
     * {@code DELETE  /event-types/:id} : delete the "id" eventType.
     *
     * @param id the id of the eventTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/event-types/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        log.debug("REST request to delete EventType : {}", id);
        eventTypeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/event-types?query=:query} : search for the eventType corresponding
     * to the query.
     *
     * @param query the query of the eventType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/event-types")
    public ResponseEntity<List<EventTypeDTO>> searchEventTypes(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of EventTypes for query {}", query);
        try {
            Page<EventTypeDTO> page = eventTypeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
