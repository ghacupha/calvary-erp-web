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
import io.github.calvary.domain.TransactionAccount;
import io.github.calvary.domain.enumeration.TransactionAccountType;
import io.github.calvary.repository.TransactionAccountRepository;
import io.github.calvary.repository.search.TransactionAccountSearchRepository;
import io.github.calvary.service.TransactionAccountService;
import io.github.calvary.service.dto.TransactionAccountDTO;
import io.github.calvary.service.mapper.TransactionAccountMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TransactionAccountResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransactionAccountResourceIT {

    private static final String DEFAULT_ACCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final TransactionAccountType DEFAULT_TRANSACTION_ACCOUNT_TYPE = TransactionAccountType.ASSET;
    private static final TransactionAccountType UPDATED_TRANSACTION_ACCOUNT_TYPE = TransactionAccountType.LIABILITY;

    private static final BigDecimal DEFAULT_OPENING_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPENING_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_OPENING_BALANCE = new BigDecimal(1 - 1);

    private static final String ENTITY_API_URL = "/api/transaction-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-accounts";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionAccountRepository transactionAccountRepository;

    @Mock
    private TransactionAccountRepository transactionAccountRepositoryMock;

    @Autowired
    private TransactionAccountMapper transactionAccountMapper;

    @Mock
    private TransactionAccountService transactionAccountServiceMock;

    @Autowired
    private TransactionAccountSearchRepository transactionAccountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionAccountMockMvc;

    private TransactionAccount transactionAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionAccount createEntity(EntityManager em) {
        TransactionAccount transactionAccount = new TransactionAccount()
            .accountName(DEFAULT_ACCOUNT_NAME)
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .transactionAccountType(DEFAULT_TRANSACTION_ACCOUNT_TYPE)
            .openingBalance(DEFAULT_OPENING_BALANCE);
        return transactionAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionAccount createUpdatedEntity(EntityManager em) {
        TransactionAccount transactionAccount = new TransactionAccount()
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .transactionAccountType(UPDATED_TRANSACTION_ACCOUNT_TYPE)
            .openingBalance(UPDATED_OPENING_BALANCE);
        return transactionAccount;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionAccountSearchRepository.deleteAll();
        assertThat(transactionAccountSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionAccount = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionAccount() throws Exception {
        int databaseSizeBeforeCreate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);
        restTransactionAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionAccount testTransactionAccount = transactionAccountList.get(transactionAccountList.size() - 1);
        assertThat(testTransactionAccount.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testTransactionAccount.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testTransactionAccount.getTransactionAccountType()).isEqualTo(DEFAULT_TRANSACTION_ACCOUNT_TYPE);
        assertThat(testTransactionAccount.getOpeningBalance()).isEqualByComparingTo(DEFAULT_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void createTransactionAccountWithExistingId() throws Exception {
        // Create the TransactionAccount with an existing ID
        transactionAccount.setId(1L);
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        int databaseSizeBeforeCreate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAccountNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        // set the field null
        transactionAccount.setAccountName(null);

        // Create the TransactionAccount, which fails.
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        restTransactionAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionAccountTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        // set the field null
        transactionAccount.setTransactionAccountType(null);

        // Create the TransactionAccount, which fails.
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        restTransactionAccountMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactionAccounts() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList
        restTransactionAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].transactionAccountType").value(hasItem(DEFAULT_TRANSACTION_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].openingBalance").value(hasItem(sameNumber(DEFAULT_OPENING_BALANCE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionAccountsWithEagerRelationshipsIsEnabled() throws Exception {
        when(transactionAccountServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionAccountMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transactionAccountServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransactionAccountsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transactionAccountServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransactionAccountMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transactionAccountRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransactionAccount() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get the transactionAccount
        restTransactionAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionAccount.getId().intValue()))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.transactionAccountType").value(DEFAULT_TRANSACTION_ACCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.openingBalance").value(sameNumber(DEFAULT_OPENING_BALANCE)));
    }

    @Test
    @Transactional
    void getTransactionAccountsByIdFiltering() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        Long id = transactionAccount.getId();

        defaultTransactionAccountShouldBeFound("id.equals=" + id);
        defaultTransactionAccountShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionAccountShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionAccountShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionAccountShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionAccountShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountName equals to DEFAULT_ACCOUNT_NAME
        defaultTransactionAccountShouldBeFound("accountName.equals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the transactionAccountList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultTransactionAccountShouldNotBeFound("accountName.equals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNameIsInShouldWork() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountName in DEFAULT_ACCOUNT_NAME or UPDATED_ACCOUNT_NAME
        defaultTransactionAccountShouldBeFound("accountName.in=" + DEFAULT_ACCOUNT_NAME + "," + UPDATED_ACCOUNT_NAME);

        // Get all the transactionAccountList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultTransactionAccountShouldNotBeFound("accountName.in=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountName is not null
        defaultTransactionAccountShouldBeFound("accountName.specified=true");

        // Get all the transactionAccountList where accountName is null
        defaultTransactionAccountShouldNotBeFound("accountName.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNameContainsSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountName contains DEFAULT_ACCOUNT_NAME
        defaultTransactionAccountShouldBeFound("accountName.contains=" + DEFAULT_ACCOUNT_NAME);

        // Get all the transactionAccountList where accountName contains UPDATED_ACCOUNT_NAME
        defaultTransactionAccountShouldNotBeFound("accountName.contains=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNameNotContainsSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountName does not contain DEFAULT_ACCOUNT_NAME
        defaultTransactionAccountShouldNotBeFound("accountName.doesNotContain=" + DEFAULT_ACCOUNT_NAME);

        // Get all the transactionAccountList where accountName does not contain UPDATED_ACCOUNT_NAME
        defaultTransactionAccountShouldBeFound("accountName.doesNotContain=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountNumber equals to DEFAULT_ACCOUNT_NUMBER
        defaultTransactionAccountShouldBeFound("accountNumber.equals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the transactionAccountList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultTransactionAccountShouldNotBeFound("accountNumber.equals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountNumber in DEFAULT_ACCOUNT_NUMBER or UPDATED_ACCOUNT_NUMBER
        defaultTransactionAccountShouldBeFound("accountNumber.in=" + DEFAULT_ACCOUNT_NUMBER + "," + UPDATED_ACCOUNT_NUMBER);

        // Get all the transactionAccountList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultTransactionAccountShouldNotBeFound("accountNumber.in=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountNumber is not null
        defaultTransactionAccountShouldBeFound("accountNumber.specified=true");

        // Get all the transactionAccountList where accountNumber is null
        defaultTransactionAccountShouldNotBeFound("accountNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountNumber contains DEFAULT_ACCOUNT_NUMBER
        defaultTransactionAccountShouldBeFound("accountNumber.contains=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the transactionAccountList where accountNumber contains UPDATED_ACCOUNT_NUMBER
        defaultTransactionAccountShouldNotBeFound("accountNumber.contains=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where accountNumber does not contain DEFAULT_ACCOUNT_NUMBER
        defaultTransactionAccountShouldNotBeFound("accountNumber.doesNotContain=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the transactionAccountList where accountNumber does not contain UPDATED_ACCOUNT_NUMBER
        defaultTransactionAccountShouldBeFound("accountNumber.doesNotContain=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByTransactionAccountTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where transactionAccountType equals to DEFAULT_TRANSACTION_ACCOUNT_TYPE
        defaultTransactionAccountShouldBeFound("transactionAccountType.equals=" + DEFAULT_TRANSACTION_ACCOUNT_TYPE);

        // Get all the transactionAccountList where transactionAccountType equals to UPDATED_TRANSACTION_ACCOUNT_TYPE
        defaultTransactionAccountShouldNotBeFound("transactionAccountType.equals=" + UPDATED_TRANSACTION_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByTransactionAccountTypeIsInShouldWork() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where transactionAccountType in DEFAULT_TRANSACTION_ACCOUNT_TYPE or UPDATED_TRANSACTION_ACCOUNT_TYPE
        defaultTransactionAccountShouldBeFound(
            "transactionAccountType.in=" + DEFAULT_TRANSACTION_ACCOUNT_TYPE + "," + UPDATED_TRANSACTION_ACCOUNT_TYPE
        );

        // Get all the transactionAccountList where transactionAccountType equals to UPDATED_TRANSACTION_ACCOUNT_TYPE
        defaultTransactionAccountShouldNotBeFound("transactionAccountType.in=" + UPDATED_TRANSACTION_ACCOUNT_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByTransactionAccountTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where transactionAccountType is not null
        defaultTransactionAccountShouldBeFound("transactionAccountType.specified=true");

        // Get all the transactionAccountList where transactionAccountType is null
        defaultTransactionAccountShouldNotBeFound("transactionAccountType.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance equals to DEFAULT_OPENING_BALANCE
        defaultTransactionAccountShouldBeFound("openingBalance.equals=" + DEFAULT_OPENING_BALANCE);

        // Get all the transactionAccountList where openingBalance equals to UPDATED_OPENING_BALANCE
        defaultTransactionAccountShouldNotBeFound("openingBalance.equals=" + UPDATED_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance in DEFAULT_OPENING_BALANCE or UPDATED_OPENING_BALANCE
        defaultTransactionAccountShouldBeFound("openingBalance.in=" + DEFAULT_OPENING_BALANCE + "," + UPDATED_OPENING_BALANCE);

        // Get all the transactionAccountList where openingBalance equals to UPDATED_OPENING_BALANCE
        defaultTransactionAccountShouldNotBeFound("openingBalance.in=" + UPDATED_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance is not null
        defaultTransactionAccountShouldBeFound("openingBalance.specified=true");

        // Get all the transactionAccountList where openingBalance is null
        defaultTransactionAccountShouldNotBeFound("openingBalance.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance is greater than or equal to DEFAULT_OPENING_BALANCE
        defaultTransactionAccountShouldBeFound("openingBalance.greaterThanOrEqual=" + DEFAULT_OPENING_BALANCE);

        // Get all the transactionAccountList where openingBalance is greater than or equal to UPDATED_OPENING_BALANCE
        defaultTransactionAccountShouldNotBeFound("openingBalance.greaterThanOrEqual=" + UPDATED_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance is less than or equal to DEFAULT_OPENING_BALANCE
        defaultTransactionAccountShouldBeFound("openingBalance.lessThanOrEqual=" + DEFAULT_OPENING_BALANCE);

        // Get all the transactionAccountList where openingBalance is less than or equal to SMALLER_OPENING_BALANCE
        defaultTransactionAccountShouldNotBeFound("openingBalance.lessThanOrEqual=" + SMALLER_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance is less than DEFAULT_OPENING_BALANCE
        defaultTransactionAccountShouldNotBeFound("openingBalance.lessThan=" + DEFAULT_OPENING_BALANCE);

        // Get all the transactionAccountList where openingBalance is less than UPDATED_OPENING_BALANCE
        defaultTransactionAccountShouldBeFound("openingBalance.lessThan=" + UPDATED_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void getAllTransactionAccountsByOpeningBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        // Get all the transactionAccountList where openingBalance is greater than DEFAULT_OPENING_BALANCE
        defaultTransactionAccountShouldNotBeFound("openingBalance.greaterThan=" + DEFAULT_OPENING_BALANCE);

        // Get all the transactionAccountList where openingBalance is greater than SMALLER_OPENING_BALANCE
        defaultTransactionAccountShouldBeFound("openingBalance.greaterThan=" + SMALLER_OPENING_BALANCE);
    }

    // @Test
    @Transactional
    void getAllTransactionAccountsByParentAccountIsEqualToSomething() throws Exception {
        TransactionAccount parentAccount;
        if (TestUtil.findAll(em, TransactionAccount.class).isEmpty()) {
            transactionAccountRepository.saveAndFlush(transactionAccount);
            parentAccount = TransactionAccountResourceIT.createEntity(em);
        } else {
            parentAccount = TestUtil.findAll(em, TransactionAccount.class).get(0);
        }
        em.persist(parentAccount);
        em.flush();
        transactionAccount.setParentAccount(parentAccount);
        transactionAccountRepository.saveAndFlush(transactionAccount);
        Long parentAccountId = parentAccount.getId();
        // Get all the transactionAccountList where parentAccount equals to parentAccountId
        defaultTransactionAccountShouldBeFound("parentAccountId.equals=" + parentAccountId);

        // Get all the transactionAccountList where parentAccount equals to (parentAccountId + 1)
        defaultTransactionAccountShouldNotBeFound("parentAccountId.equals=" + (parentAccountId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionAccountShouldBeFound(String filter) throws Exception {
        restTransactionAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].transactionAccountType").value(hasItem(DEFAULT_TRANSACTION_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].openingBalance").value(hasItem(sameNumber(DEFAULT_OPENING_BALANCE))));

        // Check, that the count call also returns 1
        restTransactionAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionAccountShouldNotBeFound(String filter) throws Exception {
        restTransactionAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactionAccount() throws Exception {
        // Get the transactionAccount
        restTransactionAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionAccount() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        transactionAccountSearchRepository.save(transactionAccount);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());

        // Update the transactionAccount
        TransactionAccount updatedTransactionAccount = transactionAccountRepository.findById(transactionAccount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionAccount are not directly saved in db
        em.detach(updatedTransactionAccount);
        updatedTransactionAccount
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .transactionAccountType(UPDATED_TRANSACTION_ACCOUNT_TYPE)
            .openingBalance(UPDATED_OPENING_BALANCE);
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(updatedTransactionAccount);

        restTransactionAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        TransactionAccount testTransactionAccount = transactionAccountList.get(transactionAccountList.size() - 1);
        assertThat(testTransactionAccount.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testTransactionAccount.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testTransactionAccount.getTransactionAccountType()).isEqualTo(UPDATED_TRANSACTION_ACCOUNT_TYPE);
        assertThat(testTransactionAccount.getOpeningBalance()).isEqualByComparingTo(UPDATED_OPENING_BALANCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionAccount> transactionAccountSearchList = IterableUtils.toList(transactionAccountSearchRepository.findAll());
                TransactionAccount testTransactionAccountSearch = transactionAccountSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionAccountSearch.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
                assertThat(testTransactionAccountSearch.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
                assertThat(testTransactionAccountSearch.getTransactionAccountType()).isEqualTo(UPDATED_TRANSACTION_ACCOUNT_TYPE);
                assertThat(testTransactionAccountSearch.getOpeningBalance()).isEqualByComparingTo(UPDATED_OPENING_BALANCE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransactionAccount() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        transactionAccount.setId(count.incrementAndGet());

        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionAccount() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        transactionAccount.setId(count.incrementAndGet());

        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionAccount() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        transactionAccount.setId(count.incrementAndGet());

        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionAccountWithPatch() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();

        // Update the transactionAccount using partial update
        TransactionAccount partialUpdatedTransactionAccount = new TransactionAccount();
        partialUpdatedTransactionAccount.setId(transactionAccount.getId());

        partialUpdatedTransactionAccount.transactionAccountType(UPDATED_TRANSACTION_ACCOUNT_TYPE).openingBalance(UPDATED_OPENING_BALANCE);

        restTransactionAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionAccount))
            )
            .andExpect(status().isOk());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        TransactionAccount testTransactionAccount = transactionAccountList.get(transactionAccountList.size() - 1);
        assertThat(testTransactionAccount.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testTransactionAccount.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testTransactionAccount.getTransactionAccountType()).isEqualTo(UPDATED_TRANSACTION_ACCOUNT_TYPE);
        assertThat(testTransactionAccount.getOpeningBalance()).isEqualByComparingTo(UPDATED_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void fullUpdateTransactionAccountWithPatch() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);

        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();

        // Update the transactionAccount using partial update
        TransactionAccount partialUpdatedTransactionAccount = new TransactionAccount();
        partialUpdatedTransactionAccount.setId(transactionAccount.getId());

        partialUpdatedTransactionAccount
            .accountName(UPDATED_ACCOUNT_NAME)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .transactionAccountType(UPDATED_TRANSACTION_ACCOUNT_TYPE)
            .openingBalance(UPDATED_OPENING_BALANCE);

        restTransactionAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionAccount))
            )
            .andExpect(status().isOk());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        TransactionAccount testTransactionAccount = transactionAccountList.get(transactionAccountList.size() - 1);
        assertThat(testTransactionAccount.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testTransactionAccount.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testTransactionAccount.getTransactionAccountType()).isEqualTo(UPDATED_TRANSACTION_ACCOUNT_TYPE);
        assertThat(testTransactionAccount.getOpeningBalance()).isEqualByComparingTo(UPDATED_OPENING_BALANCE);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionAccount() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        transactionAccount.setId(count.incrementAndGet());

        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionAccountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionAccount() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        transactionAccount.setId(count.incrementAndGet());

        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionAccount() throws Exception {
        int databaseSizeBeforeUpdate = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        transactionAccount.setId(count.incrementAndGet());

        // Create the TransactionAccount
        TransactionAccountDTO transactionAccountDTO = transactionAccountMapper.toDto(transactionAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionAccountMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionAccount in the database
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransactionAccount() throws Exception {
        // Initialize the database
        transactionAccountRepository.saveAndFlush(transactionAccount);
        transactionAccountRepository.save(transactionAccount);
        transactionAccountSearchRepository.save(transactionAccount);

        int databaseSizeBeforeDelete = transactionAccountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionAccount
        restTransactionAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionAccount> transactionAccountList = transactionAccountRepository.findAll();
        assertThat(transactionAccountList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionAccountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransactionAccount() throws Exception {
        // Initialize the database
        transactionAccount = transactionAccountRepository.saveAndFlush(transactionAccount);
        transactionAccountSearchRepository.save(transactionAccount);

        // Search the transactionAccount
        restTransactionAccountMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].transactionAccountType").value(hasItem(DEFAULT_TRANSACTION_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].openingBalance").value(hasItem(sameNumber(DEFAULT_OPENING_BALANCE))));
    }
}
