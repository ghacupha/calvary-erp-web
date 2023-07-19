package io.github.calvary.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.calvary.IntegrationTest;
import io.github.calvary.domain.AccountingEvent;
import io.github.calvary.domain.Dealer;
import io.github.calvary.domain.EventType;
import io.github.calvary.repository.AccountingEventRepository;
import io.github.calvary.repository.search.AccountingEventSearchRepository;
import io.github.calvary.service.AccountingEventService;
import io.github.calvary.service.dto.AccountingEventDTO;
import io.github.calvary.service.mapper.AccountingEventMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AccountingEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AccountingEventResourceIT {

    private static final LocalDate DEFAULT_EVENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EVENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EVENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/accounting-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/accounting-events";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AccountingEventRepository accountingEventRepository;

    @Mock
    private AccountingEventRepository accountingEventRepositoryMock;

    @Autowired
    private AccountingEventMapper accountingEventMapper;

    @Mock
    private AccountingEventService accountingEventServiceMock;

    @Autowired
    private AccountingEventSearchRepository accountingEventSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountingEventMockMvc;

    private AccountingEvent accountingEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountingEvent createEntity(EntityManager em) {
        AccountingEvent accountingEvent = new AccountingEvent().eventDate(DEFAULT_EVENT_DATE);
        // Add required entity
        Dealer dealer;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = TestUtil.findAll(em, Dealer.class).get(0);
        }
        accountingEvent.setDealer(dealer);
        return accountingEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountingEvent createUpdatedEntity(EntityManager em) {
        AccountingEvent accountingEvent = new AccountingEvent().eventDate(UPDATED_EVENT_DATE);
        // Add required entity
        Dealer dealer;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            dealer = DealerResourceIT.createUpdatedEntity(em);
            em.persist(dealer);
            em.flush();
        } else {
            dealer = TestUtil.findAll(em, Dealer.class).get(0);
        }
        accountingEvent.setDealer(dealer);
        return accountingEvent;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        accountingEventSearchRepository.deleteAll();
        assertThat(accountingEventSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        accountingEvent = createEntity(em);
    }

    @Test
    @Transactional
    void createAccountingEvent() throws Exception {
        int databaseSizeBeforeCreate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);
        restAccountingEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isCreated());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        AccountingEvent testAccountingEvent = accountingEventList.get(accountingEventList.size() - 1);
        assertThat(testAccountingEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
    }

    @Test
    @Transactional
    void createAccountingEventWithExistingId() throws Exception {
        // Create the AccountingEvent with an existing ID
        accountingEvent.setId(1L);
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        int databaseSizeBeforeCreate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountingEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEventDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        // set the field null
        accountingEvent.setEventDate(null);

        // Create the AccountingEvent, which fails.
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        restAccountingEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAccountingEvents() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList
        restAccountingEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountingEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAccountingEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(accountingEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAccountingEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(accountingEventServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAccountingEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(accountingEventServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAccountingEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(accountingEventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAccountingEvent() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get the accountingEvent
        restAccountingEventMockMvc
            .perform(get(ENTITY_API_URL_ID, accountingEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountingEvent.getId().intValue()))
            .andExpect(jsonPath("$.eventDate").value(DEFAULT_EVENT_DATE.toString()));
    }

    @Test
    @Transactional
    void getAccountingEventsByIdFiltering() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        Long id = accountingEvent.getId();

        defaultAccountingEventShouldBeFound("id.equals=" + id);
        defaultAccountingEventShouldNotBeFound("id.notEquals=" + id);

        defaultAccountingEventShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAccountingEventShouldNotBeFound("id.greaterThan=" + id);

        defaultAccountingEventShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAccountingEventShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsEqualToSomething() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate equals to DEFAULT_EVENT_DATE
        defaultAccountingEventShouldBeFound("eventDate.equals=" + DEFAULT_EVENT_DATE);

        // Get all the accountingEventList where eventDate equals to UPDATED_EVENT_DATE
        defaultAccountingEventShouldNotBeFound("eventDate.equals=" + UPDATED_EVENT_DATE);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsInShouldWork() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate in DEFAULT_EVENT_DATE or UPDATED_EVENT_DATE
        defaultAccountingEventShouldBeFound("eventDate.in=" + DEFAULT_EVENT_DATE + "," + UPDATED_EVENT_DATE);

        // Get all the accountingEventList where eventDate equals to UPDATED_EVENT_DATE
        defaultAccountingEventShouldNotBeFound("eventDate.in=" + UPDATED_EVENT_DATE);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate is not null
        defaultAccountingEventShouldBeFound("eventDate.specified=true");

        // Get all the accountingEventList where eventDate is null
        defaultAccountingEventShouldNotBeFound("eventDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate is greater than or equal to DEFAULT_EVENT_DATE
        defaultAccountingEventShouldBeFound("eventDate.greaterThanOrEqual=" + DEFAULT_EVENT_DATE);

        // Get all the accountingEventList where eventDate is greater than or equal to UPDATED_EVENT_DATE
        defaultAccountingEventShouldNotBeFound("eventDate.greaterThanOrEqual=" + UPDATED_EVENT_DATE);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate is less than or equal to DEFAULT_EVENT_DATE
        defaultAccountingEventShouldBeFound("eventDate.lessThanOrEqual=" + DEFAULT_EVENT_DATE);

        // Get all the accountingEventList where eventDate is less than or equal to SMALLER_EVENT_DATE
        defaultAccountingEventShouldNotBeFound("eventDate.lessThanOrEqual=" + SMALLER_EVENT_DATE);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsLessThanSomething() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate is less than DEFAULT_EVENT_DATE
        defaultAccountingEventShouldNotBeFound("eventDate.lessThan=" + DEFAULT_EVENT_DATE);

        // Get all the accountingEventList where eventDate is less than UPDATED_EVENT_DATE
        defaultAccountingEventShouldBeFound("eventDate.lessThan=" + UPDATED_EVENT_DATE);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        // Get all the accountingEventList where eventDate is greater than DEFAULT_EVENT_DATE
        defaultAccountingEventShouldNotBeFound("eventDate.greaterThan=" + DEFAULT_EVENT_DATE);

        // Get all the accountingEventList where eventDate is greater than SMALLER_EVENT_DATE
        defaultAccountingEventShouldBeFound("eventDate.greaterThan=" + SMALLER_EVENT_DATE);
    }

    @Test
    @Transactional
    void getAllAccountingEventsByEventTypeIsEqualToSomething() throws Exception {
        EventType eventType;
        if (TestUtil.findAll(em, EventType.class).isEmpty()) {
            accountingEventRepository.saveAndFlush(accountingEvent);
            eventType = EventTypeResourceIT.createEntity(em);
        } else {
            eventType = TestUtil.findAll(em, EventType.class).get(0);
        }
        em.persist(eventType);
        em.flush();
        accountingEvent.setEventType(eventType);
        accountingEventRepository.saveAndFlush(accountingEvent);
        Long eventTypeId = eventType.getId();
        // Get all the accountingEventList where eventType equals to eventTypeId
        defaultAccountingEventShouldBeFound("eventTypeId.equals=" + eventTypeId);

        // Get all the accountingEventList where eventType equals to (eventTypeId + 1)
        defaultAccountingEventShouldNotBeFound("eventTypeId.equals=" + (eventTypeId + 1));
    }

    @Test
    @Transactional
    void getAllAccountingEventsByDealerIsEqualToSomething() throws Exception {
        Dealer dealer;
        if (TestUtil.findAll(em, Dealer.class).isEmpty()) {
            accountingEventRepository.saveAndFlush(accountingEvent);
            dealer = DealerResourceIT.createEntity(em);
        } else {
            dealer = TestUtil.findAll(em, Dealer.class).get(0);
        }
        em.persist(dealer);
        em.flush();
        accountingEvent.setDealer(dealer);
        accountingEventRepository.saveAndFlush(accountingEvent);
        Long dealerId = dealer.getId();
        // Get all the accountingEventList where dealer equals to dealerId
        defaultAccountingEventShouldBeFound("dealerId.equals=" + dealerId);

        // Get all the accountingEventList where dealer equals to (dealerId + 1)
        defaultAccountingEventShouldNotBeFound("dealerId.equals=" + (dealerId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAccountingEventShouldBeFound(String filter) throws Exception {
        restAccountingEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountingEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())));

        // Check, that the count call also returns 1
        restAccountingEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAccountingEventShouldNotBeFound(String filter) throws Exception {
        restAccountingEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAccountingEventMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAccountingEvent() throws Exception {
        // Get the accountingEvent
        restAccountingEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccountingEvent() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        accountingEventSearchRepository.save(accountingEvent);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());

        // Update the accountingEvent
        AccountingEvent updatedAccountingEvent = accountingEventRepository.findById(accountingEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAccountingEvent are not directly saved in db
        em.detach(updatedAccountingEvent);
        updatedAccountingEvent.eventDate(UPDATED_EVENT_DATE);
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(updatedAccountingEvent);

        restAccountingEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountingEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        AccountingEvent testAccountingEvent = accountingEventList.get(accountingEventList.size() - 1);
        assertThat(testAccountingEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AccountingEvent> accountingEventSearchList = IterableUtils.toList(accountingEventSearchRepository.findAll());
                AccountingEvent testAccountingEventSearch = accountingEventSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testAccountingEventSearch.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingAccountingEvent() throws Exception {
        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        accountingEvent.setId(count.incrementAndGet());

        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountingEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountingEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountingEvent() throws Exception {
        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        accountingEvent.setId(count.incrementAndGet());

        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountingEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountingEvent() throws Exception {
        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        accountingEvent.setId(count.incrementAndGet());

        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountingEventMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAccountingEventWithPatch() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();

        // Update the accountingEvent using partial update
        AccountingEvent partialUpdatedAccountingEvent = new AccountingEvent();
        partialUpdatedAccountingEvent.setId(accountingEvent.getId());

        restAccountingEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountingEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountingEvent))
            )
            .andExpect(status().isOk());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        AccountingEvent testAccountingEvent = accountingEventList.get(accountingEventList.size() - 1);
        assertThat(testAccountingEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
    }

    @Test
    @Transactional
    void fullUpdateAccountingEventWithPatch() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);

        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();

        // Update the accountingEvent using partial update
        AccountingEvent partialUpdatedAccountingEvent = new AccountingEvent();
        partialUpdatedAccountingEvent.setId(accountingEvent.getId());

        partialUpdatedAccountingEvent.eventDate(UPDATED_EVENT_DATE);

        restAccountingEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountingEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountingEvent))
            )
            .andExpect(status().isOk());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        AccountingEvent testAccountingEvent = accountingEventList.get(accountingEventList.size() - 1);
        assertThat(testAccountingEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingAccountingEvent() throws Exception {
        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        accountingEvent.setId(count.incrementAndGet());

        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountingEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountingEventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountingEvent() throws Exception {
        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        accountingEvent.setId(count.incrementAndGet());

        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountingEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountingEvent() throws Exception {
        int databaseSizeBeforeUpdate = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        accountingEvent.setId(count.incrementAndGet());

        // Create the AccountingEvent
        AccountingEventDTO accountingEventDTO = accountingEventMapper.toDto(accountingEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountingEventMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountingEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountingEvent in the database
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAccountingEvent() throws Exception {
        // Initialize the database
        accountingEventRepository.saveAndFlush(accountingEvent);
        accountingEventRepository.save(accountingEvent);
        accountingEventSearchRepository.save(accountingEvent);

        int databaseSizeBeforeDelete = accountingEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the accountingEvent
        restAccountingEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountingEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AccountingEvent> accountingEventList = accountingEventRepository.findAll();
        assertThat(accountingEventList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(accountingEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAccountingEvent() throws Exception {
        // Initialize the database
        accountingEvent = accountingEventRepository.saveAndFlush(accountingEvent);
        accountingEventSearchRepository.save(accountingEvent);

        // Search the accountingEvent
        restAccountingEventMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + accountingEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountingEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())));
    }
}
