package io.github.calvary.web.rest;

import static org.springframework.data.elasticsearch.client.elc.QueryBuilders.*;

import io.github.calvary.repository.AccountTransactionRepository;
import io.github.calvary.service.AccountTransactionQueryService;
import io.github.calvary.service.AccountTransactionService;
import io.github.calvary.service.criteria.AccountTransactionCriteria;
import io.github.calvary.service.dto.AccountTransactionDTO;
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
 * REST controller for managing {@link io.github.calvary.domain.AccountTransaction}.
 */
@RestController
@RequestMapping("/api")
public class AccountTransactionResource {

    private final Logger log = LoggerFactory.getLogger(AccountTransactionResource.class);

    private static final String ENTITY_NAME = "accountTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountTransactionService accountTransactionService;

    private final AccountTransactionRepository accountTransactionRepository;

    private final AccountTransactionQueryService accountTransactionQueryService;

    public AccountTransactionResource(
        AccountTransactionService accountTransactionService,
        AccountTransactionRepository accountTransactionRepository,
        AccountTransactionQueryService accountTransactionQueryService
    ) {
        this.accountTransactionService = accountTransactionService;
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountTransactionQueryService = accountTransactionQueryService;
    }

    /**
     * {@code POST  /account-transactions} : Create a new accountTransaction.
     *
     * @param accountTransactionDTO the accountTransactionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountTransactionDTO, or with status {@code 400 (Bad Request)} if the accountTransaction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/account-transactions")
    public ResponseEntity<AccountTransactionDTO> createAccountTransaction(@Valid @RequestBody AccountTransactionDTO accountTransactionDTO)
        throws URISyntaxException {
        log.debug("REST request to save AccountTransaction : {}", accountTransactionDTO);
        if (accountTransactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new accountTransaction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AccountTransactionDTO result = accountTransactionService.save(accountTransactionDTO);
        return ResponseEntity
            .created(new URI("/api/account-transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /account-transactions/:id} : Updates an existing accountTransaction.
     *
     * @param id the id of the accountTransactionDTO to save.
     * @param accountTransactionDTO the accountTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the accountTransactionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/account-transactions/{id}")
    public ResponseEntity<AccountTransactionDTO> updateAccountTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccountTransactionDTO accountTransactionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AccountTransaction : {}, {}", id, accountTransactionDTO);
        if (accountTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountTransactionDTO result = accountTransactionService.update(accountTransactionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountTransactionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /account-transactions/:id} : Partial updates given fields of an existing accountTransaction, field will ignore if it is null
     *
     * @param id the id of the accountTransactionDTO to save.
     * @param accountTransactionDTO the accountTransactionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountTransactionDTO,
     * or with status {@code 400 (Bad Request)} if the accountTransactionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the accountTransactionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountTransactionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/account-transactions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccountTransactionDTO> partialUpdateAccountTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AccountTransactionDTO accountTransactionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AccountTransaction partially : {}, {}", id, accountTransactionDTO);
        if (accountTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountTransactionDTO> result = accountTransactionService.partialUpdate(accountTransactionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountTransactionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /account-transactions} : get all the accountTransactions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountTransactions in body.
     */
    @GetMapping("/account-transactions")
    public ResponseEntity<List<AccountTransactionDTO>> getAllAccountTransactions(
        AccountTransactionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get AccountTransactions by criteria: {}", criteria);

        Page<AccountTransactionDTO> page = accountTransactionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /account-transactions/count} : count all the accountTransactions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/account-transactions/count")
    public ResponseEntity<Long> countAccountTransactions(AccountTransactionCriteria criteria) {
        log.debug("REST request to count AccountTransactions by criteria: {}", criteria);
        return ResponseEntity.ok().body(accountTransactionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /account-transactions/:id} : get the "id" accountTransaction.
     *
     * @param id the id of the accountTransactionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountTransactionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/account-transactions/{id}")
    public ResponseEntity<AccountTransactionDTO> getAccountTransaction(@PathVariable Long id) {
        log.debug("REST request to get AccountTransaction : {}", id);
        Optional<AccountTransactionDTO> accountTransactionDTO = accountTransactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accountTransactionDTO);
    }

    /**
     * {@code DELETE  /account-transactions/:id} : delete the "id" accountTransaction.
     *
     * @param id the id of the accountTransactionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/account-transactions/{id}")
    public ResponseEntity<Void> deleteAccountTransaction(@PathVariable Long id) {
        log.debug("REST request to delete AccountTransaction : {}", id);
        accountTransactionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/account-transactions?query=:query} : search for the accountTransaction corresponding
     * to the query.
     *
     * @param query the query of the accountTransaction search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/account-transactions")
    public ResponseEntity<List<AccountTransactionDTO>> searchAccountTransactions(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of AccountTransactions for query {}", query);
        try {
            Page<AccountTransactionDTO> page = accountTransactionService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
