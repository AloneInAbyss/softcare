package br.com.fiap.softcare.service;

import br.com.fiap.softcare.enums.MoodLevel;
import br.com.fiap.softcare.model.EmotionalDiary;
import br.com.fiap.softcare.repository.EmotionalDiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Emotional Diary operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionalDiaryService {
    
    private final EmotionalDiaryRepository diaryRepository;
    private final AuditLogService auditLogService;
    
    /**
     * Create a new diary entry
     */
    public EmotionalDiary createDiaryEntry(EmotionalDiary diaryEntry) {
        log.info("Creating diary entry for user: {} on date: {}", 
                diaryEntry.getUserId(), diaryEntry.getEntryDate());
        
        // Check if entry already exists for this user and date
        Optional<EmotionalDiary> existingEntry = diaryRepository
                .findByUserIdAndEntryDate(diaryEntry.getUserId(), diaryEntry.getEntryDate());
        
        if (existingEntry.isPresent()) {
            throw new IllegalArgumentException("Entrada já existe para esta data: " + diaryEntry.getEntryDate());
        }
        
        EmotionalDiary savedEntry = diaryRepository.save(diaryEntry);
        
        // Log the creation
        auditLogService.logDiaryEntryCreated(diaryEntry.getUserId(), savedEntry.getId());
        
        // Check if entry indicates concern and log if necessary
        if (savedEntry.indicatesConcern()) {
            log.warn("Diary entry indicates concern for user: {}", savedEntry.getUserId());
            auditLogService.logEvent("DIARY_CONCERN_DETECTED", 
                    "Diary entry indicates potential concern", savedEntry.getUserId(), true);
        }
        
        log.info("Diary entry created with ID: {}", savedEntry.getId());
        return savedEntry;
    }
    
    /**
     * Find diary entry by ID
     */
    public Optional<EmotionalDiary> findById(String id) {
        return diaryRepository.findById(id);
    }
    
    /**
     * Find diary entries by user ID
     */
    public List<EmotionalDiary> findByUserId(String userId) {
        return diaryRepository.findByUserIdOrderByEntryDateDesc(userId);
    }
    
    /**
     * Find diary entry for specific user and date
     */
    public Optional<EmotionalDiary> findByUserIdAndDate(String userId, LocalDate date) {
        return diaryRepository.findByUserIdAndEntryDate(userId, date);
    }
    
    /**
     * Find latest diary entry for user
     */
    public Optional<EmotionalDiary> findLatestByUserId(String userId) {
        return diaryRepository.findTopByUserIdOrderByEntryDateDesc(userId);
    }
    
    /**
     * Update diary entry
     */
    public EmotionalDiary updateDiaryEntry(String id, EmotionalDiary updatedEntry) {
        log.info("Updating diary entry: {}", id);
        
        EmotionalDiary existingEntry = diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrada do diário não encontrada: " + id));
        
        // Update fields
        existingEntry.setMoodLevel(updatedEntry.getMoodLevel());
        existingEntry.setEnergyLevel(updatedEntry.getEnergyLevel());
        existingEntry.setStressLevel(updatedEntry.getStressLevel());
        existingEntry.setSleepQuality(updatedEntry.getSleepQuality());
        
        EmotionalDiary savedEntry = diaryRepository.save(existingEntry);
        
        // Check if updated entry indicates concern
        if (savedEntry.indicatesConcern()) {
            log.warn("Updated diary entry indicates concern for user: {}", savedEntry.getUserId());
            auditLogService.logEvent("DIARY_CONCERN_DETECTED", 
                    "Updated diary entry indicates potential concern", savedEntry.getUserId(), true);
        }
        
        log.info("Diary entry updated: {}", savedEntry.getId());
        return savedEntry;
    }
    
    /**
     * Delete diary entry
     */
    public void deleteDiaryEntry(String id) {
        log.info("Deleting diary entry: {}", id);
        
        diaryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrada do diário não encontrada: " + id));
        
        diaryRepository.deleteById(id);
        
        log.info("Diary entry deleted: {}", id);
    }
    
    /**
     * Find entries within date range for user
     */
    public List<EmotionalDiary> findEntriesInDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findByUserIdAndEntryDateBetween(userId, startDate, endDate);
    }
    
    /**
     * Find entries with low mood
     */
    public List<EmotionalDiary> findLowMoodEntries() {
        return diaryRepository.findLowMoodEntries();
    }
    
    /**
     * Find entries with high stress
     */
    public List<EmotionalDiary> findHighStressEntries() {
        return diaryRepository.findHighStressEntries();
    }
    
    /**
     * Calculate average wellness score for user in date range
     */
    public Double calculateAverageWellnessScore(String userId, LocalDate startDate, LocalDate endDate) {
        List<EmotionalDiary> entries = findEntriesInDateRange(userId, startDate, endDate);
        
        if (entries.isEmpty()) {
            return null;
        }
        
        double totalScore = 0;
        int validEntries = 0;
        
        for (EmotionalDiary entry : entries) {
            Double score = entry.calculateWellnessScore();
            if (score != null) {
                totalScore += score;
                validEntries++;
            }
        }
        
        return validEntries > 0 ? totalScore / validEntries : null;
    }
    
    /**
     * Get diary statistics for user
     */
    public DiaryStatistics getDiaryStatistics(String userId) {
        List<EmotionalDiary> allEntries = diaryRepository.findByUserId(userId);
        
        long totalEntries = allEntries.size();
        long concernEntries = allEntries.stream()
                .mapToLong(entry -> entry.indicatesConcern() ? 1 : 0)
                .sum();
        
        // Calculate mood distribution
        long veryLowMood = allEntries.stream()
                .mapToLong(entry -> entry.getMoodLevel() == MoodLevel.VERY_LOW ? 1 : 0)
                .sum();
        long lowMood = allEntries.stream()
                .mapToLong(entry -> entry.getMoodLevel() == MoodLevel.LOW ? 1 : 0)
                .sum();
        long neutralMood = allEntries.stream()
                .mapToLong(entry -> entry.getMoodLevel() == MoodLevel.NEUTRAL ? 1 : 0)
                .sum();
        long goodMood = allEntries.stream()
                .mapToLong(entry -> entry.getMoodLevel() == MoodLevel.GOOD ? 1 : 0)
                .sum();
        long veryGoodMood = allEntries.stream()
                .mapToLong(entry -> entry.getMoodLevel() == MoodLevel.VERY_GOOD ? 1 : 0)
                .sum();
        
        return DiaryStatistics.builder()
                .totalEntries(totalEntries)
                .concernEntries(concernEntries)
                .veryLowMood(veryLowMood)
                .lowMood(lowMood)
                .neutralMood(neutralMood)
                .goodMood(goodMood)
                .veryGoodMood(veryGoodMood)
                .build();
    }
    
    /**
     * Check if user has entry for today
     */
    public boolean hasEntryForToday(String userId) {
        return diaryRepository.findByUserIdAndEntryDate(userId, LocalDate.now()).isPresent();
    }
    
    /**
     * Inner class for diary statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class DiaryStatistics {
        private long totalEntries;
        private long concernEntries;
        private long veryLowMood;
        private long lowMood;
        private long neutralMood;
        private long goodMood;
        private long veryGoodMood;
    }
}