package br.com.fiap.softcare.controller;

import br.com.fiap.softcare.dto.CreateEmotionalDiaryRequest;
import br.com.fiap.softcare.model.EmotionalDiary;
import br.com.fiap.softcare.service.EmotionalDiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Emotional Diary operations
 */
@Slf4j
@RestController
@RequestMapping("/emotional-diary")
@RequiredArgsConstructor
public class EmotionalDiaryController {
    
    private final EmotionalDiaryService emotionalDiaryService;
    
    /**
     * Create a new diary entry
     */
    @PostMapping
    public ResponseEntity<EmotionalDiary> createDiaryEntry(@Valid @RequestBody CreateEmotionalDiaryRequest request) {
        log.info("Creating diary entry for user: {} on date: {}", 
                request.getUserId(), request.getEntryDate());
        
        EmotionalDiary diaryEntry = EmotionalDiary.builder()
                .userId(request.getUserId())
                .entryDate(request.getEntryDate())
                .moodLevel(request.getMoodLevel())
                .energyLevel(request.getEnergyLevel())
                .stressLevel(request.getStressLevel())
                .sleepQuality(request.getSleepQuality())
                .build();
        
        try {
            EmotionalDiary createdEntry = emotionalDiaryService.createDiaryEntry(diaryEntry);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get diary entry by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmotionalDiary> getDiaryEntryById(@PathVariable String id) {
        log.info("Getting diary entry by ID: {}", id);
        
        Optional<EmotionalDiary> entry = emotionalDiaryService.findById(id);
        
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get diary entries by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmotionalDiary>> getDiaryEntriesByUserId(@PathVariable String userId) {
        log.info("Getting diary entries for user: {}", userId);
        
        List<EmotionalDiary> entries = emotionalDiaryService.findByUserId(userId);
        
        return ResponseEntity.ok(entries);
    }
    
    /**
     * Get diary entry by user and date
     */
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<EmotionalDiary> getDiaryEntryByUserAndDate(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Getting diary entry for user: {} on date: {}", userId, date);
        
        Optional<EmotionalDiary> entry = emotionalDiaryService.findByUserIdAndDate(userId, date);
        
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get latest diary entry for user
     */
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<EmotionalDiary> getLatestDiaryEntry(@PathVariable String userId) {
        log.info("Getting latest diary entry for user: {}", userId);
        
        Optional<EmotionalDiary> entry = emotionalDiaryService.findLatestByUserId(userId);
        
        return entry.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update diary entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmotionalDiary> updateDiaryEntry(@PathVariable String id,
                                                           @Valid @RequestBody CreateEmotionalDiaryRequest request) {
        log.info("Updating diary entry: {}", id);
        
        try {
            EmotionalDiary updatedEntry = EmotionalDiary.builder()
                    .moodLevel(request.getMoodLevel())
                    .energyLevel(request.getEnergyLevel())
                    .stressLevel(request.getStressLevel())
                    .sleepQuality(request.getSleepQuality())
                    .build();
            
            EmotionalDiary savedEntry = emotionalDiaryService.updateDiaryEntry(id, updatedEntry);
            return ResponseEntity.ok(savedEntry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete diary entry
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaryEntry(@PathVariable String id) {
        log.info("Deleting diary entry: {}", id);
        
        try {
            emotionalDiaryService.deleteDiaryEntry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get diary entries in date range for user
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<EmotionalDiary>> getDiaryEntriesInRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting diary entries for user: {} from {} to {}", userId, startDate, endDate);
        
        List<EmotionalDiary> entries = emotionalDiaryService.findEntriesInDateRange(userId, startDate, endDate);
        
        return ResponseEntity.ok(entries);
    }
    
    /**
     * Get average wellness score for user in date range
     */
    @GetMapping("/user/{userId}/average-wellness")
    public ResponseEntity<Double> getAverageWellnessScore(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting average wellness score for user: {} from {} to {}", userId, startDate, endDate);
        
        Double averageScore = emotionalDiaryService.calculateAverageWellnessScore(userId, startDate, endDate);
        
        if (averageScore != null) {
            return ResponseEntity.ok(averageScore);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    /**
     * Check if user has entry for today
     */
    @GetMapping("/user/{userId}/today")
    public ResponseEntity<Boolean> hasEntryForToday(@PathVariable String userId) {
        log.info("Checking if user {} has diary entry for today", userId);
        
        boolean hasEntry = emotionalDiaryService.hasEntryForToday(userId);
        
        return ResponseEntity.ok(hasEntry);
    }
    
    /**
     * Get entries with low mood
     */
    @GetMapping("/low-mood")
    public ResponseEntity<List<EmotionalDiary>> getLowMoodEntries() {
        log.info("Getting entries with low mood");
        
        List<EmotionalDiary> entries = emotionalDiaryService.findLowMoodEntries();
        
        return ResponseEntity.ok(entries);
    }
    
    /**
     * Get entries with high stress
     */
    @GetMapping("/high-stress")
    public ResponseEntity<List<EmotionalDiary>> getHighStressEntries() {
        log.info("Getting entries with high stress");
        
        List<EmotionalDiary> entries = emotionalDiaryService.findHighStressEntries();
        
        return ResponseEntity.ok(entries);
    }
}