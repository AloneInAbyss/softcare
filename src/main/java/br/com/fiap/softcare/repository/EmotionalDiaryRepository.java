package br.com.fiap.softcare.repository;

import br.com.fiap.softcare.enums.MoodLevel;
import br.com.fiap.softcare.model.EmotionalDiary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmotionalDiary entity
 */
@Repository
public interface EmotionalDiaryRepository extends MongoRepository<EmotionalDiary, String> {
    
    /**
     * Find diary entries by user ID
     */
    List<EmotionalDiary> findByUserId(String userId);
    
    /**
     * Find diary entry by user and date (should be unique)
     */
    Optional<EmotionalDiary> findByUserIdAndEntryDate(String userId, LocalDate entryDate);
    
    /**
     * Find entries for a user within date range
     */
    List<EmotionalDiary> findByUserIdAndEntryDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find recent entries for a user (ordered by date desc)
     */
    List<EmotionalDiary> findByUserIdOrderByEntryDateDesc(String userId);
    
    /**
     * Find entries by mood level
     */
    List<EmotionalDiary> findByMoodLevel(MoodLevel moodLevel);
    
    /**
     * Find entries with low mood (VERY_LOW or LOW)
     */
    @Query("{'moodLevel': {$in: ['VERY_LOW', 'LOW']}}")
    List<EmotionalDiary> findLowMoodEntries();
    
    /**
     * Find entries with high stress (4 or 5)
     */
    @Query("{'stressLevel': {$gte: 4}}")
    List<EmotionalDiary> findHighStressEntries();
    
    /**
     * Find entries for a specific date range across all users
     */
    List<EmotionalDiary> findByEntryDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Count entries by user
     */
    long countByUserId(String userId);
    
    /**
     * Find latest entry for a user
     */
    Optional<EmotionalDiary> findTopByUserIdOrderByEntryDateDesc(String userId);
}