package io.github.calvary.web.rest;

import static io.github.calvary.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.TransactionEntry;
import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import io.github.calvary.repository.TransactionEntryRepository;
import io.github.calvary.repository.search.TransactionEntrySearchRepository;
import io.github.calvary.service.TransactionEntryService;
import io.github.calvary.service.criteria.TransactionEntryCriteria;
import io.github.calvary.service.dto.TransactionEntryDTO;
import io.github.calvary.service.mapper.TransactionEntryMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionEntryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransactionEntryResourceIT {

    private static final BigDecimal DEFAULT_ENTRY_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_ENTRY_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_ENTRY_AMOUNT = new BigDecimal(1 - 1);

    private static final TransactionEntryTypes DEFAULT_TRANSACTION_ENTRY_TYPE = TransactionEntryTypes.DEBIT;
    private static final TransactionEntryTypes UPDATED_TRANSACTION_ENTRY_TYPE = TransactionEntryTypes.CREDIT;

    private static final String ENTITY_API_URL = "/api/transaction-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-entries";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionEntryRepository transactionEntryRepository;

    @Mock
    private TransactionEntryRepository transactionEntryRepositoryMock;

    @Autowired
    private TransactionEntryMapper transactionEntryMapper;

    @Mock
    private TransactionEntryService transactionEntryServiceMock;

    @Autowired
    private TransactionEntrySearchRepository transactionEntrySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionEntryMockMvc;

    private TransactionEntry transactionEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionEntry createEntity(EntityManager em) {
        TransactionEntry transactionEntry = new TransactionEntry()
            .entryAmount(DEFAULT_ENTRY_AMOUNT)
            .transactionEntryType(DEFAULT_TRANSACTION_ENTRY_TYPE);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        transactionEntry.setTransactionAccount(transactionAccount);
        return transactionEntry;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionEntry createUpdatedEntity(EntityManager em) {
        TransactionEntry transactionEntry = new TransactionEntry()
            .entryAmount(UPDATED_ENTRY_AMOUNT)
            .transactionEntryType(UPDATED_TRANSACTION_ENTRY_TYPE);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createUpdatedEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        transactionEntry.setTransactionAccount(transactionAccount);
        return transactionEntry;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionEntrySearchRepository.deleteAll();
        assertThat(transactionEntrySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionEntry = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionEntry() throws Exception {
        int databaseSizeBeforeCreate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);
        restTransactionEntryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionEntry testTransactionEntry = transactionEntryList.get(transactionEntryList.size() - 1);
        assertThat(testTransactionEntry.getEntryAmount()).isEqualByComparingTo(DEFAULT_ENTRY_AMOUNT);
        assertThat(testTransactionEntry.getTransactionEntryType()).isEqualTo(DEFAULT_TRANSACTION_ENTRY_TYPE);
    }

    @Test
    @Transactional
    void createTransactionEntryWithExistingId() throws Exception {
        // Create the TransactionEntry with an existing ID
        transactionEntry.setId(1L);
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        int databaseSizeBeforeCreate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionEntryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionEntryTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        // set the field null
        transactionEntry.setTransactionEntryType(null);

        // Create the TransactionEntry, which fails.
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        restTransactionEntryMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionEntries() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList
        restTransactionEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].entryAmount").value(hasItem(sameNumber(DEFAULT_ENTRY_AMOUNT))))
            .andExpect(jsonPath("$.[*].transactionEntryType").value(hasItem(DEFAULT_TRANSACTION_ENTRY_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionEntriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(transactionEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionEntryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionEntriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transactionEntryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionEntryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transactionEntryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransactionEntry() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get the transactionEntry
        restTransactionEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionEntry.getId().intValue()))
            .andExpect(jsonPath("$.entryAmount").value(sameNumber(DEFAULT_ENTRY_AMOUNT)))
            .andExpect(jsonPath("$.transactionEntryType").value(DEFAULT_TRANSACTION_ENTRY_TYPE.toString()));
    }

    @Test
    @Transactional
    void getTransactionEntriesByIdFiltering() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        Long id = transactionEntry.getId();

        defaultTransactionEntryShouldBeFound("id.equals=" + id);
        defaultTransactionEntryShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionEntryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionEntryShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionEntryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionEntryShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount equals to DEFAULT_ENTRY_AMOUNT
        defaultTransactionEntryShouldBeFound("entryAmount.equals=" + DEFAULT_ENTRY_AMOUNT);

        // Get all the transactionEntryList where entryAmount equals to UPDATED_ENTRY_AMOUNT
        defaultTransactionEntryShouldNotBeFound("entryAmount.equals=" + UPDATED_ENTRY_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsInShouldWork() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount in DEFAULT_ENTRY_AMOUNT or UPDATED_ENTRY_AMOUNT
        defaultTransactionEntryShouldBeFound("entryAmount.in=" + DEFAULT_ENTRY_AMOUNT + "," + UPDATED_ENTRY_AMOUNT);

        // Get all the transactionEntryList where entryAmount equals to UPDATED_ENTRY_AMOUNT
        defaultTransactionEntryShouldNotBeFound("entryAmount.in=" + UPDATED_ENTRY_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount is not null
        defaultTransactionEntryShouldBeFound("entryAmount.specified=true");

        // Get all the transactionEntryList where entryAmount is null
        defaultTransactionEntryShouldNotBeFound("entryAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount is greater than or equal to DEFAULT_ENTRY_AMOUNT
        defaultTransactionEntryShouldBeFound("entryAmount.greaterThanOrEqual=" + DEFAULT_ENTRY_AMOUNT);

        // Get all the transactionEntryList where entryAmount is greater than or equal to UPDATED_ENTRY_AMOUNT
        defaultTransactionEntryShouldNotBeFound("entryAmount.greaterThanOrEqual=" + UPDATED_ENTRY_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount is less than or equal to DEFAULT_ENTRY_AMOUNT
        defaultTransactionEntryShouldBeFound("entryAmount.lessThanOrEqual=" + DEFAULT_ENTRY_AMOUNT);

        // Get all the transactionEntryList where entryAmount is less than or equal to SMALLER_ENTRY_AMOUNT
        defaultTransactionEntryShouldNotBeFound("entryAmount.lessThanOrEqual=" + SMALLER_ENTRY_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount is less than DEFAULT_ENTRY_AMOUNT
        defaultTransactionEntryShouldNotBeFound("entryAmount.lessThan=" + DEFAULT_ENTRY_AMOUNT);

        // Get all the transactionEntryList where entryAmount is less than UPDATED_ENTRY_AMOUNT
        defaultTransactionEntryShouldBeFound("entryAmount.lessThan=" + UPDATED_ENTRY_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByEntryAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where entryAmount is greater than DEFAULT_ENTRY_AMOUNT
        defaultTransactionEntryShouldNotBeFound("entryAmount.greaterThan=" + DEFAULT_ENTRY_AMOUNT);

        // Get all the transactionEntryList where entryAmount is greater than SMALLER_ENTRY_AMOUNT
        defaultTransactionEntryShouldBeFound("entryAmount.greaterThan=" + SMALLER_ENTRY_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByTransactionEntryTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where transactionEntryType equals to DEFAULT_TRANSACTION_ENTRY_TYPE
        defaultTransactionEntryShouldBeFound("transactionEntryType.equals=" + DEFAULT_TRANSACTION_ENTRY_TYPE);

        // Get all the transactionEntryList where transactionEntryType equals to UPDATED_TRANSACTION_ENTRY_TYPE
        defaultTransactionEntryShouldNotBeFound("transactionEntryType.equals=" + UPDATED_TRANSACTION_ENTRY_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByTransactionEntryTypeIsInShouldWork() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where transactionEntryType in DEFAULT_TRANSACTION_ENTRY_TYPE or UPDATED_TRANSACTION_ENTRY_TYPE
        defaultTransactionEntryShouldBeFound(
            "transactionEntryType.in=" + DEFAULT_TRANSACTION_ENTRY_TYPE + "," + UPDATED_TRANSACTION_ENTRY_TYPE
        );

        // Get all the transactionEntryList where transactionEntryType equals to UPDATED_TRANSACTION_ENTRY_TYPE
        defaultTransactionEntryShouldNotBeFound("transactionEntryType.in=" + UPDATED_TRANSACTION_ENTRY_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByTransactionEntryTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        // Get all the transactionEntryList where transactionEntryType is not null
        defaultTransactionEntryShouldBeFound("transactionEntryType.specified=true");

        // Get all the transactionEntryList where transactionEntryType is null
        defaultTransactionEntryShouldNotBeFound("transactionEntryType.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionEntriesByTransactionAccountIsEqualToSomething() throws Exception {
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionEntryRepository.saveAndFlush(transactionEntry);
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        em.persist(transactionAccount);
        em.flush();
        transactionEntry.setTransactionAccount(transactionAccount);
        transactionEntryRepository.saveAndFlush(transactionEntry);
        Long transactionAccountId = transactionAccount.getId();
        // Get all the transactionEntryList where transactionAccount equals to transactionAccountId
        defaultTransactionEntryShouldBeFound("transactionAccountId.equals=" + transactionAccountId);

        // Get all the transactionEntryList where transactionAccount equals to (transactionAccountId + 1)
        defaultTransactionEntryShouldNotBeFound("transactionAccountId.equals=" + (transactionAccountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionEntryShouldBeFound(String filter) throws Exception {
        restTransactionEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].entryAmount").value(hasItem(sameNumber(DEFAULT_ENTRY_AMOUNT))))
            .andExpect(jsonPath("$.[*].transactionEntryType").value(hasItem(DEFAULT_TRANSACTION_ENTRY_TYPE.toString())));

        // Check, that the count call also returns 1
        restTransactionEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionEntryShouldNotBeFound(String filter) throws Exception {
        restTransactionEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionEntryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionEntry() throws Exception {
        // Get the transactionEntry
        restTransactionEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionEntry() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        transactionEntrySearchRepository.save(transactionEntry);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());

        // Update the transactionEntry
        TransactionEntry updatedTransactionEntry = transactionEntryRepository.findById(transactionEntry.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionEntry are not directly saved in db
        em.detach(updatedTransactionEntry);
        updatedTransactionEntry.entryAmount(UPDATED_ENTRY_AMOUNT).transactionEntryType(UPDATED_TRANSACTION_ENTRY_TYPE);
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(updatedTransactionEntry);

        restTransactionEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        TransactionEntry testTransactionEntry = transactionEntryList.get(transactionEntryList.size() - 1);
        assertThat(testTransactionEntry.getEntryAmount()).isEqualByComparingTo(UPDATED_ENTRY_AMOUNT);
        assertThat(testTransactionEntry.getTransactionEntryType()).isEqualTo(UPDATED_TRANSACTION_ENTRY_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionEntry> transactionEntrySearchList = IterableUtils.toList(transactionEntrySearchRepository.findAll());
                TransactionEntry testTransactionEntrySearch = transactionEntrySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionEntrySearch.getEntryAmount()).isEqualByComparingTo(UPDATED_ENTRY_AMOUNT);
                assertThat(testTransactionEntrySearch.getTransactionEntryType()).isEqualTo(UPDATED_TRANSACTION_ENTRY_TYPE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        transactionEntry.setId(count.incrementAndGet());

        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionEntryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        transactionEntry.setId(count.incrementAndGet());

        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        transactionEntry.setId(count.incrementAndGet());

        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEntryMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionEntryWithPatch() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();

        // Update the transactionEntry using partial update
        TransactionEntry partialUpdatedTransactionEntry = new TransactionEntry();
        partialUpdatedTransactionEntry.setId(transactionEntry.getId());

        partialUpdatedTransactionEntry.transactionEntryType(UPDATED_TRANSACTION_ENTRY_TYPE);

        restTransactionEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionEntry))
            )
            .andExpect(status().isOk());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        TransactionEntry testTransactionEntry = transactionEntryList.get(transactionEntryList.size() - 1);
        assertThat(testTransactionEntry.getEntryAmount()).isEqualByComparingTo(DEFAULT_ENTRY_AMOUNT);
        assertThat(testTransactionEntry.getTransactionEntryType()).isEqualTo(UPDATED_TRANSACTION_ENTRY_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTransactionEntryWithPatch() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);

        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();

        // Update the transactionEntry using partial update
        TransactionEntry partialUpdatedTransactionEntry = new TransactionEntry();
        partialUpdatedTransactionEntry.setId(transactionEntry.getId());

        partialUpdatedTransactionEntry.entryAmount(UPDATED_ENTRY_AMOUNT).transactionEntryType(UPDATED_TRANSACTION_ENTRY_TYPE);

        restTransactionEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionEntry))
            )
            .andExpect(status().isOk());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        TransactionEntry testTransactionEntry = transactionEntryList.get(transactionEntryList.size() - 1);
        assertThat(testTransactionEntry.getEntryAmount()).isEqualByComparingTo(UPDATED_ENTRY_AMOUNT);
        assertThat(testTransactionEntry.getTransactionEntryType()).isEqualTo(UPDATED_TRANSACTION_ENTRY_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        transactionEntry.setId(count.incrementAndGet());

        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionEntryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        transactionEntry.setId(count.incrementAndGet());

        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionEntry() throws Exception {
        int databaseSizeBeforeUpdate = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        transactionEntry.setId(count.incrementAndGet());

        // Create the TransactionEntry
        TransactionEntryDTO transactionEntryDTO = transactionEntryMapper.toDto(transactionEntry);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionEntryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionEntryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionEntry in the database
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionEntry() throws Exception {
        // Initialize the database
        transactionEntryRepository.saveAndFlush(transactionEntry);
        transactionEntryRepository.save(transactionEntry);
        transactionEntrySearchRepository.save(transactionEntry);

        int databaseSizeBeforeDelete = transactionEntryRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionEntry
        restTransactionEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionEntry> transactionEntryList = transactionEntryRepository.findAll();
        assertThat(transactionEntryList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionEntrySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionEntry() throws Exception {
        // Initialize the database
        transactionEntry = transactionEntryRepository.saveAndFlush(transactionEntry);
        transactionEntrySearchRepository.save(transactionEntry);

        // Search the transactionEntry
        restTransactionEntryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].entryAmount").value(hasItem(sameNumber(DEFAULT_ENTRY_AMOUNT))))
            .andExpect(jsonPath("$.[*].transactionEntryType").value(hasItem(DEFAULT_TRANSACTION_ENTRY_TYPE.toString())));
    }
}
