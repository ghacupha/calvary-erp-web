package io.github.calvary.web.rest;

import io.github.calvary.repository.AccountTransactionRepository;
import io.github.calvary.service.AccountTransactionService;
import io.github.calvary.erp.AccountTransactionPostingProcessor;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

import java.net.URISyntaxException;
import java.util.Objects;

@RestController
@RequestMapping("/api/post")
public class AccountTransactionPostingResource {

    private final Logger log = LoggerFactory.getLogger(AccountTransactionResource.class);

    private static final String ENTITY_NAME = "accountTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountTransactionRepository accountTransactionRepository;
    private final AccountTransactionService accountTransactionService;
    private final AccountTransactionPostingProcessor postingProcessor;

    public AccountTransactionPostingResource(AccountTransactionRepository accountTransactionRepository, AccountTransactionService accountTransactionService, AccountTransactionPostingProcessor postingProcessor) {
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountTransactionService = accountTransactionService;
        this.postingProcessor = postingProcessor;
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
    public ResponseEntity<AccountTransactionDTO> postTransaction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccountTransactionDTO accountTransactionDTO
    ) {
        log.debug("REST request to post AccountTransaction : {}, {}", id, accountTransactionDTO);
        if (accountTransactionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountTransactionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountTransactionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountTransactionDTO result = postingProcessor.post(accountTransactionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, accountTransactionDTO.getId().toString()))
            .body(result);
    }
}
