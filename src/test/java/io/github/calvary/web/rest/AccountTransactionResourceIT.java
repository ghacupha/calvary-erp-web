package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.AccountTransaction;
import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.repository.AccountTransactionRepository;
import io.github.calvary.repository.search.AccountTransactionSearchRepository;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.service.mapper.AccountTransactionMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountTransactionResourceIT {

    private static final LocalDate DEFAULT_TRANSACTION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_TRANSACTION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_TRANSACTION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/account-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/account-transactions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private AccountTransactionMapper accountTransactionMapper;

    @Autowired
    private AccountTransactionSearchRepository accountTransactionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountTransactionMockMvc;

    private AccountTransaction accountTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountTransaction createEntity(EntityManager em) {
        AccountTransaction accountTransaction = new AccountTransaction()
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .description(DEFAULT_DESCRIPTION)
            .referenceNumber(DEFAULT_REFERENCE_NUMBER);
        return accountTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountTransaction createUpdatedEntity(EntityManager em) {
        AccountTransaction accountTransaction = new AccountTransaction()
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .description(UPDATED_DESCRIPTION)
            .referenceNumber(UPDATED_REFERENCE_NUMBER);
        return accountTransaction;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        accountTransactionSearchRepository.deleteAll();
        assertThat(accountTransactionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        accountTransaction = createEntity(em);
    }

    @Test
    @Transactional
    void createAccountTransaction() throws Exception {
        int databaseSizeBeforeCreate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);
        restAccountTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        AccountTransaction testAccountTransaction = accountTransactionList.get(accountTransactionList.size() - 1);
        assertThat(testAccountTransaction.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testAccountTransaction.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAccountTransaction.getReferenceNumber()).isEqualTo(DEFAULT_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void createAccountTransactionWithExistingId() throws Exception {
        // Create the AccountTransaction with an existing ID
        accountTransaction.setId(1L);
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        int databaseSizeBeforeCreate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        // set the field null
        accountTransaction.setTransactionDate(null);

        // Create the AccountTransaction, which fails.
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        restAccountTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAccountTransactions() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList
        restAccountTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)));
    }

    @Test
    @Transactional
    void getAccountTransaction() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get the accountTransaction
        restAccountTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, accountTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountTransaction.getId().intValue()))
            .andExpect(jsonPath("$.transactionDate").value(DEFAULT_TRANSACTION_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.referenceNumber").value(DEFAULT_REFERENCE_NUMBER));
    }

    @Test
    @Transactional
    void getAccountTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        Long id = accountTransaction.getId();

        defaultAccountTransactionShouldBeFound("id.equals=" + id);
        defaultAccountTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultAccountTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAccountTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultAccountTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAccountTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate equals to DEFAULT_TRANSACTION_DATE
        defaultAccountTransactionShouldBeFound("transactionDate.equals=" + DEFAULT_TRANSACTION_DATE);

        // Get all the accountTransactionList where transactionDate equals to UPDATED_TRANSACTION_DATE
        defaultAccountTransactionShouldNotBeFound("transactionDate.equals=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsInShouldWork() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate in DEFAULT_TRANSACTION_DATE or UPDATED_TRANSACTION_DATE
        defaultAccountTransactionShouldBeFound("transactionDate.in=" + DEFAULT_TRANSACTION_DATE + "," + UPDATED_TRANSACTION_DATE);

        // Get all the accountTransactionList where transactionDate equals to UPDATED_TRANSACTION_DATE
        defaultAccountTransactionShouldNotBeFound("transactionDate.in=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate is not null
        defaultAccountTransactionShouldBeFound("transactionDate.specified=true");

        // Get all the accountTransactionList where transactionDate is null
        defaultAccountTransactionShouldNotBeFound("transactionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate is greater than or equal to DEFAULT_TRANSACTION_DATE
        defaultAccountTransactionShouldBeFound("transactionDate.greaterThanOrEqual=" + DEFAULT_TRANSACTION_DATE);

        // Get all the accountTransactionList where transactionDate is greater than or equal to UPDATED_TRANSACTION_DATE
        defaultAccountTransactionShouldNotBeFound("transactionDate.greaterThanOrEqual=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate is less than or equal to DEFAULT_TRANSACTION_DATE
        defaultAccountTransactionShouldBeFound("transactionDate.lessThanOrEqual=" + DEFAULT_TRANSACTION_DATE);

        // Get all the accountTransactionList where transactionDate is less than or equal to SMALLER_TRANSACTION_DATE
        defaultAccountTransactionShouldNotBeFound("transactionDate.lessThanOrEqual=" + SMALLER_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsLessThanSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate is less than DEFAULT_TRANSACTION_DATE
        defaultAccountTransactionShouldNotBeFound("transactionDate.lessThan=" + DEFAULT_TRANSACTION_DATE);

        // Get all the accountTransactionList where transactionDate is less than UPDATED_TRANSACTION_DATE
        defaultAccountTransactionShouldBeFound("transactionDate.lessThan=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where transactionDate is greater than DEFAULT_TRANSACTION_DATE
        defaultAccountTransactionShouldNotBeFound("transactionDate.greaterThan=" + DEFAULT_TRANSACTION_DATE);

        // Get all the accountTransactionList where transactionDate is greater than SMALLER_TRANSACTION_DATE
        defaultAccountTransactionShouldBeFound("transactionDate.greaterThan=" + SMALLER_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where description equals to DEFAULT_DESCRIPTION
        defaultAccountTransactionShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the accountTransactionList where description equals to UPDATED_DESCRIPTION
        defaultAccountTransactionShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultAccountTransactionShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the accountTransactionList where description equals to UPDATED_DESCRIPTION
        defaultAccountTransactionShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where description is not null
        defaultAccountTransactionShouldBeFound("description.specified=true");

        // Get all the accountTransactionList where description is null
        defaultAccountTransactionShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where description contains DEFAULT_DESCRIPTION
        defaultAccountTransactionShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the accountTransactionList where description contains UPDATED_DESCRIPTION
        defaultAccountTransactionShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where description does not contain DEFAULT_DESCRIPTION
        defaultAccountTransactionShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the accountTransactionList where description does not contain UPDATED_DESCRIPTION
        defaultAccountTransactionShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByReferenceNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where referenceNumber equals to DEFAULT_REFERENCE_NUMBER
        defaultAccountTransactionShouldBeFound("referenceNumber.equals=" + DEFAULT_REFERENCE_NUMBER);

        // Get all the accountTransactionList where referenceNumber equals to UPDATED_REFERENCE_NUMBER
        defaultAccountTransactionShouldNotBeFound("referenceNumber.equals=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByReferenceNumberIsInShouldWork() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where referenceNumber in DEFAULT_REFERENCE_NUMBER or UPDATED_REFERENCE_NUMBER
        defaultAccountTransactionShouldBeFound("referenceNumber.in=" + DEFAULT_REFERENCE_NUMBER + "," + UPDATED_REFERENCE_NUMBER);

        // Get all the accountTransactionList where referenceNumber equals to UPDATED_REFERENCE_NUMBER
        defaultAccountTransactionShouldNotBeFound("referenceNumber.in=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByReferenceNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where referenceNumber is not null
        defaultAccountTransactionShouldBeFound("referenceNumber.specified=true");

        // Get all the accountTransactionList where referenceNumber is null
        defaultAccountTransactionShouldNotBeFound("referenceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByReferenceNumberContainsSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where referenceNumber contains DEFAULT_REFERENCE_NUMBER
        defaultAccountTransactionShouldBeFound("referenceNumber.contains=" + DEFAULT_REFERENCE_NUMBER);

        // Get all the accountTransactionList where referenceNumber contains UPDATED_REFERENCE_NUMBER
        defaultAccountTransactionShouldNotBeFound("referenceNumber.contains=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByReferenceNumberNotContainsSomething() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        // Get all the accountTransactionList where referenceNumber does not contain DEFAULT_REFERENCE_NUMBER
        defaultAccountTransactionShouldNotBeFound("referenceNumber.doesNotContain=" + DEFAULT_REFERENCE_NUMBER);

        // Get all the accountTransactionList where referenceNumber does not contain UPDATED_REFERENCE_NUMBER
        defaultAccountTransactionShouldBeFound("referenceNumber.doesNotContain=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAccountTransactionsByTransactionEntryIsEqualToSomething() throws Exception {
        TransactionEntry transactionEntry;
        if (TestUtil.findAll(em, TransactionEntry.class).isEmpty()) {
            accountTransactionRepository.saveAndFlush(accountTransaction);
            transactionEntry = TransactionEntryResourceIT.createEntity(em);
        } else {
            transactionEntry = TestUtil.findAll(em, TransactionEntry.class).get(0);
        }
        em.persist(transactionEntry);
        em.flush();
        accountTransaction.addTransactionEntry(transactionEntry);
        accountTransactionRepository.saveAndFlush(accountTransaction);
        Long transactionEntryId = transactionEntry.getId();
        // Get all the accountTransactionList where transactionEntry equals to transactionEntryId
        defaultAccountTransactionShouldBeFound("transactionEntryId.equals=" + transactionEntryId);

        // Get all the accountTransactionList where transactionEntry equals to (transactionEntryId + 1)
        defaultAccountTransactionShouldNotBeFound("transactionEntryId.equals=" + (transactionEntryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAccountTransactionShouldBeFound(String filter) throws Exception {
        restAccountTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)));

        // Check, that the count call also returns 1
        restAccountTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAccountTransactionShouldNotBeFound(String filter) throws Exception {
        restAccountTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAccountTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAccountTransaction() throws Exception {
        // Get the accountTransaction
        restAccountTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccountTransaction() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        accountTransactionSearchRepository.save(accountTransaction);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());

        // Update the accountTransaction
        AccountTransaction updatedAccountTransaction = accountTransactionRepository.findById(accountTransaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAccountTransaction are not directly saved in db
        em.detach(updatedAccountTransaction);
        updatedAccountTransaction
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .description(UPDATED_DESCRIPTION)
            .referenceNumber(UPDATED_REFERENCE_NUMBER);
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(updatedAccountTransaction);

        restAccountTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        AccountTransaction testAccountTransaction = accountTransactionList.get(accountTransactionList.size() - 1);
        assertThat(testAccountTransaction.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testAccountTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAccountTransaction.getReferenceNumber()).isEqualTo(UPDATED_REFERENCE_NUMBER);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AccountTransaction> accountTransactionSearchList = IterableUtils.toList(accountTransactionSearchRepository.findAll());
                AccountTransaction testAccountTransactionSearch = accountTransactionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAccountTransactionSearch.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
                assertThat(testAccountTransactionSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testAccountTransactionSearch.getReferenceNumber()).isEqualTo(UPDATED_REFERENCE_NUMBER);
            });
    }

    @Test
    @Transactional
    void putNonExistingAccountTransaction() throws Exception {
        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        accountTransaction.setId(count.incrementAndGet());

        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountTransactionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountTransaction() throws Exception {
        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        accountTransaction.setId(count.incrementAndGet());

        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountTransaction() throws Exception {
        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        accountTransaction.setId(count.incrementAndGet());

        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountTransactionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAccountTransactionWithPatch() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();

        // Update the accountTransaction using partial update
        AccountTransaction partialUpdatedAccountTransaction = new AccountTransaction();
        partialUpdatedAccountTransaction.setId(accountTransaction.getId());

        partialUpdatedAccountTransaction.description(UPDATED_DESCRIPTION).referenceNumber(UPDATED_REFERENCE_NUMBER);

        restAccountTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountTransaction))
            )
            .andExpect(status().isOk());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        AccountTransaction testAccountTransaction = accountTransactionList.get(accountTransactionList.size() - 1);
        assertThat(testAccountTransaction.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testAccountTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAccountTransaction.getReferenceNumber()).isEqualTo(UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdateAccountTransactionWithPatch() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);

        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();

        // Update the accountTransaction using partial update
        AccountTransaction partialUpdatedAccountTransaction = new AccountTransaction();
        partialUpdatedAccountTransaction.setId(accountTransaction.getId());

        partialUpdatedAccountTransaction
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .description(UPDATED_DESCRIPTION)
            .referenceNumber(UPDATED_REFERENCE_NUMBER);

        restAccountTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountTransaction))
            )
            .andExpect(status().isOk());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        AccountTransaction testAccountTransaction = accountTransactionList.get(accountTransactionList.size() - 1);
        assertThat(testAccountTransaction.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testAccountTransaction.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAccountTransaction.getReferenceNumber()).isEqualTo(UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingAccountTransaction() throws Exception {
        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        accountTransaction.setId(count.incrementAndGet());

        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountTransactionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountTransaction() throws Exception {
        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        accountTransaction.setId(count.incrementAndGet());

        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountTransaction() throws Exception {
        int databaseSizeBeforeUpdate = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        accountTransaction.setId(count.incrementAndGet());

        // Create the AccountTransaction
        AccountTransactionDTO accountTransactionDTO = accountTransactionMapper.toDto(accountTransaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountTransactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountTransaction in the database
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAccountTransaction() throws Exception {
        // Initialize the database
        accountTransactionRepository.saveAndFlush(accountTransaction);
        accountTransactionRepository.save(accountTransaction);
        accountTransactionSearchRepository.save(accountTransaction);

        int databaseSizeBeforeDelete = accountTransactionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the accountTransaction
        restAccountTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountTransaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AccountTransaction> accountTransactionList = accountTransactionRepository.findAll();
        assertThat(accountTransactionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountTransactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAccountTransaction() throws Exception {
        // Initialize the database
        accountTransaction = accountTransactionRepository.saveAndFlush(accountTransaction);
        accountTransactionSearchRepository.save(accountTransaction);

        // Search the accountTransaction
        restAccountTransactionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + accountTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)));
    }
}
