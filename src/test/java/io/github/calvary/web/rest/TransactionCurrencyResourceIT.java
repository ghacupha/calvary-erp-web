package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionCurrency;
import io.github.calvary.repository.TransactionCurrencyRepository;
import io.github.calvary.repository.search.TransactionCurrencySearchRepository;
import io.github.calvary.service.dto.TransactionCurrencyDTO;
import io.github.calvary.service.mapper.TransactionCurrencyMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link TransactionCurrencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionCurrencyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transaction-currencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-currencies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionCurrencyRepository transactionCurrencyRepository;

    @Autowired
    private TransactionCurrencyMapper transactionCurrencyMapper;

    @Autowired
    private TransactionCurrencySearchRepository transactionCurrencySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionCurrencyMockMvc;

    private TransactionCurrency transactionCurrency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionCurrency createEntity(EntityManager em) {
        TransactionCurrency transactionCurrency = new TransactionCurrency().name(DEFAULT_NAME).code(DEFAULT_CODE);
        return transactionCurrency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionCurrency createUpdatedEntity(EntityManager em) {
        TransactionCurrency transactionCurrency = new TransactionCurrency().name(UPDATED_NAME).code(UPDATED_CODE);
        return transactionCurrency;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionCurrencySearchRepository.deleteAll();
        assertThat(transactionCurrencySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionCurrency = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionCurrency() throws Exception {
        int databaseSizeBeforeCreate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);
        restTransactionCurrencyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionCurrency testTransactionCurrency = transactionCurrencyList.get(transactionCurrencyList.size() - 1);
        assertThat(testTransactionCurrency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTransactionCurrency.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void createTransactionCurrencyWithExistingId() throws Exception {
        // Create the TransactionCurrency with an existing ID
        transactionCurrency.setId(1L);
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        int databaseSizeBeforeCreate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionCurrencyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        // set the field null
        transactionCurrency.setName(null);

        // Create the TransactionCurrency, which fails.
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        restTransactionCurrencyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        // set the field null
        transactionCurrency.setCode(null);

        // Create the TransactionCurrency, which fails.
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        restTransactionCurrencyMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionCurrencies() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionCurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getTransactionCurrency() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get the transactionCurrency
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionCurrency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionCurrency.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getTransactionCurrenciesByIdFiltering() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        Long id = transactionCurrency.getId();

        defaultTransactionCurrencyShouldBeFound("id.equals=" + id);
        defaultTransactionCurrencyShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionCurrencyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionCurrencyShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionCurrencyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionCurrencyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where name equals to DEFAULT_NAME
        defaultTransactionCurrencyShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the transactionCurrencyList where name equals to UPDATED_NAME
        defaultTransactionCurrencyShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTransactionCurrencyShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the transactionCurrencyList where name equals to UPDATED_NAME
        defaultTransactionCurrencyShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where name is not null
        defaultTransactionCurrencyShouldBeFound("name.specified=true");

        // Get all the transactionCurrencyList where name is null
        defaultTransactionCurrencyShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByNameContainsSomething() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where name contains DEFAULT_NAME
        defaultTransactionCurrencyShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the transactionCurrencyList where name contains UPDATED_NAME
        defaultTransactionCurrencyShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where name does not contain DEFAULT_NAME
        defaultTransactionCurrencyShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the transactionCurrencyList where name does not contain UPDATED_NAME
        defaultTransactionCurrencyShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where code equals to DEFAULT_CODE
        defaultTransactionCurrencyShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the transactionCurrencyList where code equals to UPDATED_CODE
        defaultTransactionCurrencyShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where code in DEFAULT_CODE or UPDATED_CODE
        defaultTransactionCurrencyShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the transactionCurrencyList where code equals to UPDATED_CODE
        defaultTransactionCurrencyShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where code is not null
        defaultTransactionCurrencyShouldBeFound("code.specified=true");

        // Get all the transactionCurrencyList where code is null
        defaultTransactionCurrencyShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByCodeContainsSomething() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where code contains DEFAULT_CODE
        defaultTransactionCurrencyShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the transactionCurrencyList where code contains UPDATED_CODE
        defaultTransactionCurrencyShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllTransactionCurrenciesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        // Get all the transactionCurrencyList where code does not contain DEFAULT_CODE
        defaultTransactionCurrencyShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the transactionCurrencyList where code does not contain UPDATED_CODE
        defaultTransactionCurrencyShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionCurrencyShouldBeFound(String filter) throws Exception {
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionCurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));

        // Check, that the count call also returns 1
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionCurrencyShouldNotBeFound(String filter) throws Exception {
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionCurrency() throws Exception {
        // Get the transactionCurrency
        restTransactionCurrencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionCurrency() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        transactionCurrencySearchRepository.save(transactionCurrency);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());

        // Update the transactionCurrency
        TransactionCurrency updatedTransactionCurrency = transactionCurrencyRepository.findById(transactionCurrency.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionCurrency are not directly saved in db
        em.detach(updatedTransactionCurrency);
        updatedTransactionCurrency.name(UPDATED_NAME).code(UPDATED_CODE);
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(updatedTransactionCurrency);

        restTransactionCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionCurrencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        TransactionCurrency testTransactionCurrency = transactionCurrencyList.get(transactionCurrencyList.size() - 1);
        assertThat(testTransactionCurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransactionCurrency.getCode()).isEqualTo(UPDATED_CODE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionCurrency> transactionCurrencySearchList = IterableUtils.toList(
                    transactionCurrencySearchRepository.findAll()
                );
                TransactionCurrency testTransactionCurrencySearch = transactionCurrencySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionCurrencySearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testTransactionCurrencySearch.getCode()).isEqualTo(UPDATED_CODE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionCurrency() throws Exception {
        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        transactionCurrency.setId(count.incrementAndGet());

        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionCurrencyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionCurrency() throws Exception {
        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        transactionCurrency.setId(count.incrementAndGet());

        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionCurrency() throws Exception {
        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        transactionCurrency.setId(count.incrementAndGet());

        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionCurrencyMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionCurrencyWithPatch() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();

        // Update the transactionCurrency using partial update
        TransactionCurrency partialUpdatedTransactionCurrency = new TransactionCurrency();
        partialUpdatedTransactionCurrency.setId(transactionCurrency.getId());

        restTransactionCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionCurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionCurrency))
            )
            .andExpect(status().isOk());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        TransactionCurrency testTransactionCurrency = transactionCurrencyList.get(transactionCurrencyList.size() - 1);
        assertThat(testTransactionCurrency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTransactionCurrency.getCode()).isEqualTo(DEFAULT_CODE);
    }

    @Test
    @Transactional
    void fullUpdateTransactionCurrencyWithPatch() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);

        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();

        // Update the transactionCurrency using partial update
        TransactionCurrency partialUpdatedTransactionCurrency = new TransactionCurrency();
        partialUpdatedTransactionCurrency.setId(transactionCurrency.getId());

        partialUpdatedTransactionCurrency.name(UPDATED_NAME).code(UPDATED_CODE);

        restTransactionCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionCurrency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionCurrency))
            )
            .andExpect(status().isOk());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        TransactionCurrency testTransactionCurrency = transactionCurrencyList.get(transactionCurrencyList.size() - 1);
        assertThat(testTransactionCurrency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransactionCurrency.getCode()).isEqualTo(UPDATED_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionCurrency() throws Exception {
        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        transactionCurrency.setId(count.incrementAndGet());

        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionCurrencyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionCurrency() throws Exception {
        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        transactionCurrency.setId(count.incrementAndGet());

        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionCurrency() throws Exception {
        int databaseSizeBeforeUpdate = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        transactionCurrency.setId(count.incrementAndGet());

        // Create the TransactionCurrency
        TransactionCurrencyDTO transactionCurrencyDTO = transactionCurrencyMapper.toDto(transactionCurrency);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionCurrencyMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionCurrencyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionCurrency in the database
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionCurrency() throws Exception {
        // Initialize the database
        transactionCurrencyRepository.saveAndFlush(transactionCurrency);
        transactionCurrencyRepository.save(transactionCurrency);
        transactionCurrencySearchRepository.save(transactionCurrency);

        int databaseSizeBeforeDelete = transactionCurrencyRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionCurrency
        restTransactionCurrencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionCurrency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionCurrency> transactionCurrencyList = transactionCurrencyRepository.findAll();
        assertThat(transactionCurrencyList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionCurrencySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionCurrency() throws Exception {
        // Initialize the database
        transactionCurrency = transactionCurrencyRepository.saveAndFlush(transactionCurrency);
        transactionCurrencySearchRepository.save(transactionCurrency);

        // Search the transactionCurrency
        restTransactionCurrencyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionCurrency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionCurrency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }
}
