package com.scheduler.scheduler.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.scheduler.dto.auth.LoginDTO;
import com.scheduler.scheduler.dto.schedule.CreateScheduleDTO;
import com.scheduler.scheduler.dto.schedule.ScheduleDTO;
import com.scheduler.scheduler.dto.shift.CreateShiftDTO;
import com.scheduler.scheduler.dto.user.UserDetailsDTO;
import com.scheduler.scheduler.model.Organization;
import com.scheduler.scheduler.model.Role;
import com.scheduler.scheduler.model.User;
import com.scheduler.scheduler.repository.OrganizationRepository;
import com.scheduler.scheduler.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ScheduleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String managerToken;
    private String employeeToken;
    private Long organizationId;
    private Long managerId;
    private Long employeeId;

    private static final LocalDateTime MON = LocalDateTime.of(2026, 6, 22, 0, 0);
    private static final LocalDateTime TUE = LocalDateTime.of(2026, 6, 23, 0, 0);
    private static final LocalDateTime WED = LocalDateTime.of(2026, 6, 24, 0, 0);
    private static final LocalDateTime THU = LocalDateTime.of(2026, 6, 25, 0, 0);
    private static final LocalDateTime FRI = LocalDateTime.of(2026, 6, 26, 0, 0);
    private static final LocalDateTime WEEK_START = MON;
    private static final LocalDateTime WEEK_END   = LocalDateTime.of(2026, 6, 28, 23, 59, 59);

    @BeforeEach
    void setUp() throws Exception {
        Organization org = new Organization();
        org.setName("Test Org");
        org.setActive(true);
        Organization savedOrg = organizationRepository.save(org);
        organizationId = savedOrg.getId();

        User manager = new User();
        manager.setFirstName("Manager");
        manager.setLastName("Test");
        manager.setEmail("manager@test.com");
        manager.setPassword(passwordEncoder.encode("password123"));
        manager.setRole(Role.MANAGER);
        manager.setOrganization(savedOrg);
        User savedManager = userRepository.save(manager);
        managerId = savedManager.getId();

        org.setOwner(savedManager);
        organizationRepository.save(org);

        User employee = new User();
        employee.setFirstName("Employee");
        employee.setLastName("Test");
        employee.setEmail("employee@test.com");
        employee.setPassword(passwordEncoder.encode("password123"));
        employee.setRole(Role.EMPLOYEE);
        employee.setOrganization(savedOrg);
        User savedEmployee = userRepository.save(employee);
        employeeId = savedEmployee.getId();

        managerToken  = login("manager@test.com",  "password123");
        employeeToken = login("employee@test.com", "password123");
    }

    private String login(String email, String password) throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private CreateScheduleDTO buildValidSchedule() {
        CreateShiftDTO shift = new CreateShiftDTO();
        shift.setUserId(employeeId);
        shift.setDate(MON);
        shift.setStartTime(MON.withHour(8));
        shift.setEndTime(MON.withHour(16));

        CreateScheduleDTO dto = new CreateScheduleDTO();
        dto.setWeekStart(WEEK_START);
        dto.setWeekEnd(WEEK_END);
        dto.setCreatedBy_id(managerId);
        dto.setOrganizationId(organizationId);
        dto.setShifts(List.of(shift));
        return dto;
    }

    // -------------------------------------------------------------------------
    // Tworzenie grafiku — happy path
    // -------------------------------------------------------------------------

    @Test
    void createSchedule_shouldReturn201_whenScheduleIsValid() throws Exception {
        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.organizationId").value(organizationId));
    }

    @Test
    void createSchedule_shouldReturn201_withMultipleShifts() throws Exception {
        CreateShiftDTO mon = buildShift(employeeId, MON, 8, 16);
        CreateShiftDTO tue = buildShift(employeeId, TUE, 8, 16);
        CreateShiftDTO wed = buildShift(employeeId, WED, 8, 16);

        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(mon, tue, wed));

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    // -------------------------------------------------------------------------
    // Tworzenie grafiku — walidacja
    // -------------------------------------------------------------------------

    @Test
    void createSchedule_shouldReturn4xx_whenShiftExceeds8hDaily() throws Exception {
        CreateShiftDTO shift = buildShift(employeeId, MON, 6, 17); // 11h
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(shift));

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createSchedule_shouldReturn4xx_whenWeekStartAfterWeekEnd() throws Exception {
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setWeekStart(WEEK_END);
        dto.setWeekEnd(WEEK_START);

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createSchedule_shouldReturn4xx_whenShiftOutsideScheduleRange() throws Exception {
        CreateShiftDTO shift = buildShift(employeeId, MON.minusDays(1), 8, 16);
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(shift));

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createSchedule_shouldReturn4xx_whenShiftsOverlap() throws Exception {
        CreateShiftDTO s1 = buildShift(employeeId, MON, 8, 16);
        CreateShiftDTO s2 = buildShift(employeeId, MON, 14, 22);
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(s1, s2));

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // Tworzenie grafiku — security
    // -------------------------------------------------------------------------

    @Test
    void createSchedule_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSchedule_shouldReturn403_whenCalledByEmployee() throws Exception {
        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------------
    // WorkingHours i nadgodziny
    // -------------------------------------------------------------------------

    @Test
    void createSchedule_shouldCalculateWorkingHoursForEmployee() throws Exception {
        // 5 zmian x 8h = 40h, target tygodnia dla zwykłego tygodnia roboczego = 40h, overtime = 0
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(
                buildShift(employeeId, MON, 8, 16),
                buildShift(employeeId, TUE, 8, 16),
                buildShift(employeeId, WED, 8, 16),
                buildShift(employeeId, THU, 8, 16),
                buildShift(employeeId, FRI, 8, 16)
        ));

        MvcResult result = mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDTO scheduleDTO = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);

        // Pobierz szczegóły usera i sprawdź WorkingHours
        MvcResult userResult = mockMvc.perform(get("/api/user/" + employeeId + "/details")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailsDTO userDetails = objectMapper.readValue(userResult.getResponse().getContentAsString(), UserDetailsDTO.class);
        assertThat(userDetails.getWorkingHoursList()).isNotEmpty();

        float totalHours = userDetails.getWorkingHoursList().stream()
                .map(wh -> wh.getTotalHours())
                .reduce(0f, Float::sum);
        assertThat(totalHours).isEqualTo(40f);
    }

    @Test
    void createSchedule_shouldCalculatePositiveOvertimeHours() throws Exception {
        // 6 zmian x 8h = 48h, w 5-dniowym tygodniu target = 40h, overtime = 8h
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(
                buildShift(employeeId, MON, 8, 16),
                buildShift(employeeId, TUE, 8, 16),
                buildShift(employeeId, WED, 8, 16),
                buildShift(employeeId, THU, 8, 16),
                buildShift(employeeId, FRI, 8, 16),
                buildShift(employeeId, SAT(), 8, 16)
        ));

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        MvcResult userResult = mockMvc.perform(get("/api/user/" + employeeId + "/details")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailsDTO userDetails = objectMapper.readValue(userResult.getResponse().getContentAsString(), UserDetailsDTO.class);
        float overtime = userDetails.getWorkingHoursList().stream()
                .map(wh -> wh.getOvertimeHours())
                .reduce(0f, Float::sum);
        assertThat(overtime).isGreaterThan(0f);
    }

    @Test
    void createSchedule_shouldCalculateNegativeOvertimeHours_whenEmployeeWorksLessThanTarget() throws Exception {
        // 3 x 8h = 24h, target = 40h, overtime = -16h
        CreateScheduleDTO dto = buildValidSchedule();
        dto.setShifts(List.of(
                buildShift(employeeId, MON, 8, 16),
                buildShift(employeeId, TUE, 8, 16),
                buildShift(employeeId, WED, 8, 16)
        ));

        mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        MvcResult userResult = mockMvc.perform(get("/api/user/" + employeeId + "/details")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andReturn();

        UserDetailsDTO userDetails = objectMapper.readValue(userResult.getResponse().getContentAsString(), UserDetailsDTO.class);
        float overtime = userDetails.getWorkingHoursList().stream()
                .map(wh -> wh.getOvertimeHours())
                .reduce(0f, Float::sum);
        assertThat(overtime).isLessThan(0f);
    }

    // -------------------------------------------------------------------------
    // Pobieranie grafiku — routing po roli
    // -------------------------------------------------------------------------

    @Test
    void getSchedule_shouldReturn200_whenCalledByManager() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDTO scheduleDTO = objectMapper.readValue(created.getResponse().getContentAsString(), ScheduleDTO.class);

        mockMvc.perform(get("/api/schedule/" + scheduleDTO.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduleDTO.getId()));
    }

    @Test
    void getSchedule_shouldReturn200_whenCalledByEmployee() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDTO scheduleDTO = objectMapper.readValue(created.getResponse().getContentAsString(), ScheduleDTO.class);

        mockMvc.perform(get("/api/schedule/" + scheduleDTO.getId())
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    void getSchedule_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/schedule/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getSchedule_shouldReturn4xx_whenScheduleNotFound() throws Exception {
        mockMvc.perform(get("/api/schedule/99999")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().is4xxClientError());
    }

    // -------------------------------------------------------------------------
    // Usuwanie grafiku
    // -------------------------------------------------------------------------

    @Test
    void deleteSchedule_shouldReturn204_whenCalledByManager() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDTO scheduleDTO = objectMapper.readValue(created.getResponse().getContentAsString(), ScheduleDTO.class);

        mockMvc.perform(delete("/api/schedule/" + scheduleDTO.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSchedule_shouldReturn403_whenCalledByEmployee() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDTO scheduleDTO = objectMapper.readValue(created.getResponse().getContentAsString(), ScheduleDTO.class);

        mockMvc.perform(delete("/api/schedule/" + scheduleDTO.getId())
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteSchedule_shouldArchiveSchedule_notDeleteFromDb() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/schedule")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidSchedule())))
                .andExpect(status().isCreated())
                .andReturn();

        ScheduleDTO scheduleDTO = objectMapper.readValue(created.getResponse().getContentAsString(), ScheduleDTO.class);

        mockMvc.perform(delete("/api/schedule/" + scheduleDTO.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        // Grafik nadal istnieje w bazie (status ARCHIVED), nie jest fizycznie usunięty
        mockMvc.perform(get("/api/schedule/" + scheduleDTO.getId())
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private CreateShiftDTO buildShift(Long userId, LocalDateTime day, int startHour, int endHour) {
        CreateShiftDTO dto = new CreateShiftDTO();
        dto.setUserId(userId);
        dto.setDate(day.withHour(0).withMinute(0).withSecond(0));
        dto.setStartTime(day.withHour(startHour).withMinute(0).withSecond(0));
        dto.setEndTime(day.withHour(endHour).withMinute(0).withSecond(0));
        return dto;
    }

    private LocalDateTime SAT() {
        return LocalDateTime.of(2026, 6, 27, 0, 0);
    }
}
