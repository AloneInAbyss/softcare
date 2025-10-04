package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.model.AuditLog;
import br.com.fiap.softcare.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for AuditLogController
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.database=softcare_test"
})
@WithMockUser(username = "user", roles = "USER")
class AuditLogControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // Clean up database before each test
        auditLogRepository.deleteAll();
    }

    @Test
    @DisplayName("Should get audit logs by user ID successfully")
    void shouldGetAuditLogsByUserIdSuccessfully() throws Exception {
        // Given
        AuditLog userLog1 = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "User logged in", AuditLog.Severity.INFO, true);
        AuditLog userLog2 = createTestAuditLog("user123", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Diary entry created", AuditLog.Severity.INFO, true);
        AuditLog otherUserLog = createTestAuditLog("user456", AuditLog.EventTypes.USER_LOGIN, 
                "Other user logged in", AuditLog.Severity.INFO, true);
        
        auditLogRepository.saveAll(List.of(userLog1, userLog2, otherUserLog));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is("user123"))))
                .andExpect(jsonPath("$[*].eventType", hasItems(AuditLog.EventTypes.USER_LOGIN, AuditLog.EventTypes.DIARY_ENTRY_CREATED)));
    }

    @Test
    @DisplayName("Should return empty list when user has no audit logs")
    void shouldReturnEmptyListWhenUserHasNoAuditLogs() throws Exception {
        // Given - no logs for this user
        AuditLog otherUserLog = createTestAuditLog("user456", AuditLog.EventTypes.USER_LOGIN, 
                "Other user logged in", AuditLog.Severity.INFO, true);
        auditLogRepository.save(otherUserLog);

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should get recent audit logs by user ID successfully")
    void shouldGetRecentAuditLogsByUserIdSuccessfully() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        AuditLog recentLog1 = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Recent login", AuditLog.Severity.INFO, true);
        recentLog1.setTimestamp(now.minusMinutes(10));
        
        AuditLog recentLog2 = createTestAuditLog("user123", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Recent diary entry", AuditLog.Severity.INFO, true);
        recentLog2.setTimestamp(now.minusMinutes(5));
        
        AuditLog oldLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGOUT, 
                "Old logout", AuditLog.Severity.INFO, true);
        oldLog.setTimestamp(now.minusDays(10)); // Should not appear in recent logs
        
        auditLogRepository.saveAll(List.of(recentLog1, recentLog2, oldLog));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}/recent", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].userId", everyItem(is("user123"))));
    }

    @Test
    @DisplayName("Should get audit logs by event type successfully")
    void shouldGetAuditLogsByEventTypeSuccessfully() throws Exception {
        // Given
        AuditLog loginLog1 = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "User login", AuditLog.Severity.INFO, true);
        AuditLog loginLog2 = createTestAuditLog("user456", AuditLog.EventTypes.USER_LOGIN, 
                "Another user login", AuditLog.Severity.INFO, true);
        AuditLog diaryLog = createTestAuditLog("user123", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Diary entry", AuditLog.Severity.INFO, true);
        
        auditLogRepository.saveAll(List.of(loginLog1, loginLog2, diaryLog));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/event-type/{eventType}", AuditLog.EventTypes.USER_LOGIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].eventType", everyItem(is(AuditLog.EventTypes.USER_LOGIN))));
    }

    @Test
    @DisplayName("Should return empty list for non-existent event type")
    void shouldReturnEmptyListForNonExistentEventType() throws Exception {
        // Given
        AuditLog loginLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "User login", AuditLog.Severity.INFO, true);
        auditLogRepository.save(loginLog);

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/event-type/{eventType}", "NON_EXISTENT_EVENT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should get critical audit logs successfully")
    void shouldGetCriticalAuditLogsSuccessfully() throws Exception {
        // Given
        AuditLog criticalLog1 = createTestAuditLog("user123", AuditLog.EventTypes.SECURITY_VIOLATION, 
                "Security violation detected", AuditLog.Severity.CRITICAL, false);
        AuditLog criticalLog2 = createTestAuditLog("user456", AuditLog.EventTypes.SYSTEM_ERROR, 
                "Critical system error", AuditLog.Severity.CRITICAL, false);
        AuditLog errorLog = createTestAuditLog("user789", AuditLog.EventTypes.SYSTEM_ERROR, 
                "System error", AuditLog.Severity.ERROR, false);
        AuditLog infoLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Normal login", AuditLog.Severity.INFO, true);
        
        auditLogRepository.saveAll(List.of(criticalLog1, criticalLog2, errorLog, infoLog));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/critical"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].severity", everyItem(anyOf(is(AuditLog.Severity.CRITICAL), is(AuditLog.Severity.ERROR)))));
    }

    @Test
    @DisplayName("Should return empty list when no critical logs exist")
    void shouldReturnEmptyListWhenNoCriticalLogsExist() throws Exception {
        // Given
        AuditLog infoLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Normal login", AuditLog.Severity.INFO, true);
        AuditLog warnLog = createTestAuditLog("user456", AuditLog.EventTypes.USER_LOGOUT, 
                "Warning logout", AuditLog.Severity.WARN, true);
        
        auditLogRepository.saveAll(List.of(infoLog, warnLog));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/critical"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should get failed operations successfully")
    void shouldGetFailedOperationsSuccessfully() throws Exception {
        // Given
        AuditLog failedLogin = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Failed login attempt", AuditLog.Severity.WARN, false);
        failedLogin.setErrorMessage("Invalid credentials");
        
        AuditLog failedOperation = createTestAuditLog("user456", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Failed to create diary entry", AuditLog.Severity.ERROR, false);
        failedOperation.setErrorMessage("Validation failed");
        
        AuditLog successfulLogin = createTestAuditLog("user789", AuditLog.EventTypes.USER_LOGIN, 
                "Successful login", AuditLog.Severity.INFO, true);
        
        auditLogRepository.saveAll(List.of(failedLogin, failedOperation, successfulLogin));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/failed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].success", everyItem(is(false))))
                .andExpect(jsonPath("$[*].errorMessage", everyItem(notNullValue())));
    }

    @Test
    @DisplayName("Should return empty list when no failed operations exist")
    void shouldReturnEmptyListWhenNoFailedOperationsExist() throws Exception {
        // Given
        AuditLog successfulLogin = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Successful login", AuditLog.Severity.INFO, true);
        AuditLog successfulOperation = createTestAuditLog("user456", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Successful diary entry", AuditLog.Severity.INFO, true);
        
        auditLogRepository.saveAll(List.of(successfulLogin, successfulOperation));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/failed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle logs with different severity levels correctly")
    void shouldHandleLogsWithDifferentSeverityLevelsCorrectly() throws Exception {
        // Given
        AuditLog infoLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Info level log", AuditLog.Severity.INFO, true);
        AuditLog warnLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGOUT, 
                "Warning level log", AuditLog.Severity.WARN, true);
        AuditLog errorLog = createTestAuditLog("user123", AuditLog.EventTypes.SYSTEM_ERROR, 
                "Error level log", AuditLog.Severity.ERROR, false);
        AuditLog criticalLog = createTestAuditLog("user123", AuditLog.EventTypes.SECURITY_VIOLATION, 
                "Critical level log", AuditLog.Severity.CRITICAL, false);
        
        auditLogRepository.saveAll(List.of(infoLog, warnLog, errorLog, criticalLog));

        // When & Then - Test user logs include all severity levels
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].severity", hasItems(
                        AuditLog.Severity.INFO, 
                        AuditLog.Severity.WARN, 
                        AuditLog.Severity.ERROR, 
                        AuditLog.Severity.CRITICAL
                )));
    }

    @Test
    @DisplayName("Should handle logs with HTTP request information correctly")
    void shouldHandleLogsWithHttpRequestInformationCorrectly() throws Exception {
        // Given
        AuditLog httpLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Login with HTTP info", AuditLog.Severity.INFO, true);
        httpLog.setHttpMethod("POST");
        httpLog.setRequestUrl("/api/v1/auth/login");
        httpLog.setIpAddress("192.168.1.100");
        httpLog.setUserEmail("user@example.com");
        
        auditLogRepository.save(httpLog);

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].httpMethod").value("POST"))
                .andExpect(jsonPath("$[0].requestUrl").value("/api/v1/auth/login"))
                .andExpect(jsonPath("$[0].ipAddress").value("192.168.1.100"))
                .andExpect(jsonPath("$[0].userEmail").value("user@example.com"));
    }

    @Test
    @DisplayName("Should handle logs with resource ID correctly")
    void shouldHandleLogsWithResourceIdCorrectly() throws Exception {
        // Given
        AuditLog resourceLog = createTestAuditLog("user123", AuditLog.EventTypes.ASSESSMENT_CREATED, 
                "Assessment created", AuditLog.Severity.INFO, true);
        resourceLog.setResourceId("assessment_12345");
        
        auditLogRepository.save(resourceLog);

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].resourceId").value("assessment_12345"))
                .andExpect(jsonPath("$[0].eventType").value(AuditLog.EventTypes.ASSESSMENT_CREATED));
    }

    @Test
    @DisplayName("Should filter logs by multiple event types correctly")
    void shouldFilterLogsByMultipleEventTypesCorrectly() throws Exception {
        // Given
        AuditLog loginLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "User login", AuditLog.Severity.INFO, true);
        AuditLog logoutLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGOUT, 
                "User logout", AuditLog.Severity.INFO, true);
        AuditLog createLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_CREATED, 
                "User created", AuditLog.Severity.INFO, true);
        AuditLog diaryLog = createTestAuditLog("user123", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Diary entry", AuditLog.Severity.INFO, true);
        
        auditLogRepository.saveAll(List.of(loginLog, logoutLog, createLog, diaryLog));

        // When & Then - Test USER_LOGIN
        mockMvc.perform(get("/api/v1/audit-logs/event-type/{eventType}", AuditLog.EventTypes.USER_LOGIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventType").value(AuditLog.EventTypes.USER_LOGIN));

        // When & Then - Test USER_CREATED
        mockMvc.perform(get("/api/v1/audit-logs/event-type/{eventType}", AuditLog.EventTypes.USER_CREATED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventType").value(AuditLog.EventTypes.USER_CREATED));
    }

    @Test
    @DisplayName("Should handle logs ordered by timestamp correctly")
    void shouldHandleLogsOrderedByTimestampCorrectly() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        AuditLog oldLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGIN, 
                "Old login", AuditLog.Severity.INFO, true);
        oldLog.setTimestamp(now.minusHours(2));
        
        AuditLog newLog = createTestAuditLog("user123", AuditLog.EventTypes.USER_LOGOUT, 
                "Recent logout", AuditLog.Severity.INFO, true);
        newLog.setTimestamp(now.minusMinutes(30));
        
        AuditLog newestLog = createTestAuditLog("user123", AuditLog.EventTypes.DIARY_ENTRY_CREATED, 
                "Latest diary", AuditLog.Severity.INFO, true);
        newestLog.setTimestamp(now.minusMinutes(5));
        
        auditLogRepository.saveAll(List.of(oldLog, newLog, newestLog));

        // When & Then
        mockMvc.perform(get("/api/v1/audit-logs/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                // Verify logs are in chronological order (newest first typically)
                .andExpect(jsonPath("$[*].description", hasItems("Old login", "Recent logout", "Latest diary")));
    }

    // Helper method to create test audit logs
    private AuditLog createTestAuditLog(String userId, String eventType, String description, 
                                       String severity, boolean success) {
        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .eventType(eventType)
                .description(description)
                .severity(severity)
                .success(success)
                .timestamp(LocalDateTime.now())
                .build();
                
        if (!success) {
            auditLog.setErrorMessage("Operation failed: " + description);
        }
        
        return auditLog;
    }
}