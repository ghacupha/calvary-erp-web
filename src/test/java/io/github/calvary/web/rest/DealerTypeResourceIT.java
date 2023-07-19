package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.DealerType;
import io.github.calvary.repository.DealerTypeRepository;
import io.github.calvary.repository.search.DealerTypeSearchRepository;
import io.github.calvary.service.dto.DealerTypeDTO;
import io.github.calvary.service.mapper.DealerTypeMapper;
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
 * Integration tests for the {@link DealerTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DealerTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dealer-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dealer-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DealerTypeRepository dealerTypeRepository;

    @Autowired
    private DealerTypeMapper dealerTypeMapper;

    @Autowired
    private DealerTypeSearchRepository dealerTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDealerTypeMockMvc;

    private DealerType dealerType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DealerType createEntity(EntityManager em) {
        DealerType dealerType = new DealerType().name(DEFAULT_NAME);
        return dealerType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DealerType createUpdatedEntity(EntityManager em) {
        DealerType dealerType = new DealerType().name(UPDATED_NAME);
        return dealerType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        dealerTypeSearchRepository.deleteAll();
        assertThat(dealerTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        dealerType = createEntity(em);
    }

    @Test
    @Transactional
    void createDealerType() throws Exception {
        int databaseSizeBeforeCreate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);
        restDealerTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        DealerType testDealerType = dealerTypeList.get(dealerTypeList.size() - 1);
        assertThat(testDealerType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createDealerTypeWithExistingId() throws Exception {
        // Create the DealerType with an existing ID
        dealerType.setId(1L);
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        int databaseSizeBeforeCreate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDealerTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        // set the field null
        dealerType.setName(null);

        // Create the DealerType, which fails.
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        restDealerTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO)))
            .andExpect(status().isBadRequest());

        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDealerTypes() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get all the dealerTypeList
        restDealerTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dealerType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getDealerType() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get the dealerType
        restDealerTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, dealerType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dealerType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getDealerTypesByIdFiltering() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        Long id = dealerType.getId();

        defaultDealerTypeShouldBeFound("id.equals=" + id);
        defaultDealerTypeShouldNotBeFound("id.notEquals=" + id);

        defaultDealerTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDealerTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultDealerTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDealerTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDealerTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get all the dealerTypeList where name equals to DEFAULT_NAME
        defaultDealerTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the dealerTypeList where name equals to UPDATED_NAME
        defaultDealerTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealerTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get all the dealerTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDealerTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the dealerTypeList where name equals to UPDATED_NAME
        defaultDealerTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealerTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get all the dealerTypeList where name is not null
        defaultDealerTypeShouldBeFound("name.specified=true");

        // Get all the dealerTypeList where name is null
        defaultDealerTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllDealerTypesByNameContainsSomething() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get all the dealerTypeList where name contains DEFAULT_NAME
        defaultDealerTypeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the dealerTypeList where name contains UPDATED_NAME
        defaultDealerTypeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllDealerTypesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        // Get all the dealerTypeList where name does not contain DEFAULT_NAME
        defaultDealerTypeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the dealerTypeList where name does not contain UPDATED_NAME
        defaultDealerTypeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDealerTypeShouldBeFound(String filter) throws Exception {
        restDealerTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dealerType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restDealerTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDealerTypeShouldNotBeFound(String filter) throws Exception {
        restDealerTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDealerTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDealerType() throws Exception {
        // Get the dealerType
        restDealerTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDealerType() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        dealerTypeSearchRepository.save(dealerType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());

        // Update the dealerType
        DealerType updatedDealerType = dealerTypeRepository.findById(dealerType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDealerType are not directly saved in db
        em.detach(updatedDealerType);
        updatedDealerType.name(UPDATED_NAME);
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(updatedDealerType);

        restDealerTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dealerTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        DealerType testDealerType = dealerTypeList.get(dealerTypeList.size() - 1);
        assertThat(testDealerType.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DealerType> dealerTypeSearchList = IterableUtils.toList(dealerTypeSearchRepository.findAll());
                DealerType testDealerTypeSearch = dealerTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDealerTypeSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingDealerType() throws Exception {
        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        dealerType.setId(count.incrementAndGet());

        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDealerTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dealerTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDealerType() throws Exception {
        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        dealerType.setId(count.incrementAndGet());

        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDealerType() throws Exception {
        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        dealerType.setId(count.incrementAndGet());

        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDealerTypeWithPatch() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();

        // Update the dealerType using partial update
        DealerType partialUpdatedDealerType = new DealerType();
        partialUpdatedDealerType.setId(dealerType.getId());

        partialUpdatedDealerType.name(UPDATED_NAME);

        restDealerTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDealerType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDealerType))
            )
            .andExpect(status().isOk());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        DealerType testDealerType = dealerTypeList.get(dealerTypeList.size() - 1);
        assertThat(testDealerType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateDealerTypeWithPatch() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);

        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();

        // Update the dealerType using partial update
        DealerType partialUpdatedDealerType = new DealerType();
        partialUpdatedDealerType.setId(dealerType.getId());

        partialUpdatedDealerType.name(UPDATED_NAME);

        restDealerTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDealerType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDealerType))
            )
            .andExpect(status().isOk());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        DealerType testDealerType = dealerTypeList.get(dealerTypeList.size() - 1);
        assertThat(testDealerType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingDealerType() throws Exception {
        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        dealerType.setId(count.incrementAndGet());

        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDealerTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dealerTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDealerType() throws Exception {
        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        dealerType.setId(count.incrementAndGet());

        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDealerType() throws Exception {
        int databaseSizeBeforeUpdate = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        dealerType.setId(count.incrementAndGet());

        // Create the DealerType
        DealerTypeDTO dealerTypeDTO = dealerTypeMapper.toDto(dealerType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDealerTypeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dealerTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DealerType in the database
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDealerType() throws Exception {
        // Initialize the database
        dealerTypeRepository.saveAndFlush(dealerType);
        dealerTypeRepository.save(dealerType);
        dealerTypeSearchRepository.save(dealerType);

        int databaseSizeBeforeDelete = dealerTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the dealerType
        restDealerTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, dealerType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DealerType> dealerTypeList = dealerTypeRepository.findAll();
        assertThat(dealerTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dealerTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDealerType() throws Exception {
        // Initialize the database
        dealerType = dealerTypeRepository.saveAndFlush(dealerType);
        dealerTypeSearchRepository.save(dealerType);

        // Search the dealerType
        restDealerTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dealerType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dealerType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
