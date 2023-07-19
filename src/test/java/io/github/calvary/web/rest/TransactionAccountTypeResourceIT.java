package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.TransactionAccountType;
import io.github.calvary.repository.TransactionAccountTypeRepository;
import io.github.calvary.repository.search.TransactionAccountTypeSearchRepository;
import io.github.calvary.service.dto.TransactionAccountTypeDTO;
import io.github.calvary.service.mapper.TransactionAccountTypeMapper;
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
 * Integration tests for the {@link TransactionAccountTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionAccountTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transaction-account-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-account-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionAccountTypeRepository transactionAccountTypeRepository;

    @Autowired
    private TransactionAccountTypeMapper transactionAccountTypeMapper;

    @Autowired
    private TransactionAccountTypeSearchRepository transactionAccountTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionAccountTypeMockMvc;

    private TransactionAccountType transactionAccountType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionAccountType createEntity(EntityManager em) {
        TransactionAccountType transactionAccountType = new TransactionAccountType().name(DEFAULT_NAME);
        return transactionAccountType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionAccountType createUpdatedEntity(EntityManager em) {
        TransactionAccountType transactionAccountType = new TransactionAccountType().name(UPDATED_NAME);
        return transactionAccountType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionAccountTypeSearchRepository.deleteAll();
        assertThat(transactionAccountTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionAccountType = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionAccountType() throws Exception {
        int databaseSizeBeforeCreate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);
        restTransactionAccountTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionAccountType testTransactionAccountType = transactionAccountTypeList.get(transactionAccountTypeList.size() - 1);
        assertThat(testTransactionAccountType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createTransactionAccountTypeWithExistingId() throws Exception {
        // Create the TransactionAccountType with an existing ID
        transactionAccountType.setId(1L);
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        int databaseSizeBeforeCreate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionAccountTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        // set the field null
        transactionAccountType.setName(null);

        // Create the TransactionAccountType, which fails.
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        restTransactionAccountTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionAccountTypes() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get all the transactionAccountTypeList
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionAccountType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getTransactionAccountType() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get the transactionAccountType
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionAccountType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionAccountType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getTransactionAccountTypesByIdFiltering() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        Long id = transactionAccountType.getId();

        defaultTransactionAccountTypeShouldBeFound("id.equals=" + id);
        defaultTransactionAccountTypeShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionAccountTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionAccountTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionAccountTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionAccountTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionAccountTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get all the transactionAccountTypeList where name equals to DEFAULT_NAME
        defaultTransactionAccountTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the transactionAccountTypeList where name equals to UPDATED_NAME
        defaultTransactionAccountTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get all the transactionAccountTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTransactionAccountTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the transactionAccountTypeList where name equals to UPDATED_NAME
        defaultTransactionAccountTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get all the transactionAccountTypeList where name is not null
        defaultTransactionAccountTypeShouldBeFound("name.specified=true");

        // Get all the transactionAccountTypeList where name is null
        defaultTransactionAccountTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionAccountTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get all the transactionAccountTypeList where name contains DEFAULT_NAME
        defaultTransactionAccountTypeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the transactionAccountTypeList where name contains UPDATED_NAME
        defaultTransactionAccountTypeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        // Get all the transactionAccountTypeList where name does not contain DEFAULT_NAME
        defaultTransactionAccountTypeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the transactionAccountTypeList where name does not contain UPDATED_NAME
        defaultTransactionAccountTypeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionAccountTypeShouldBeFound(String filter) throws Exception {
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionAccountType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionAccountTypeShouldNotBeFound(String filter) throws Exception {
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionAccountType() throws Exception {
        // Get the transactionAccountType
        restTransactionAccountTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionAccountType() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        transactionAccountTypeSearchRepository.save(transactionAccountType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());

        // Update the transactionAccountType
        TransactionAccountType updatedTransactionAccountType = transactionAccountTypeRepository
            .findById(transactionAccountType.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionAccountType are not directly saved in db
        em.detach(updatedTransactionAccountType);
        updatedTransactionAccountType.name(UPDATED_NAME);
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(updatedTransactionAccountType);

        restTransactionAccountTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionAccountTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        TransactionAccountType testTransactionAccountType = transactionAccountTypeList.get(transactionAccountTypeList.size() - 1);
        assertThat(testTransactionAccountType.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionAccountType> transactionAccountTypeSearchList = IterableUtils.toList(
                    transactionAccountTypeSearchRepository.findAll()
                );
                TransactionAccountType testTransactionAccountTypeSearch = transactionAccountTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionAccountTypeSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionAccountType() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        transactionAccountType.setId(count.incrementAndGet());

        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionAccountTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionAccountTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionAccountType() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        transactionAccountType.setId(count.incrementAndGet());

        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionAccountType() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        transactionAccountType.setId(count.incrementAndGet());

        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountTypeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionAccountTypeWithPatch() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();

        // Update the transactionAccountType using partial update
        TransactionAccountType partialUpdatedTransactionAccountType = new TransactionAccountType();
        partialUpdatedTransactionAccountType.setId(transactionAccountType.getId());

        partialUpdatedTransactionAccountType.name(UPDATED_NAME);

        restTransactionAccountTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionAccountType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionAccountType))
            )
            .andExpect(status().isOk());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        TransactionAccountType testTransactionAccountType = transactionAccountTypeList.get(transactionAccountTypeList.size() - 1);
        assertThat(testTransactionAccountType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateTransactionAccountTypeWithPatch() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);

        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();

        // Update the transactionAccountType using partial update
        TransactionAccountType partialUpdatedTransactionAccountType = new TransactionAccountType();
        partialUpdatedTransactionAccountType.setId(transactionAccountType.getId());

        partialUpdatedTransactionAccountType.name(UPDATED_NAME);

        restTransactionAccountTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionAccountType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionAccountType))
            )
            .andExpect(status().isOk());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        TransactionAccountType testTransactionAccountType = transactionAccountTypeList.get(transactionAccountTypeList.size() - 1);
        assertThat(testTransactionAccountType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionAccountType() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        transactionAccountType.setId(count.incrementAndGet());

        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionAccountTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionAccountTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionAccountType() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        transactionAccountType.setId(count.incrementAndGet());

        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionAccountType() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        transactionAccountType.setId(count.incrementAndGet());

        // Create the TransactionAccountType
        TransactionAccountTypeDTO transactionAccountTypeDTO = transactionAccountTypeMapper.toDto(transactionAccountType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionAccountType in the database
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionAccountType() throws Exception {
        // Initialize the database
        transactionAccountTypeRepository.saveAndFlush(transactionAccountType);
        transactionAccountTypeRepository.save(transactionAccountType);
        transactionAccountTypeSearchRepository.save(transactionAccountType);

        int databaseSizeBeforeDelete = transactionAccountTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionAccountType
        restTransactionAccountTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionAccountType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionAccountType> transactionAccountTypeList = transactionAccountTypeRepository.findAll();
        assertThat(transactionAccountTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionAccountType() throws Exception {
        // Initialize the database
        transactionAccountType = transactionAccountTypeRepository.saveAndFlush(transactionAccountType);
        transactionAccountTypeSearchRepository.save(transactionAccountType);

        // Search the transactionAccountType
        restTransactionAccountTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionAccountType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionAccountType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
