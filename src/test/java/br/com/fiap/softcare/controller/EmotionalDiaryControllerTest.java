package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.dto.CreateEmotionalDiaryRequest;
import br.com.fiap.softcare.enums.MoodLevel;
import br.com.fiap.softcare.model.EmotionalDiary;
import br.com.fiap.softcare.repository.EmotionalDiaryRepository;
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

import java.time.LocalDate;
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
 * Integration tests for EmotionalDiaryController
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.database=softcare_test"
})
@WithMockUser(username = "user", roles = "USER")
class EmotionalDiaryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmotionalDiaryRepository emotionalDiaryRepository;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // Clean up database before each test
        emotionalDiaryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create emotional diary entry successfully")
    void shouldCreateEmotionalDiaryEntrySuccessfully() throws Exception {
        // Given
        CreateEmotionalDiaryRequest request = new CreateEmotionalDiaryRequest();
        request.setUserId("user123");
        request.setEntryDate(LocalDate.now());
        request.setMoodLevel(MoodLevel.GOOD);
        request.setEnergyLevel(4);
        request.setStressLevel(2);
        request.setSleepQuality(4);

        // When & Then
        mockMvc.perform(post("/api/v1/emotional-diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.entryDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.moodLevel").value("GOOD"))
                .andExpect(jsonPath("$.energyLevel").value(4))
                .andExpect(jsonPath("$.stressLevel").value(2))
                .andExpect(jsonPath("$.sleepQuality").value(4));

        // Verify entry was saved in database
        List<EmotionalDiary> entries = emotionalDiaryRepository.findAll();
        assertEquals(1, entries.size());
        assertEquals("user123", entries.get(0).getUserId());
    }

    @Test
    @DisplayName("Should get emotional diary entry by ID successfully")
    void shouldGetEmotionalDiaryEntryByIdSuccessfully() throws Exception {
        // Given
        EmotionalDiary entry = createTestEntry("user123", LocalDate.now(), MoodLevel.GOOD);
        EmotionalDiary savedEntry = emotionalDiaryRepository.save(entry);

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/{id}", savedEntry.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEntry.getId()))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.moodLevel").value("GOOD"));
    }

    @Test
    @DisplayName("Should return 404 when getting diary entry by non-existent ID")
    void shouldReturn404WhenGettingDiaryEntryByNonExistentId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/{id}", "507f1f77bcf86cd799439011"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get diary entries by user ID successfully")
    void shouldGetDiaryEntriesByUserIdSuccessfully() throws Exception {
        // Given
        EmotionalDiary entry1 = createTestEntry("user123", LocalDate.now(), MoodLevel.GOOD);
        EmotionalDiary entry2 = createTestEntry("user123", LocalDate.now().minusDays(1), MoodLevel.NEUTRAL);
        EmotionalDiary entry3 = createTestEntry("user456", LocalDate.now(), MoodLevel.LOW);
        
        emotionalDiaryRepository.saveAll(List.of(entry1, entry2, entry3));

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].userId", everyItem(is("user123"))));
    }

    @Test
    @DisplayName("Should get diary entry by user and date successfully")
    void shouldGetDiaryEntryByUserAndDateSuccessfully() throws Exception {
        // Given
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        EmotionalDiary entry = createTestEntry("user123", testDate, MoodLevel.VERY_GOOD);
        emotionalDiaryRepository.save(entry);

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/date/{date}", 
                        "user123", testDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.entryDate").value(testDate.toString()))
                .andExpect(jsonPath("$.moodLevel").value("VERY_GOOD"));
    }

    @Test
    @DisplayName("Should return 404 when getting diary entry by user and non-existent date")
    void shouldReturn404WhenGettingDiaryEntryByUserAndNonExistentDate() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/date/{date}", 
                        "user123", "2024-12-25"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get latest diary entry for user successfully")
    void shouldGetLatestDiaryEntryForUserSuccessfully() throws Exception {
        // Given
        EmotionalDiary oldEntry = createTestEntry("user123", LocalDate.now().minusDays(5), MoodLevel.LOW);
        EmotionalDiary latestEntry = createTestEntry("user123", LocalDate.now(), MoodLevel.GOOD);
        
        emotionalDiaryRepository.saveAll(List.of(oldEntry, latestEntry));

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/latest", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.entryDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.moodLevel").value("GOOD"));
    }

    @Test
    @DisplayName("Should return 404 when getting latest diary entry for user with no entries")
    void shouldReturn404WhenGettingLatestDiaryEntryForUserWithNoEntries() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/latest", "user123"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update emotional diary entry successfully")
    void shouldUpdateEmotionalDiaryEntrySuccessfully() throws Exception {
        // Given
        EmotionalDiary entry = createTestEntry("user123", LocalDate.now(), MoodLevel.NEUTRAL);
        EmotionalDiary savedEntry = emotionalDiaryRepository.save(entry);

        CreateEmotionalDiaryRequest updateRequest = new CreateEmotionalDiaryRequest();
        updateRequest.setUserId("user123");
        updateRequest.setEntryDate(LocalDate.now());
        updateRequest.setMoodLevel(MoodLevel.VERY_GOOD);
        updateRequest.setEnergyLevel(5);
        updateRequest.setStressLevel(1);
        updateRequest.setSleepQuality(5);

        // When & Then
        mockMvc.perform(put("/api/v1/emotional-diary/{id}", savedEntry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEntry.getId()))
                .andExpect(jsonPath("$.moodLevel").value("VERY_GOOD"))
                .andExpect(jsonPath("$.energyLevel").value(5))
                .andExpect(jsonPath("$.stressLevel").value(1))
                .andExpect(jsonPath("$.sleepQuality").value(5));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent diary entry")
    void shouldReturn404WhenUpdatingNonExistentDiaryEntry() throws Exception {
        // Given
        CreateEmotionalDiaryRequest updateRequest = new CreateEmotionalDiaryRequest();
        updateRequest.setUserId("user123");
        updateRequest.setEntryDate(LocalDate.now());
        updateRequest.setMoodLevel(MoodLevel.GOOD);

        // When & Then
        mockMvc.perform(put("/api/v1/emotional-diary/{id}", "507f1f77bcf86cd799439011")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete emotional diary entry successfully")
    void shouldDeleteEmotionalDiaryEntrySuccessfully() throws Exception {
        // Given
        EmotionalDiary entry = createTestEntry("user123", LocalDate.now(), MoodLevel.GOOD);
        EmotionalDiary savedEntry = emotionalDiaryRepository.save(entry);

        // When & Then
        mockMvc.perform(delete("/api/v1/emotional-diary/{id}", savedEntry.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verify entry was deleted
        assertTrue(emotionalDiaryRepository.findById(savedEntry.getId()).isEmpty());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent diary entry")
    void shouldReturn404WhenDeletingNonExistentDiaryEntry() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/emotional-diary/{id}", "507f1f77bcf86cd799439011"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get diary entries in date range successfully")
    void shouldGetDiaryEntriesInDateRangeSuccessfully() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        EmotionalDiary entry1 = createTestEntry("user123", LocalDate.now().minusDays(5), MoodLevel.GOOD);
        EmotionalDiary entry2 = createTestEntry("user123", LocalDate.now().minusDays(3), MoodLevel.NEUTRAL);
        EmotionalDiary entry3 = createTestEntry("user123", LocalDate.now().minusDays(10), MoodLevel.LOW); // Outside range
        
        emotionalDiaryRepository.saveAll(List.of(entry1, entry2, entry3));

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/range", "user123")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should get average wellness score successfully")
    void shouldGetAverageWellnessScoreSuccessfully() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        EmotionalDiary entry1 = createTestEntry("user123", LocalDate.now().minusDays(5), MoodLevel.GOOD);
        entry1.setEnergyLevel(4);
        entry1.setStressLevel(2);
        entry1.setSleepQuality(4);
        
        EmotionalDiary entry2 = createTestEntry("user123", LocalDate.now().minusDays(3), MoodLevel.VERY_GOOD);
        entry2.setEnergyLevel(5);
        entry2.setStressLevel(1);
        entry2.setSleepQuality(5);
        
        emotionalDiaryRepository.saveAll(List.of(entry1, entry2));

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/average-wellness", "user123")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$", greaterThan(0.0)));
    }

    @Test
    @DisplayName("Should return no content when no entries for average wellness score")
    void shouldReturnNoContentWhenNoEntriesForAverageWellnessScore() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/average-wellness", "user123")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should check if user has entry for today successfully")
    void shouldCheckIfUserHasEntryForTodaySuccessfully() throws Exception {
        // Given
        EmotionalDiary todayEntry = createTestEntry("user123", LocalDate.now(), MoodLevel.GOOD);
        emotionalDiaryRepository.save(todayEntry);

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/today", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("Should return false when user has no entry for today")
    void shouldReturnFalseWhenUserHasNoEntryForToday() throws Exception {
        // Given
        EmotionalDiary yesterdayEntry = createTestEntry("user123", LocalDate.now().minusDays(1), MoodLevel.GOOD);
        emotionalDiaryRepository.save(yesterdayEntry);

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/user/{userId}/today", "user123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @DisplayName("Should get low mood entries successfully")
    void shouldGetLowMoodEntriesSuccessfully() throws Exception {
        // Given
        EmotionalDiary lowMoodEntry1 = createTestEntry("user1", LocalDate.now(), MoodLevel.VERY_LOW);
        EmotionalDiary lowMoodEntry2 = createTestEntry("user2", LocalDate.now(), MoodLevel.LOW);
        EmotionalDiary goodMoodEntry = createTestEntry("user3", LocalDate.now(), MoodLevel.GOOD);
        
        emotionalDiaryRepository.saveAll(List.of(lowMoodEntry1, lowMoodEntry2, goodMoodEntry));

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/low-mood"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].moodLevel", everyItem(anyOf(is("VERY_LOW"), is("LOW")))));
    }

    @Test
    @DisplayName("Should get high stress entries successfully")
    void shouldGetHighStressEntriesSuccessfully() throws Exception {
        // Given
        EmotionalDiary highStressEntry1 = createTestEntry("user1", LocalDate.now(), MoodLevel.NEUTRAL);
        highStressEntry1.setStressLevel(5);
        
        EmotionalDiary highStressEntry2 = createTestEntry("user2", LocalDate.now(), MoodLevel.GOOD);
        highStressEntry2.setStressLevel(4);
        
        EmotionalDiary lowStressEntry = createTestEntry("user3", LocalDate.now(), MoodLevel.GOOD);
        lowStressEntry.setStressLevel(2);
        
        emotionalDiaryRepository.saveAll(List.of(highStressEntry1, highStressEntry2, lowStressEntry));

        // When & Then
        mockMvc.perform(get("/api/v1/emotional-diary/high-stress"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].stressLevel", everyItem(greaterThanOrEqualTo(4))));
    }

    @Test
    @DisplayName("Should return validation errors for invalid create request")
    void shouldReturnValidationErrorsForInvalidCreateRequest() throws Exception {
        // Given
        CreateEmotionalDiaryRequest invalidRequest = new CreateEmotionalDiaryRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/emotional-diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid energy level")
    void shouldReturnValidationErrorsForInvalidEnergyLevel() throws Exception {
        // Given
        CreateEmotionalDiaryRequest request = new CreateEmotionalDiaryRequest();
        request.setUserId("user123");
        request.setEntryDate(LocalDate.now());
        request.setMoodLevel(MoodLevel.GOOD);
        request.setEnergyLevel(10); // Invalid: should be 1-5

        // When & Then
        mockMvc.perform(post("/api/v1/emotional-diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid stress level")
    void shouldReturnValidationErrorsForInvalidStressLevel() throws Exception {
        // Given
        CreateEmotionalDiaryRequest request = new CreateEmotionalDiaryRequest();
        request.setUserId("user123");
        request.setEntryDate(LocalDate.now());
        request.setMoodLevel(MoodLevel.GOOD);
        request.setStressLevel(0); // Invalid: should be 1-5

        // When & Then
        mockMvc.perform(post("/api/v1/emotional-diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation errors for invalid sleep quality")
    void shouldReturnValidationErrorsForInvalidSleepQuality() throws Exception {
        // Given
        CreateEmotionalDiaryRequest request = new CreateEmotionalDiaryRequest();
        request.setUserId("user123");
        request.setEntryDate(LocalDate.now());
        request.setMoodLevel(MoodLevel.GOOD);
        request.setSleepQuality(6); // Invalid: should be 1-5

        // When & Then
        mockMvc.perform(post("/api/v1/emotional-diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle duplicate entry creation gracefully")
    void shouldHandleDuplicateEntryCreationGracefully() throws Exception {
        // Given
        LocalDate entryDate = LocalDate.now();
        EmotionalDiary existingEntry = createTestEntry("user123", entryDate, MoodLevel.GOOD);
        emotionalDiaryRepository.save(existingEntry);

        CreateEmotionalDiaryRequest duplicateRequest = new CreateEmotionalDiaryRequest();
        duplicateRequest.setUserId("user123");
        duplicateRequest.setEntryDate(entryDate);
        duplicateRequest.setMoodLevel(MoodLevel.LOW);

        // When & Then
        mockMvc.perform(post("/api/v1/emotional-diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Helper method to create test entries
    private EmotionalDiary createTestEntry(String userId, LocalDate entryDate, MoodLevel moodLevel) {
        return EmotionalDiary.builder()
                .userId(userId)
                .entryDate(entryDate)
                .moodLevel(moodLevel)
                .energyLevel(3)
                .stressLevel(3)
                .sleepQuality(3)
                .build();
    }
}