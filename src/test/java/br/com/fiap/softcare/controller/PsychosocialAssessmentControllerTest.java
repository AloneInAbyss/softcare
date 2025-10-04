package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.dto.CreatePsychosocialAssessmentRequest;
import br.com.fiap.softcare.model.PsychosocialAssessment;
import br.com.fiap.softcare.repository.PsychosocialAssessmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
 * Integration tests for PsychosocialAssessmentController
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.database=softcare_test"
})
@WithMockUser(username = "user", roles = "USER")
class PsychosocialAssessmentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PsychosocialAssessmentRepository assessmentRepository;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // Clean up database before each test
        assessmentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create psychosocial assessment successfully")
    void shouldCreatePsychosocialAssessmentSuccessfully() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(3);
        request.setWorkLifeBalance(4);
        request.setJobSatisfaction(4);
        request.setRelationshipWithColleagues(5);
        request.setPersonalWellbeing(3);

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.workStressLevel").value(3))
                .andExpect(jsonPath("$.workLifeBalance").value(4))
                .andExpect(jsonPath("$.jobSatisfaction").value(4))
                .andExpect(jsonPath("$.relationshipWithColleagues").value(5))
                .andExpect(jsonPath("$.personalWellbeing").value(3))
                .andExpect(jsonPath("$.overallScore").exists())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.isComplete").value(true));

        // Verify assessment was saved in database
        List<PsychosocialAssessment> assessments = assessmentRepository.findAll();
        assertEquals(1, assessments.size());
        assertEquals("user123", assessments.get(0).getUserId());
    }

    @Test
    @DisplayName("Should get psychosocial assessment by ID successfully")
    void shouldGetPsychosocialAssessmentByIdSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment assessment = createTestAssessment("user123", 3, 4, 4, 5, 3);
        PsychosocialAssessment savedAssessment = assessmentRepository.save(assessment);

        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/{id}", savedAssessment.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAssessment.getId()))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.workStressLevel").value(3));
    }

    @Test
    @DisplayName("Should return 404 when getting assessment by non-existent ID")
    void shouldReturn404WhenGettingAssessmentByNonExistentId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/{id}", "507f1f77bcf86cd799439011"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get assessments by user ID successfully")
    void shouldGetAssessmentsByUserIdSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment assessment1 = createTestAssessment("user123", 3, 4, 4, 5, 3);
        PsychosocialAssessment assessment2 = createTestAssessment("user123", 2, 3, 3, 4, 2);
        PsychosocialAssessment assessment3 = createTestAssessment("user456", 4, 5, 5, 5, 4);
        
        assessmentRepository.saveAll(List.of(assessment1, assessment2, assessment3));

        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is("user123"))));
    }

    @Test
    @DisplayName("Should get latest assessment for user successfully")
    void shouldGetLatestAssessmentForUserSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment oldAssessment = createTestAssessment("user123", 2, 3, 3, 4, 2);
        oldAssessment.setCreatedAt(LocalDateTime.now().minusDays(5));
        
        PsychosocialAssessment latestAssessment = createTestAssessment("user123", 4, 5, 5, 5, 4);
        latestAssessment.setCreatedAt(LocalDateTime.now());
        
        assessmentRepository.saveAll(List.of(oldAssessment, latestAssessment));

        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/user/{userId}/latest", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.workStressLevel").value(4))
                .andExpect(jsonPath("$.workLifeBalance").value(5));
    }

    @Test
    @DisplayName("Should return 404 when getting latest assessment for user with no assessments")
    void shouldReturn404WhenGettingLatestAssessmentForUserWithNoAssessments() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/user/{userId}/latest", "user123"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update psychosocial assessment successfully")
    void shouldUpdatePsychosocialAssessmentSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment assessment = createTestAssessment("user123", 3, 4, 4, 5, 3);
        PsychosocialAssessment savedAssessment = assessmentRepository.save(assessment);

        CreatePsychosocialAssessmentRequest updateRequest = new CreatePsychosocialAssessmentRequest();
        updateRequest.setUserId("user123");
        updateRequest.setWorkStressLevel(2);
        updateRequest.setWorkLifeBalance(5);
        updateRequest.setJobSatisfaction(5);
        updateRequest.setRelationshipWithColleagues(5);
        updateRequest.setPersonalWellbeing(4);

        // When & Then
        mockMvc.perform(put("/api/v1/psychosocial-assessments/{id}", savedAssessment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAssessment.getId()))
                .andExpect(jsonPath("$.workStressLevel").value(2))
                .andExpect(jsonPath("$.workLifeBalance").value(5))
                .andExpect(jsonPath("$.jobSatisfaction").value(5))
                .andExpect(jsonPath("$.relationshipWithColleagues").value(5))
                .andExpect(jsonPath("$.personalWellbeing").value(4))
                .andExpect(jsonPath("$.overallScore").exists())
                .andExpect(jsonPath("$.riskLevel").exists());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent assessment")
    void shouldReturn404WhenUpdatingNonExistentAssessment() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest updateRequest = new CreatePsychosocialAssessmentRequest();
        updateRequest.setUserId("user123");
        updateRequest.setWorkStressLevel(3);
        updateRequest.setWorkLifeBalance(4);
        updateRequest.setJobSatisfaction(4);
        updateRequest.setRelationshipWithColleagues(5);
        updateRequest.setPersonalWellbeing(3);

        // When & Then
        mockMvc.perform(put("/api/v1/psychosocial-assessments/{id}", "507f1f77bcf86cd799439011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete psychosocial assessment successfully")
    void shouldDeletePsychosocialAssessmentSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment assessment = createTestAssessment("user123", 3, 4, 4, 5, 3);
        PsychosocialAssessment savedAssessment = assessmentRepository.save(assessment);

        // When & Then
        mockMvc.perform(delete("/api/v1/psychosocial-assessments/{id}", savedAssessment.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify assessment was deleted
        assertTrue(assessmentRepository.findById(savedAssessment.getId()).isEmpty());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent assessment")
    void shouldReturn404WhenDeletingNonExistentAssessment() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/psychosocial-assessments/{id}", "507f1f77bcf86cd799439011"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get assessments by risk level successfully")
    void shouldGetAssessmentsByRiskLevelSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment lowRiskAssessment = createTestAssessment("user1", 2, 5, 5, 5, 5); // Good scores except stress
        PsychosocialAssessment highRiskAssessment = createTestAssessment("user2", 5, 1, 1, 2, 1); // Poor scores
        PsychosocialAssessment moderateRiskAssessment = createTestAssessment("user3", 3, 3, 3, 3, 3); // Average scores
        
        assessmentRepository.saveAll(List.of(lowRiskAssessment, highRiskAssessment, moderateRiskAssessment));

        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/risk-level/{riskLevel}", "MODERATE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].riskLevel", everyItem(is("MODERATE"))));
    }

    @Test
    @DisplayName("Should get high risk assessments successfully")
    void shouldGetHighRiskAssessmentsSuccessfully() throws Exception {
        // Given
        PsychosocialAssessment lowRiskAssessment = createTestAssessment("user1", 2, 5, 5, 5, 5);
        PsychosocialAssessment highRiskAssessment1 = createTestAssessment("user2", 5, 1, 1, 2, 1);
        PsychosocialAssessment highRiskAssessment2 = createTestAssessment("user3", 4, 2, 1, 1, 2);
        
        assessmentRepository.saveAll(List.of(lowRiskAssessment, highRiskAssessment1, highRiskAssessment2));

        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/high-risk"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].riskLevel", everyItem(anyOf(is("HIGH"), is("CRITICAL")))));
    }

    @Test
    @DisplayName("Should get assessments in date range successfully")
    void shouldGetAssessmentsInDateRangeSuccessfully() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(7);
        LocalDateTime endDate = now;
        
        PsychosocialAssessment recentAssessment1 = createTestAssessment("user1", 3, 4, 4, 5, 3);
        recentAssessment1.setCreatedAt(now.minusDays(3));
        
        PsychosocialAssessment recentAssessment2 = createTestAssessment("user2", 2, 3, 3, 4, 2);
        recentAssessment2.setCreatedAt(now.minusDays(1));
        
        PsychosocialAssessment oldAssessment = createTestAssessment("user3", 4, 5, 5, 5, 4);
        oldAssessment.setCreatedAt(now.minusDays(10)); // Outside range
        
        assessmentRepository.saveAll(List.of(recentAssessment1, recentAssessment2, oldAssessment));

        // When & Then
        mockMvc.perform(get("/api/v1/psychosocial-assessments/date-range")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should return validation errors for invalid create request")
    void shouldReturnValidationErrorsForInvalidCreateRequest() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest invalidRequest = new CreatePsychosocialAssessmentRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid work stress level")
    void shouldReturnValidationErrorsForInvalidWorkStressLevel() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(10); // Invalid: should be 1-5
        request.setWorkLifeBalance(4);
        request.setJobSatisfaction(4);
        request.setRelationshipWithColleagues(5);
        request.setPersonalWellbeing(3);

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid work life balance")
    void shouldReturnValidationErrorsForInvalidWorkLifeBalance() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(3);
        request.setWorkLifeBalance(0); // Invalid: should be 1-5
        request.setJobSatisfaction(4);
        request.setRelationshipWithColleagues(5);
        request.setPersonalWellbeing(3);

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid job satisfaction")
    void shouldReturnValidationErrorsForInvalidJobSatisfaction() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(3);
        request.setWorkLifeBalance(4);
        request.setJobSatisfaction(6); // Invalid: should be 1-5
        request.setRelationshipWithColleagues(5);
        request.setPersonalWellbeing(3);

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid relationship with colleagues")
    void shouldReturnValidationErrorsForInvalidRelationshipWithColleagues() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(3);
        request.setWorkLifeBalance(4);
        request.setJobSatisfaction(4);
        request.setRelationshipWithColleagues(-1); // Invalid: should be 1-5
        request.setPersonalWellbeing(3);

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid personal wellbeing")
    void shouldReturnValidationErrorsForInvalidPersonalWellbeing() throws Exception {
        // Given
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(3);
        request.setWorkLifeBalance(4);
        request.setJobSatisfaction(4);
        request.setRelationshipWithColleagues(5);
        request.setPersonalWellbeing(7); // Invalid: should be 1-5

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should calculate risk level correctly for low risk assessment")
    void shouldCalculateRiskLevelCorrectlyForLowRiskAssessment() throws Exception {
        // Given - Good scores should result in low risk
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(2); // Low stress (good)
        request.setWorkLifeBalance(5); // Excellent balance
        request.setJobSatisfaction(5); // Very satisfied
        request.setRelationshipWithColleagues(5); // Excellent relationships
        request.setPersonalWellbeing(4); // Good wellbeing

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.overallScore", greaterThanOrEqualTo(4.0)));
    }

    @Test
    @DisplayName("Should calculate risk level correctly for high risk assessment")
    void shouldCalculateRiskLevelCorrectlyForHighRiskAssessment() throws Exception {
        // Given - Poor scores should result in high/critical risk
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(5); // Very high stress (bad)
        request.setWorkLifeBalance(1); // Poor balance
        request.setJobSatisfaction(1); // Very unsatisfied
        request.setRelationshipWithColleagues(2); // Poor relationships
        request.setPersonalWellbeing(1); // Poor wellbeing

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riskLevel", anyOf(is("HIGH"), is("CRITICAL"))))
                .andExpect(jsonPath("$.overallScore", lessThan(3.0)));
    }

    @Test
    @DisplayName("Should calculate risk level correctly for moderate risk assessment")
    void shouldCalculateRiskLevelCorrectlyForModerateRiskAssessment() throws Exception {
        // Given - Average scores should result in moderate risk
        CreatePsychosocialAssessmentRequest request = new CreatePsychosocialAssessmentRequest();
        request.setUserId("user123");
        request.setWorkStressLevel(3); // Moderate stress
        request.setWorkLifeBalance(3); // Average balance
        request.setJobSatisfaction(3); // Neutral satisfaction
        request.setRelationshipWithColleagues(3); // Average relationships
        request.setPersonalWellbeing(3); // Average wellbeing

        // When & Then
        mockMvc.perform(post("/api/v1/psychosocial-assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riskLevel").value("MODERATE"))
                .andExpect(jsonPath("$.overallScore", allOf(greaterThanOrEqualTo(3.0), lessThan(4.0))));
    }

    // Helper method to create test assessments
    private PsychosocialAssessment createTestAssessment(String userId, Integer workStress, 
                                                       Integer workLifeBalance, Integer jobSatisfaction,
                                                       Integer relationships, Integer wellbeing) {
        PsychosocialAssessment assessment = PsychosocialAssessment.builder()
                .userId(userId)
                .workStressLevel(workStress)
                .workLifeBalance(workLifeBalance)
                .jobSatisfaction(jobSatisfaction)
                .relationshipWithColleagues(relationships)
                .personalWellbeing(wellbeing)
                .build();
        
        assessment.calculateOverallScore();
        assessment.determineRiskLevel();
        
        return assessment;
    }
}