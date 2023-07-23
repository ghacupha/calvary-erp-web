package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.domain.BalanceSheetItemType;
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.repository.BalanceSheetItemTypeRepository;
import io.github.calvary.repository.search.BalanceSheetItemTypeSearchRepository;
import io.github.calvary.service.BalanceSheetItemTypeService;
import io.github.calvary.service.dto.BalanceSheetItemTypeDTO;
import io.github.calvary.service.mapper.BalanceSheetItemTypeMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link BalanceSheetItemTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BalanceSheetItemTypeResourceIT {

    private static final Integer DEFAULT_ITEM_SEQUENCE = 1;
    private static final Integer UPDATED_ITEM_SEQUENCE = 2;
    private static final Integer SMALLER_ITEM_SEQUENCE = 1 - 1;

    private static final String DEFAULT_ITEM_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/balance-sheet-item-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/balance-sheet-item-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BalanceSheetItemTypeRepository balanceSheetItemTypeRepository;

    @Mock
    private BalanceSheetItemTypeRepository balanceSheetItemTypeRepositoryMock;

    @Autowired
    private BalanceSheetItemTypeMapper balanceSheetItemTypeMapper;

    @Mock
    private BalanceSheetItemTypeService balanceSheetItemTypeServiceMock;

    @Autowired
    private BalanceSheetItemTypeSearchRepository balanceSheetItemTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBalanceSheetItemTypeMockMvc;

    private BalanceSheetItemType balanceSheetItemType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BalanceSheetItemType createEntity(EntityManager em) {
        BalanceSheetItemType balanceSheetItemType = new BalanceSheetItemType()
            .itemSequence(DEFAULT_ITEM_SEQUENCE)
            .itemNumber(DEFAULT_ITEM_NUMBER)
            .shortDescription(DEFAULT_SHORT_DESCRIPTION);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        balanceSheetItemType.setTransactionAccount(transactionAccount);
        return balanceSheetItemType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BalanceSheetItemType createUpdatedEntity(EntityManager em) {
        BalanceSheetItemType balanceSheetItemType = new BalanceSheetItemType()
            .itemSequence(UPDATED_ITEM_SEQUENCE)
            .itemNumber(UPDATED_ITEM_NUMBER)
            .shortDescription(UPDATED_SHORT_DESCRIPTION);
        // Add required entity
        TransactionAccount transactionAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccount = TransactionAccountResourceIT.createUpdatedEntity(em);
            em.persist(transactionAccount);
            em.flush();
        } else {
            transactionAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        balanceSheetItemType.setTransactionAccount(transactionAccount);
        return balanceSheetItemType;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        balanceSheetItemTypeSearchRepository.deleteAll();
        assertThat(balanceSheetItemTypeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        balanceSheetItemType = createEntity(em);
    }

    @Test
    @Transactional
    void createBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeCreate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);
        restBalanceSheetItemTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        BalanceSheetItemType testBalanceSheetItemType = balanceSheetItemTypeList.get(balanceSheetItemTypeList.size() - 1);
        assertThat(testBalanceSheetItemType.getItemSequence()).isEqualTo(DEFAULT_ITEM_SEQUENCE);
        assertThat(testBalanceSheetItemType.getItemNumber()).isEqualTo(DEFAULT_ITEM_NUMBER);
        assertThat(testBalanceSheetItemType.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createBalanceSheetItemTypeWithExistingId() throws Exception {
        // Create the BalanceSheetItemType with an existing ID
        balanceSheetItemType.setId(1L);
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        int databaseSizeBeforeCreate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBalanceSheetItemTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemSequenceIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        // set the field null
        balanceSheetItemType.setItemSequence(null);

        // Create the BalanceSheetItemType, which fails.
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        restBalanceSheetItemTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkItemNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        // set the field null
        balanceSheetItemType.setItemNumber(null);

        // Create the BalanceSheetItemType, which fails.
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        restBalanceSheetItemTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypes() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceSheetItemType.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemSequence").value(hasItem(DEFAULT_ITEM_SEQUENCE)))
            .andExpect(jsonPath("$.[*].itemNumber").value(hasItem(DEFAULT_ITEM_NUMBER)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBalanceSheetItemTypesWithEagerRelationshipsIsEnabled() throws Exception {
        when(balanceSheetItemTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBalanceSheetItemTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(balanceSheetItemTypeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBalanceSheetItemTypesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(balanceSheetItemTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBalanceSheetItemTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(balanceSheetItemTypeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBalanceSheetItemType() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get the balanceSheetItemType
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, balanceSheetItemType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(balanceSheetItemType.getId().intValue()))
            .andExpect(jsonPath("$.itemSequence").value(DEFAULT_ITEM_SEQUENCE))
            .andExpect(jsonPath("$.itemNumber").value(DEFAULT_ITEM_NUMBER))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getBalanceSheetItemTypesByIdFiltering() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        Long id = balanceSheetItemType.getId();

        defaultBalanceSheetItemTypeShouldBeFound("id.equals=" + id);
        defaultBalanceSheetItemTypeShouldNotBeFound("id.notEquals=" + id);

        defaultBalanceSheetItemTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBalanceSheetItemTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultBalanceSheetItemTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBalanceSheetItemTypeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence equals to DEFAULT_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.equals=" + DEFAULT_ITEM_SEQUENCE);

        // Get all the balanceSheetItemTypeList where itemSequence equals to UPDATED_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.equals=" + UPDATED_ITEM_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsInShouldWork() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence in DEFAULT_ITEM_SEQUENCE or UPDATED_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.in=" + DEFAULT_ITEM_SEQUENCE + "," + UPDATED_ITEM_SEQUENCE);

        // Get all the balanceSheetItemTypeList where itemSequence equals to UPDATED_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.in=" + UPDATED_ITEM_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence is not null
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.specified=true");

        // Get all the balanceSheetItemTypeList where itemSequence is null
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.specified=false");
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence is greater than or equal to DEFAULT_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.greaterThanOrEqual=" + DEFAULT_ITEM_SEQUENCE);

        // Get all the balanceSheetItemTypeList where itemSequence is greater than or equal to UPDATED_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.greaterThanOrEqual=" + UPDATED_ITEM_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence is less than or equal to DEFAULT_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.lessThanOrEqual=" + DEFAULT_ITEM_SEQUENCE);

        // Get all the balanceSheetItemTypeList where itemSequence is less than or equal to SMALLER_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.lessThanOrEqual=" + SMALLER_ITEM_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsLessThanSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence is less than DEFAULT_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.lessThan=" + DEFAULT_ITEM_SEQUENCE);

        // Get all the balanceSheetItemTypeList where itemSequence is less than UPDATED_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.lessThan=" + UPDATED_ITEM_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemSequenceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemSequence is greater than DEFAULT_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldNotBeFound("itemSequence.greaterThan=" + DEFAULT_ITEM_SEQUENCE);

        // Get all the balanceSheetItemTypeList where itemSequence is greater than SMALLER_ITEM_SEQUENCE
        defaultBalanceSheetItemTypeShouldBeFound("itemSequence.greaterThan=" + SMALLER_ITEM_SEQUENCE);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemNumber equals to DEFAULT_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldBeFound("itemNumber.equals=" + DEFAULT_ITEM_NUMBER);

        // Get all the balanceSheetItemTypeList where itemNumber equals to UPDATED_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldNotBeFound("itemNumber.equals=" + UPDATED_ITEM_NUMBER);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemNumberIsInShouldWork() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemNumber in DEFAULT_ITEM_NUMBER or UPDATED_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldBeFound("itemNumber.in=" + DEFAULT_ITEM_NUMBER + "," + UPDATED_ITEM_NUMBER);

        // Get all the balanceSheetItemTypeList where itemNumber equals to UPDATED_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldNotBeFound("itemNumber.in=" + UPDATED_ITEM_NUMBER);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemNumber is not null
        defaultBalanceSheetItemTypeShouldBeFound("itemNumber.specified=true");

        // Get all the balanceSheetItemTypeList where itemNumber is null
        defaultBalanceSheetItemTypeShouldNotBeFound("itemNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemNumberContainsSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemNumber contains DEFAULT_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldBeFound("itemNumber.contains=" + DEFAULT_ITEM_NUMBER);

        // Get all the balanceSheetItemTypeList where itemNumber contains UPDATED_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldNotBeFound("itemNumber.contains=" + UPDATED_ITEM_NUMBER);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByItemNumberNotContainsSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where itemNumber does not contain DEFAULT_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldNotBeFound("itemNumber.doesNotContain=" + DEFAULT_ITEM_NUMBER);

        // Get all the balanceSheetItemTypeList where itemNumber does not contain UPDATED_ITEM_NUMBER
        defaultBalanceSheetItemTypeShouldBeFound("itemNumber.doesNotContain=" + UPDATED_ITEM_NUMBER);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByShortDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where shortDescription equals to DEFAULT_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldBeFound("shortDescription.equals=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemTypeList where shortDescription equals to UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldNotBeFound("shortDescription.equals=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByShortDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where shortDescription in DEFAULT_SHORT_DESCRIPTION or UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldBeFound("shortDescription.in=" + DEFAULT_SHORT_DESCRIPTION + "," + UPDATED_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemTypeList where shortDescription equals to UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldNotBeFound("shortDescription.in=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByShortDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where shortDescription is not null
        defaultBalanceSheetItemTypeShouldBeFound("shortDescription.specified=true");

        // Get all the balanceSheetItemTypeList where shortDescription is null
        defaultBalanceSheetItemTypeShouldNotBeFound("shortDescription.specified=false");
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByShortDescriptionContainsSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where shortDescription contains DEFAULT_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldBeFound("shortDescription.contains=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemTypeList where shortDescription contains UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldNotBeFound("shortDescription.contains=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByShortDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        // Get all the balanceSheetItemTypeList where shortDescription does not contain DEFAULT_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldNotBeFound("shortDescription.doesNotContain=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the balanceSheetItemTypeList where shortDescription does not contain UPDATED_SHORT_DESCRIPTION
        defaultBalanceSheetItemTypeShouldBeFound("shortDescription.doesNotContain=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBalanceSheetItemTypesByTransactionAccountIsEqualToSomething() throws Exception {
        // Get already existing entity
        TransactionAccount transactionAccount = balanceSheetItemType.getTransactionAccount();
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);
        Long transactionAccountId = transactionAccount.getId();
        // Get all the balanceSheetItemTypeList where transactionAccount equals to transactionAccountId
        defaultBalanceSheetItemTypeShouldBeFound("transactionAccountId.equals=" + transactionAccountId);

        // Get all the balanceSheetItemTypeList where transactionAccount equals to (transactionAccountId + 1)
        defaultBalanceSheetItemTypeShouldNotBeFound("transactionAccountId.equals=" + (transactionAccountId + 1));
    }

    // @Test
    @Transactional
    void getAllBalanceSheetItemTypesByParentItemIsEqualToSomething() throws Exception {
        BalanceSheetItemType parentItem;
        if (TestUtil.findAll(em, BalanceSheetItemType.class).isEmpty()) {
            balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);
            parentItem = BalanceSheetItemTypeResourceIT.createEntity(em);
        } else {
            parentItem = TestUtil.findAll(em, BalanceSheetItemType.class).get(0);
        }
        em.persist(parentItem);
        em.flush();
        balanceSheetItemType.setParentItem(parentItem);
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);
        Long parentItemId = parentItem.getId();
        // Get all the balanceSheetItemTypeList where parentItem equals to parentItemId
        defaultBalanceSheetItemTypeShouldBeFound("parentItemId.equals=" + parentItemId);

        // Get all the balanceSheetItemTypeList where parentItem equals to (parentItemId + 1)
        defaultBalanceSheetItemTypeShouldNotBeFound("parentItemId.equals=" + (parentItemId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBalanceSheetItemTypeShouldBeFound(String filter) throws Exception {
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceSheetItemType.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemSequence").value(hasItem(DEFAULT_ITEM_SEQUENCE)))
            .andExpect(jsonPath("$.[*].itemNumber").value(hasItem(DEFAULT_ITEM_NUMBER)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBalanceSheetItemTypeShouldNotBeFound(String filter) throws Exception {
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBalanceSheetItemType() throws Exception {
        // Get the balanceSheetItemType
        restBalanceSheetItemTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBalanceSheetItemType() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        balanceSheetItemTypeSearchRepository.save(balanceSheetItemType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());

        // Update the balanceSheetItemType
        BalanceSheetItemType updatedBalanceSheetItemType = balanceSheetItemTypeRepository
            .findById(balanceSheetItemType.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedBalanceSheetItemType are not directly saved in db
        em.detach(updatedBalanceSheetItemType);
        updatedBalanceSheetItemType
            .itemSequence(UPDATED_ITEM_SEQUENCE)
            .itemNumber(UPDATED_ITEM_NUMBER)
            .shortDescription(UPDATED_SHORT_DESCRIPTION);
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(updatedBalanceSheetItemType);

        restBalanceSheetItemTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, balanceSheetItemTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        BalanceSheetItemType testBalanceSheetItemType = balanceSheetItemTypeList.get(balanceSheetItemTypeList.size() - 1);
        assertThat(testBalanceSheetItemType.getItemSequence()).isEqualTo(UPDATED_ITEM_SEQUENCE);
        assertThat(testBalanceSheetItemType.getItemNumber()).isEqualTo(UPDATED_ITEM_NUMBER);
        assertThat(testBalanceSheetItemType.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<BalanceSheetItemType> balanceSheetItemTypeSearchList = IterableUtils.toList(
                    balanceSheetItemTypeSearchRepository.findAll()
                );
                BalanceSheetItemType testBalanceSheetItemTypeSearch = balanceSheetItemTypeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBalanceSheetItemTypeSearch.getItemSequence()).isEqualTo(UPDATED_ITEM_SEQUENCE);
                assertThat(testBalanceSheetItemTypeSearch.getItemNumber()).isEqualTo(UPDATED_ITEM_NUMBER);
                assertThat(testBalanceSheetItemTypeSearch.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        balanceSheetItemType.setId(count.incrementAndGet());

        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBalanceSheetItemTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, balanceSheetItemTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        balanceSheetItemType.setId(count.incrementAndGet());

        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        balanceSheetItemType.setId(count.incrementAndGet());

        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemTypeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBalanceSheetItemTypeWithPatch() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();

        // Update the balanceSheetItemType using partial update
        BalanceSheetItemType partialUpdatedBalanceSheetItemType = new BalanceSheetItemType();
        partialUpdatedBalanceSheetItemType.setId(balanceSheetItemType.getId());

        partialUpdatedBalanceSheetItemType.shortDescription(UPDATED_SHORT_DESCRIPTION);

        restBalanceSheetItemTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBalanceSheetItemType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBalanceSheetItemType))
            )
            .andExpect(status().isOk());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        BalanceSheetItemType testBalanceSheetItemType = balanceSheetItemTypeList.get(balanceSheetItemTypeList.size() - 1);
        assertThat(testBalanceSheetItemType.getItemSequence()).isEqualTo(DEFAULT_ITEM_SEQUENCE);
        assertThat(testBalanceSheetItemType.getItemNumber()).isEqualTo(DEFAULT_ITEM_NUMBER);
        assertThat(testBalanceSheetItemType.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateBalanceSheetItemTypeWithPatch() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);

        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();

        // Update the balanceSheetItemType using partial update
        BalanceSheetItemType partialUpdatedBalanceSheetItemType = new BalanceSheetItemType();
        partialUpdatedBalanceSheetItemType.setId(balanceSheetItemType.getId());

        partialUpdatedBalanceSheetItemType
            .itemSequence(UPDATED_ITEM_SEQUENCE)
            .itemNumber(UPDATED_ITEM_NUMBER)
            .shortDescription(UPDATED_SHORT_DESCRIPTION);

        restBalanceSheetItemTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBalanceSheetItemType.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBalanceSheetItemType))
            )
            .andExpect(status().isOk());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        BalanceSheetItemType testBalanceSheetItemType = balanceSheetItemTypeList.get(balanceSheetItemTypeList.size() - 1);
        assertThat(testBalanceSheetItemType.getItemSequence()).isEqualTo(UPDATED_ITEM_SEQUENCE);
        assertThat(testBalanceSheetItemType.getItemNumber()).isEqualTo(UPDATED_ITEM_NUMBER);
        assertThat(testBalanceSheetItemType.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        balanceSheetItemType.setId(count.incrementAndGet());

        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBalanceSheetItemTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, balanceSheetItemTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        balanceSheetItemType.setId(count.incrementAndGet());

        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBalanceSheetItemType() throws Exception {
        int databaseSizeBeforeUpdate = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        balanceSheetItemType.setId(count.incrementAndGet());

        // Create the BalanceSheetItemType
        BalanceSheetItemTypeDTO balanceSheetItemTypeDTO = balanceSheetItemTypeMapper.toDto(balanceSheetItemType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBalanceSheetItemTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(balanceSheetItemTypeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BalanceSheetItemType in the database
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBalanceSheetItemType() throws Exception {
        // Initialize the database
        balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);
        balanceSheetItemTypeRepository.save(balanceSheetItemType);
        balanceSheetItemTypeSearchRepository.save(balanceSheetItemType);

        int databaseSizeBeforeDelete = balanceSheetItemTypeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the balanceSheetItemType
        restBalanceSheetItemTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, balanceSheetItemType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BalanceSheetItemType> balanceSheetItemTypeList = balanceSheetItemTypeRepository.findAll();
        assertThat(balanceSheetItemTypeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(balanceSheetItemTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBalanceSheetItemType() throws Exception {
        // Initialize the database
        balanceSheetItemType = balanceSheetItemTypeRepository.saveAndFlush(balanceSheetItemType);
        balanceSheetItemTypeSearchRepository.save(balanceSheetItemType);

        // Search the balanceSheetItemType
        restBalanceSheetItemTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + balanceSheetItemType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(balanceSheetItemType.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemSequence").value(hasItem(DEFAULT_ITEM_SEQUENCE)))
            .andExpect(jsonPath("$.[*].itemNumber").value(hasItem(DEFAULT_ITEM_NUMBER)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)));
    }
}
