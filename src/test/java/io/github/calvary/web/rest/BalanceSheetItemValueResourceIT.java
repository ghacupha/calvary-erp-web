package io.github.calvary.web.rest;

import static io.github.calvary.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.domain.BalanceSheetItemValue;
import io.github.calvary.repository.BalanceSheetItemValueRepository;
import io.github.calvary.repository.search.BalanceSheetItemValueSearchRepository;
import io.github.calvary.service.BalanceSheetItemValueService;
import io.github.calvary.service.dto.BalanceSheetItemValueDTO;
import io.github.calvary.service.mapper.BalanceSheetItemValueMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BalanceSheetItemValueResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BalanceSheetItemValueResourceIT {

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_EFFECTIVE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EFFECTIVE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EFFECTIVE_DATE = LocalDate.ofEpochDay(-1L);

    private static final BigDecimal DEFAULT_ITEM_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_ITEM_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_ITEM_AMOUNT = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/balance-sheet-item-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/balance-sheet-item-values";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BalanceSheetItemValueRepository balanceSheetItemValueRepository;

    @Mock
    private BalanceSheetItemValueRepository balanceSheetItemValueRepositoryMock;

    @Autowired
    private BalanceSheetItemValueMapper balanceSheetItemValueMapper;

    @Mock
    private BalanceSheetItemValueService balanceSheetItemValueServiceMock;

    @Autowired
    private BalanceSheetItemValueSearchRepository balanceSheetItemValueSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBalanceSheetItemValueMockMvc;

    private BalanceSheetItemValue balanceSheetItemValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BalanceSheetItemValue createEntity(EntityManager em) {
        BalanceSheetItemValue balanceSheetItemValue = new BalanceSheetItemValue()
            .shortDescription(DEFAULT_SHORT_DESCRIPTION)
            .effectiveDate(DEFAULT_EFFECTIVE_DATE)
            .itemAmount(DEFAULT_ITEM_AMOUNT);
        // Add required entity
        BalanceSheetItemType balanceSheetItemType;
        if (TestUtil.findAll(em, BalanceSheetItemType.class).isEmpty()) {
            balanceSheetItemType = BalanceSheetItemTypeResourceIT.createEntity(em);
            em.persist(balanceSheetItemType);
            em.flush();
        } else {
            balanceSheetItemType = TestUtil.findAll(em, BalanceSheetItemType.class).get(0);
        }
        balanceSheetItemValue.setItemType(balanceSheetItemType);
        return balanceSheetItemValue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BalanceSheetItemValue createUpdatedEntity(EntityManager em) {
        BalanceSheetItemValue balanceSheetItemValue = new BalanceSheetItemValue()
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .effectiveDate(UPDATED_EFFECTIVE_DATE)
            .itemAmount(UPDATED_ITEM_AMOUNT);
        // Add required entity
        BalanceSheetItemType balanceSheetItemType;
        if (TestUtil.findAll(em, BalanceSheetItemType.class).isEmpty()) {
            balanceSheetItemType = BalanceSheetItemTypeResourceIT.createUpdatedEntity(em);
            em.persist(balanceSheetItemType);
            em.flush();
        } else {
            balanceSheetItemType = TestUtil.findAll(em, BalanceSheetItemType.class).get(0);
        }
        balanceSheetItemValue.setItemType(balanceSheetItemType);
        return balanceSheetItemValue;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        balanceSheetItemValueSearchRepository.deleteAll();
        assertThat(balanceSheetItemValueSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        balanceSheetItemValue = createEntity(em);
    }

    @Test
    @Transactional
    void createBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeCreate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);
        restBalanceSheetItemValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BalanceSheetItemValue testBalanceSheetItemValue = balanceSheetItemValueList.get(balanceSheetItemValueList.size() - 1);
        assertThat(testBalanceSheetItemValue.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testBalanceSheetItemValue.getEffectiveDate()).isEqualTo(DEFAULT_EFFECTIVE_DATE);
        assertThat(testBalanceSheetItemValue.getItemAmount()).isEqualByComparingTo(DEFAULT_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void createBalanceSheetItemValueWithExistingId() throws Exception {
        // Create the BalanceSheetItemValue with an existing ID
        balanceSheetItemValue.setId(1L);
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        int databaseSizeBeforeCreate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBalanceSheetItemValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEffectiveDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        // set the field null
        balanceSheetItemValue.setEffectiveDate(null);

        // Create the BalanceSheetItemValue, which fails.
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        restBalanceSheetItemValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        // set the field null
        balanceSheetItemValue.setItemAmount(null);

        // Create the BalanceSheetItemValue, which fails.
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        restBalanceSheetItemValueMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValues() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceSheetItemValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].effectiveDate").value(hasItem(DEFAULT_EFFECTIVE_DATE.toString())))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBalanceSheetItemValuesWithEagerRelationshipsIsEnabled() throws Exception {
        when(balanceSheetItemValueServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBalanceSheetItemValueMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(balanceSheetItemValueServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBalanceSheetItemValuesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(balanceSheetItemValueServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBalanceSheetItemValueMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(balanceSheetItemValueRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBalanceSheetItemValue() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get the balanceSheetItemValue
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_API_URL_ID, balanceSheetItemValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(balanceSheetItemValue.getId().intValue()))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION))
            .andExpect(jsonPath("$.effectiveDate").value(DEFAULT_EFFECTIVE_DATE.toString()))
            .andExpect(jsonPath("$.itemAmount").value(sameNumber(DEFAULT_ITEM_AMOUNT)));
    }

    @Test
    @Transactional
    void getBalanceSheetItemValuesByIdFiltering() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        Long id = balanceSheetItemValue.getId();

        defaultBalanceSheetItemValueShouldBeFound("id.equals=" + id);
        defaultBalanceSheetItemValueShouldNotBeFound("id.notEquals=" + id);

        defaultBalanceSheetItemValueShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBalanceSheetItemValueShouldNotBeFound("id.greaterThan=" + id);

        defaultBalanceSheetItemValueShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBalanceSheetItemValueShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByShortDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where shortDescription equals to DEFAULT_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldBeFound("shortDescription.equals=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemValueList where shortDescription equals to UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldNotBeFound("shortDescription.equals=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByShortDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where shortDescription in DEFAULT_SHORT_DESCRIPTION or UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldBeFound("shortDescription.in=" + DEFAULT_SHORT_DESCRIPTION + "," + UPDATED_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemValueList where shortDescription equals to UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldNotBeFound("shortDescription.in=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByShortDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where shortDescription is not null
        defaultBalanceSheetItemValueShouldBeFound("shortDescription.specified=true");

        // Get all the balanceSheetItemValueList where shortDescription is null
        defaultBalanceSheetItemValueShouldNotBeFound("shortDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByShortDescriptionContainsSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where shortDescription contains DEFAULT_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldBeFound("shortDescription.contains=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemValueList where shortDescription contains UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldNotBeFound("shortDescription.contains=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByShortDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where shortDescription does not contain DEFAULT_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldNotBeFound("shortDescription.doesNotContain=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemValueList where shortDescription does not contain UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemValueShouldBeFound("shortDescription.doesNotContain=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate equals to DEFAULT_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.equals=" + DEFAULT_EFFECTIVE_DATE);

        // Get all the balanceSheetItemValueList where effectiveDate equals to UPDATED_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.equals=" + UPDATED_EFFECTIVE_DATE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsInShouldWork() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate in DEFAULT_EFFECTIVE_DATE or UPDATED_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.in=" + DEFAULT_EFFECTIVE_DATE + "," + UPDATED_EFFECTIVE_DATE);

        // Get all the balanceSheetItemValueList where effectiveDate equals to UPDATED_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.in=" + UPDATED_EFFECTIVE_DATE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate is not null
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.specified=true");

        // Get all the balanceSheetItemValueList where effectiveDate is null
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate is greater than or equal to DEFAULT_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.greaterThanOrEqual=" + DEFAULT_EFFECTIVE_DATE);

        // Get all the balanceSheetItemValueList where effectiveDate is greater than or equal to UPDATED_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.greaterThanOrEqual=" + UPDATED_EFFECTIVE_DATE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate is less than or equal to DEFAULT_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.lessThanOrEqual=" + DEFAULT_EFFECTIVE_DATE);

        // Get all the balanceSheetItemValueList where effectiveDate is less than or equal to SMALLER_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.lessThanOrEqual=" + SMALLER_EFFECTIVE_DATE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsLessThanSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate is less than DEFAULT_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.lessThan=" + DEFAULT_EFFECTIVE_DATE);

        // Get all the balanceSheetItemValueList where effectiveDate is less than UPDATED_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.lessThan=" + UPDATED_EFFECTIVE_DATE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByEffectiveDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where effectiveDate is greater than DEFAULT_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldNotBeFound("effectiveDate.greaterThan=" + DEFAULT_EFFECTIVE_DATE);

        // Get all the balanceSheetItemValueList where effectiveDate is greater than SMALLER_EFFECTIVE_DATE
        defaultBalanceSheetItemValueShouldBeFound("effectiveDate.greaterThan=" + SMALLER_EFFECTIVE_DATE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount equals to DEFAULT_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.equals=" + DEFAULT_ITEM_AMOUNT);

        // Get all the balanceSheetItemValueList where itemAmount equals to UPDATED_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.equals=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsInShouldWork() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount in DEFAULT_ITEM_AMOUNT or UPDATED_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.in=" + DEFAULT_ITEM_AMOUNT + "," + UPDATED_ITEM_AMOUNT);

        // Get all the balanceSheetItemValueList where itemAmount equals to UPDATED_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.in=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount is not null
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.specified=true");

        // Get all the balanceSheetItemValueList where itemAmount is null
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount is greater than or equal to DEFAULT_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.greaterThanOrEqual=" + DEFAULT_ITEM_AMOUNT);

        // Get all the balanceSheetItemValueList where itemAmount is greater than or equal to UPDATED_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.greaterThanOrEqual=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount is less than or equal to DEFAULT_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.lessThanOrEqual=" + DEFAULT_ITEM_AMOUNT);

        // Get all the balanceSheetItemValueList where itemAmount is less than or equal to SMALLER_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.lessThanOrEqual=" + SMALLER_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount is less than DEFAULT_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.lessThan=" + DEFAULT_ITEM_AMOUNT);

        // Get all the balanceSheetItemValueList where itemAmount is less than UPDATED_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.lessThan=" + UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        // Get all the balanceSheetItemValueList where itemAmount is greater than DEFAULT_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldNotBeFound("itemAmount.greaterThan=" + DEFAULT_ITEM_AMOUNT);

        // Get all the balanceSheetItemValueList where itemAmount is greater than SMALLER_ITEM_AMOUNT
        defaultBalanceSheetItemValueShouldBeFound("itemAmount.greaterThan=" + SMALLER_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemValuesByItemTypeIsEqualToSomething() throws Exception {
        BalanceSheetItemType itemType;
        if (TestUtil.findAll(em, BalanceSheetItemType.class).isEmpty()) {
            balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);
            itemType = BalanceSheetItemTypeResourceIT.createEntity(em);
        } else {
            itemType = TestUtil.findAll(em, BalanceSheetItemType.class).get(0);
        }
        em.persist(itemType);
        em.flush();
        balanceSheetItemValue.setItemType(itemType);
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);
        Long itemTypeId = itemType.getId();
        // Get all the balanceSheetItemValueList where itemType equals to itemTypeId
        defaultBalanceSheetItemValueShouldBeFound("itemTypeId.equals=" + itemTypeId);

        // Get all the balanceSheetItemValueList where itemType equals to (itemTypeId + 1)
        defaultBalanceSheetItemValueShouldNotBeFound("itemTypeId.equals=" + (itemTypeId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBalanceSheetItemValueShouldBeFound(String filter) throws Exception {
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceSheetItemValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].effectiveDate").value(hasItem(DEFAULT_EFFECTIVE_DATE.toString())))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));

        // Check, that the count call also returns 1
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBalanceSheetItemValueShouldNotBeFound(String filter) throws Exception {
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBalanceSheetItemValue() throws Exception {
        // Get the balanceSheetItemValue
        restBalanceSheetItemValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBalanceSheetItemValue() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        balanceSheetItemValueSearchRepository.save(balanceSheetItemValue);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());

        // Update the balanceSheetItemValue
        BalanceSheetItemValue updatedBalanceSheetItemValue = balanceSheetItemValueRepository
            .findById(balanceSheetItemValue.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedBalanceSheetItemValue are not directly saved in db
        em.detach(updatedBalanceSheetItemValue);
        updatedBalanceSheetItemValue
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .effectiveDate(UPDATED_EFFECTIVE_DATE)
            .itemAmount(UPDATED_ITEM_AMOUNT);
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(updatedBalanceSheetItemValue);

        restBalanceSheetItemValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, balanceSheetItemValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isOk());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        BalanceSheetItemValue testBalanceSheetItemValue = balanceSheetItemValueList.get(balanceSheetItemValueList.size() - 1);
        assertThat(testBalanceSheetItemValue.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testBalanceSheetItemValue.getEffectiveDate()).isEqualTo(UPDATED_EFFECTIVE_DATE);
        assertThat(testBalanceSheetItemValue.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BalanceSheetItemValue> balanceSheetItemValueSearchList = IterableUtils.toList(
                    balanceSheetItemValueSearchRepository.findAll()
                );
                BalanceSheetItemValue testBalanceSheetItemValueSearch = balanceSheetItemValueSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBalanceSheetItemValueSearch.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
                assertThat(testBalanceSheetItemValueSearch.getEffectiveDate()).isEqualTo(UPDATED_EFFECTIVE_DATE);
                assertThat(testBalanceSheetItemValueSearch.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
            });
    }

    @Test
    @Transactional
    void putNonExistingBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        balanceSheetItemValue.setId(count.incrementAndGet());

        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBalanceSheetItemValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, balanceSheetItemValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        balanceSheetItemValue.setId(count.incrementAndGet());

        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        balanceSheetItemValue.setId(count.incrementAndGet());

        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemValueMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBalanceSheetItemValueWithPatch() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();

        // Update the balanceSheetItemValue using partial update
        BalanceSheetItemValue partialUpdatedBalanceSheetItemValue = new BalanceSheetItemValue();
        partialUpdatedBalanceSheetItemValue.setId(balanceSheetItemValue.getId());

        restBalanceSheetItemValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBalanceSheetItemValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBalanceSheetItemValue))
            )
            .andExpect(status().isOk());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        BalanceSheetItemValue testBalanceSheetItemValue = balanceSheetItemValueList.get(balanceSheetItemValueList.size() - 1);
        assertThat(testBalanceSheetItemValue.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testBalanceSheetItemValue.getEffectiveDate()).isEqualTo(DEFAULT_EFFECTIVE_DATE);
        assertThat(testBalanceSheetItemValue.getItemAmount()).isEqualByComparingTo(DEFAULT_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateBalanceSheetItemValueWithPatch() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);

        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();

        // Update the balanceSheetItemValue using partial update
        BalanceSheetItemValue partialUpdatedBalanceSheetItemValue = new BalanceSheetItemValue();
        partialUpdatedBalanceSheetItemValue.setId(balanceSheetItemValue.getId());

        partialUpdatedBalanceSheetItemValue
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .effectiveDate(UPDATED_EFFECTIVE_DATE)
            .itemAmount(UPDATED_ITEM_AMOUNT);

        restBalanceSheetItemValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBalanceSheetItemValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBalanceSheetItemValue))
            )
            .andExpect(status().isOk());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        BalanceSheetItemValue testBalanceSheetItemValue = balanceSheetItemValueList.get(balanceSheetItemValueList.size() - 1);
        assertThat(testBalanceSheetItemValue.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testBalanceSheetItemValue.getEffectiveDate()).isEqualTo(UPDATED_EFFECTIVE_DATE);
        assertThat(testBalanceSheetItemValue.getItemAmount()).isEqualByComparingTo(UPDATED_ITEM_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        balanceSheetItemValue.setId(count.incrementAndGet());

        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBalanceSheetItemValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, balanceSheetItemValueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        balanceSheetItemValue.setId(count.incrementAndGet());

        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBalanceSheetItemValue() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        balanceSheetItemValue.setId(count.incrementAndGet());

        // Create the BalanceSheetItemValue
        BalanceSheetItemValueDTO balanceSheetItemValueDTO = balanceSheetItemValueMapper.toDto(balanceSheetItemValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemValueMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemValueDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BalanceSheetItemValue in the database
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBalanceSheetItemValue() throws Exception {
        // Initialize the database
        balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);
        balanceSheetItemValueRepository.save(balanceSheetItemValue);
        balanceSheetItemValueSearchRepository.save(balanceSheetItemValue);

        int databaseSizeBeforeDelete = balanceSheetItemValueRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the balanceSheetItemValue
        restBalanceSheetItemValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, balanceSheetItemValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BalanceSheetItemValue> balanceSheetItemValueList = balanceSheetItemValueRepository.findAll();
        assertThat(balanceSheetItemValueList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBalanceSheetItemValue() throws Exception {
        // Initialize the database
        balanceSheetItemValue = balanceSheetItemValueRepository.saveAndFlush(balanceSheetItemValue);
        balanceSheetItemValueSearchRepository.save(balanceSheetItemValue);

        // Search the balanceSheetItemValue
        restBalanceSheetItemValueMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + balanceSheetItemValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceSheetItemValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].effectiveDate").value(hasItem(DEFAULT_EFFECTIVE_DATE.toString())))
            .andExpect(jsonPath("$.[*].itemAmount").value(hasItem(sameNumber(DEFAULT_ITEM_AMOUNT))));
    }
}
