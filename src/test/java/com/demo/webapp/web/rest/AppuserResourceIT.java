package com.demo.webapp.web.rest;

import static com.demo.webapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.webapp.IntegrationTest;
import com.demo.webapp.domain.Appuser;
import com.demo.webapp.domain.User;
import com.demo.webapp.repository.AppuserRepository;
import com.demo.webapp.service.criteria.AppuserCriteria;
import com.demo.webapp.service.dto.AppuserDTO;
import com.demo.webapp.service.mapper.AppuserMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppuserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppuserResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_INSURANCE_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_INSURANCE_COMPANY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);
    private static final BigDecimal SMALLER_BALANCE = new BigDecimal(1 - 1);

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/appusers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppuserRepository appuserRepository;

    @Autowired
    private AppuserMapper appuserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppuserMockMvc;

    private Appuser appuser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appuser createEntity(EntityManager em) {
        Appuser appuser = new Appuser()
            .date(DEFAULT_DATE)
            .insuranceCompany(DEFAULT_INSURANCE_COMPANY)
            .balance(DEFAULT_BALANCE)
            .country(DEFAULT_COUNTRY);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        appuser.setUser(user);
        return appuser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appuser createUpdatedEntity(EntityManager em) {
        Appuser appuser = new Appuser()
            .date(UPDATED_DATE)
            .insuranceCompany(UPDATED_INSURANCE_COMPANY)
            .balance(UPDATED_BALANCE)
            .country(UPDATED_COUNTRY);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        appuser.setUser(user);
        return appuser;
    }

    @BeforeEach
    public void initTest() {
        appuser = createEntity(em);
    }

    @Test
    @Transactional
    void createAppuser() throws Exception {
        int databaseSizeBeforeCreate = appuserRepository.findAll().size();
        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);
        restAppuserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeCreate + 1);
        Appuser testAppuser = appuserList.get(appuserList.size() - 1);
        assertThat(testAppuser.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAppuser.getInsuranceCompany()).isEqualTo(DEFAULT_INSURANCE_COMPANY);
        assertThat(testAppuser.getBalance()).isEqualByComparingTo(DEFAULT_BALANCE);
        assertThat(testAppuser.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    void createAppuserWithExistingId() throws Exception {
        // Create the Appuser with an existing ID
        appuser.setId(1L);
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        int databaseSizeBeforeCreate = appuserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppuserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = appuserRepository.findAll().size();
        // set the field null
        appuser.setDate(null);

        // Create the Appuser, which fails.
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        restAppuserMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isBadRequest());

        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppusers() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList
        restAppuserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appuser.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].insuranceCompany").value(hasItem(DEFAULT_INSURANCE_COMPANY)))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(sameNumber(DEFAULT_BALANCE))))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @Test
    @Transactional
    void getAppuser() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get the appuser
        restAppuserMockMvc
            .perform(get(ENTITY_API_URL_ID, appuser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appuser.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.insuranceCompany").value(DEFAULT_INSURANCE_COMPANY))
            .andExpect(jsonPath("$.balance").value(sameNumber(DEFAULT_BALANCE)))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    void getAppusersByIdFiltering() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        Long id = appuser.getId();

        defaultAppuserShouldBeFound("id.equals=" + id);
        defaultAppuserShouldNotBeFound("id.notEquals=" + id);

        defaultAppuserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAppuserShouldNotBeFound("id.greaterThan=" + id);

        defaultAppuserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAppuserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppusersByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where date equals to DEFAULT_DATE
        defaultAppuserShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the appuserList where date equals to UPDATED_DATE
        defaultAppuserShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllAppusersByDateIsInShouldWork() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where date in DEFAULT_DATE or UPDATED_DATE
        defaultAppuserShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the appuserList where date equals to UPDATED_DATE
        defaultAppuserShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllAppusersByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where date is not null
        defaultAppuserShouldBeFound("date.specified=true");

        // Get all the appuserList where date is null
        defaultAppuserShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllAppusersByInsuranceCompanyIsEqualToSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where insuranceCompany equals to DEFAULT_INSURANCE_COMPANY
        defaultAppuserShouldBeFound("insuranceCompany.equals=" + DEFAULT_INSURANCE_COMPANY);

        // Get all the appuserList where insuranceCompany equals to UPDATED_INSURANCE_COMPANY
        defaultAppuserShouldNotBeFound("insuranceCompany.equals=" + UPDATED_INSURANCE_COMPANY);
    }

    @Test
    @Transactional
    void getAllAppusersByInsuranceCompanyIsInShouldWork() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where insuranceCompany in DEFAULT_INSURANCE_COMPANY or UPDATED_INSURANCE_COMPANY
        defaultAppuserShouldBeFound("insuranceCompany.in=" + DEFAULT_INSURANCE_COMPANY + "," + UPDATED_INSURANCE_COMPANY);

        // Get all the appuserList where insuranceCompany equals to UPDATED_INSURANCE_COMPANY
        defaultAppuserShouldNotBeFound("insuranceCompany.in=" + UPDATED_INSURANCE_COMPANY);
    }

    @Test
    @Transactional
    void getAllAppusersByInsuranceCompanyIsNullOrNotNull() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where insuranceCompany is not null
        defaultAppuserShouldBeFound("insuranceCompany.specified=true");

        // Get all the appuserList where insuranceCompany is null
        defaultAppuserShouldNotBeFound("insuranceCompany.specified=false");
    }

    @Test
    @Transactional
    void getAllAppusersByInsuranceCompanyContainsSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where insuranceCompany contains DEFAULT_INSURANCE_COMPANY
        defaultAppuserShouldBeFound("insuranceCompany.contains=" + DEFAULT_INSURANCE_COMPANY);

        // Get all the appuserList where insuranceCompany contains UPDATED_INSURANCE_COMPANY
        defaultAppuserShouldNotBeFound("insuranceCompany.contains=" + UPDATED_INSURANCE_COMPANY);
    }

    @Test
    @Transactional
    void getAllAppusersByInsuranceCompanyNotContainsSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where insuranceCompany does not contain DEFAULT_INSURANCE_COMPANY
        defaultAppuserShouldNotBeFound("insuranceCompany.doesNotContain=" + DEFAULT_INSURANCE_COMPANY);

        // Get all the appuserList where insuranceCompany does not contain UPDATED_INSURANCE_COMPANY
        defaultAppuserShouldBeFound("insuranceCompany.doesNotContain=" + UPDATED_INSURANCE_COMPANY);
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsEqualToSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance equals to DEFAULT_BALANCE
        defaultAppuserShouldBeFound("balance.equals=" + DEFAULT_BALANCE);

        // Get all the appuserList where balance equals to UPDATED_BALANCE
        defaultAppuserShouldNotBeFound("balance.equals=" + UPDATED_BALANCE);
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsInShouldWork() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance in DEFAULT_BALANCE or UPDATED_BALANCE
        defaultAppuserShouldBeFound("balance.in=" + DEFAULT_BALANCE + "," + UPDATED_BALANCE);

        // Get all the appuserList where balance equals to UPDATED_BALANCE
        defaultAppuserShouldNotBeFound("balance.in=" + UPDATED_BALANCE);
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance is not null
        defaultAppuserShouldBeFound("balance.specified=true");

        // Get all the appuserList where balance is null
        defaultAppuserShouldNotBeFound("balance.specified=false");
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance is greater than or equal to DEFAULT_BALANCE
        defaultAppuserShouldBeFound("balance.greaterThanOrEqual=" + DEFAULT_BALANCE);

        // Get all the appuserList where balance is greater than or equal to UPDATED_BALANCE
        defaultAppuserShouldNotBeFound("balance.greaterThanOrEqual=" + UPDATED_BALANCE);
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance is less than or equal to DEFAULT_BALANCE
        defaultAppuserShouldBeFound("balance.lessThanOrEqual=" + DEFAULT_BALANCE);

        // Get all the appuserList where balance is less than or equal to SMALLER_BALANCE
        defaultAppuserShouldNotBeFound("balance.lessThanOrEqual=" + SMALLER_BALANCE);
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsLessThanSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance is less than DEFAULT_BALANCE
        defaultAppuserShouldNotBeFound("balance.lessThan=" + DEFAULT_BALANCE);

        // Get all the appuserList where balance is less than UPDATED_BALANCE
        defaultAppuserShouldBeFound("balance.lessThan=" + UPDATED_BALANCE);
    }

    @Test
    @Transactional
    void getAllAppusersByBalanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where balance is greater than DEFAULT_BALANCE
        defaultAppuserShouldNotBeFound("balance.greaterThan=" + DEFAULT_BALANCE);

        // Get all the appuserList where balance is greater than SMALLER_BALANCE
        defaultAppuserShouldBeFound("balance.greaterThan=" + SMALLER_BALANCE);
    }

    @Test
    @Transactional
    void getAllAppusersByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where country equals to DEFAULT_COUNTRY
        defaultAppuserShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the appuserList where country equals to UPDATED_COUNTRY
        defaultAppuserShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllAppusersByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultAppuserShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the appuserList where country equals to UPDATED_COUNTRY
        defaultAppuserShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllAppusersByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where country is not null
        defaultAppuserShouldBeFound("country.specified=true");

        // Get all the appuserList where country is null
        defaultAppuserShouldNotBeFound("country.specified=false");
    }

    @Test
    @Transactional
    void getAllAppusersByCountryContainsSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where country contains DEFAULT_COUNTRY
        defaultAppuserShouldBeFound("country.contains=" + DEFAULT_COUNTRY);

        // Get all the appuserList where country contains UPDATED_COUNTRY
        defaultAppuserShouldNotBeFound("country.contains=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllAppusersByCountryNotContainsSomething() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        // Get all the appuserList where country does not contain DEFAULT_COUNTRY
        defaultAppuserShouldNotBeFound("country.doesNotContain=" + DEFAULT_COUNTRY);

        // Get all the appuserList where country does not contain UPDATED_COUNTRY
        defaultAppuserShouldBeFound("country.doesNotContain=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllAppusersByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = appuser.getUser();
        appuserRepository.saveAndFlush(appuser);
        Long userId = user.getId();

        // Get all the appuserList where user equals to userId
        defaultAppuserShouldBeFound("userId.equals=" + userId);

        // Get all the appuserList where user equals to (userId + 1)
        defaultAppuserShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppuserShouldBeFound(String filter) throws Exception {
        restAppuserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appuser.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].insuranceCompany").value(hasItem(DEFAULT_INSURANCE_COMPANY)))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(sameNumber(DEFAULT_BALANCE))))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));

        // Check, that the count call also returns 1
        restAppuserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppuserShouldNotBeFound(String filter) throws Exception {
        restAppuserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppuserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppuser() throws Exception {
        // Get the appuser
        restAppuserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppuser() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();

        // Update the appuser
        Appuser updatedAppuser = appuserRepository.findById(appuser.getId()).get();
        // Disconnect from session so that the updates on updatedAppuser are not directly saved in db
        em.detach(updatedAppuser);
        updatedAppuser.date(UPDATED_DATE).insuranceCompany(UPDATED_INSURANCE_COMPANY).balance(UPDATED_BALANCE).country(UPDATED_COUNTRY);
        AppuserDTO appuserDTO = appuserMapper.toDto(updatedAppuser);

        restAppuserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appuserDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isOk());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
        Appuser testAppuser = appuserList.get(appuserList.size() - 1);
        assertThat(testAppuser.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAppuser.getInsuranceCompany()).isEqualTo(UPDATED_INSURANCE_COMPANY);
        assertThat(testAppuser.getBalance()).isEqualByComparingTo(UPDATED_BALANCE);
        assertThat(testAppuser.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void putNonExistingAppuser() throws Exception {
        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();
        appuser.setId(count.incrementAndGet());

        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppuserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appuserDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppuser() throws Exception {
        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();
        appuser.setId(count.incrementAndGet());

        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppuserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppuser() throws Exception {
        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();
        appuser.setId(count.incrementAndGet());

        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppuserMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppuserWithPatch() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();

        // Update the appuser using partial update
        Appuser partialUpdatedAppuser = new Appuser();
        partialUpdatedAppuser.setId(appuser.getId());

        partialUpdatedAppuser.insuranceCompany(UPDATED_INSURANCE_COMPANY).balance(UPDATED_BALANCE);

        restAppuserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppuser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppuser))
            )
            .andExpect(status().isOk());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
        Appuser testAppuser = appuserList.get(appuserList.size() - 1);
        assertThat(testAppuser.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAppuser.getInsuranceCompany()).isEqualTo(UPDATED_INSURANCE_COMPANY);
        assertThat(testAppuser.getBalance()).isEqualByComparingTo(UPDATED_BALANCE);
        assertThat(testAppuser.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    void fullUpdateAppuserWithPatch() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();

        // Update the appuser using partial update
        Appuser partialUpdatedAppuser = new Appuser();
        partialUpdatedAppuser.setId(appuser.getId());

        partialUpdatedAppuser
            .date(UPDATED_DATE)
            .insuranceCompany(UPDATED_INSURANCE_COMPANY)
            .balance(UPDATED_BALANCE)
            .country(UPDATED_COUNTRY);

        restAppuserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppuser.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppuser))
            )
            .andExpect(status().isOk());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
        Appuser testAppuser = appuserList.get(appuserList.size() - 1);
        assertThat(testAppuser.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAppuser.getInsuranceCompany()).isEqualTo(UPDATED_INSURANCE_COMPANY);
        assertThat(testAppuser.getBalance()).isEqualByComparingTo(UPDATED_BALANCE);
        assertThat(testAppuser.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void patchNonExistingAppuser() throws Exception {
        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();
        appuser.setId(count.incrementAndGet());

        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppuserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appuserDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppuser() throws Exception {
        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();
        appuser.setId(count.incrementAndGet());

        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppuserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppuser() throws Exception {
        int databaseSizeBeforeUpdate = appuserRepository.findAll().size();
        appuser.setId(count.incrementAndGet());

        // Create the Appuser
        AppuserDTO appuserDTO = appuserMapper.toDto(appuser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppuserMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appuserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appuser in the database
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppuser() throws Exception {
        // Initialize the database
        appuserRepository.saveAndFlush(appuser);

        int databaseSizeBeforeDelete = appuserRepository.findAll().size();

        // Delete the appuser
        restAppuserMockMvc
            .perform(delete(ENTITY_API_URL_ID, appuser.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Appuser> appuserList = appuserRepository.findAll();
        assertThat(appuserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
