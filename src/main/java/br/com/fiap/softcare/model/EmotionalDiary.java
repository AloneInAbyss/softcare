package br.com.fiap.softcare.model;

import br.com.fiap.softcare.enums.MoodLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * EmotionalDiary entity representing daily emotional check-ins
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "emotional_diaries")
@CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'entryDate': -1}", unique = true)
public class EmotionalDiary {
    
    @Id
    private String id;
    
    @NotNull(message = "ID do usuário é obrigatório")
    @Indexed
    private String userId;
    
    @NotNull(message = "Data da entrada é obrigatória")
    @Indexed
    private LocalDate entryDate;
    
    @NotNull(message = "Nível de humor é obrigatório")
    private MoodLevel moodLevel;
    
    // Additional metrics (1-5 scale)
    @Min(value = 1, message = "Nível de energia deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de energia deve ser entre 1 e 5")
    private Integer energyLevel;
    
    @Min(value = 1, message = "Nível de stress deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de stress deve ser entre 1 e 5")
    private Integer stressLevel;
    
    @Min(value = 1, message = "Qualidade do sono deve ser entre 1 e 5")
    @Max(value = 5, message = "Qualidade do sono deve ser entre 1 e 5")
    private Integer sleepQuality;
    
    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * Calculate a composite wellness score for the day
     */
    public Double calculateWellnessScore() {
        int totalFactors = 0;
        int scoreSum = 0;
        
        if (moodLevel != null) {
            scoreSum += moodLevel.getValue();
            totalFactors++;
        }
        if (energyLevel != null) {
            scoreSum += energyLevel;
            totalFactors++;
        }
        if (stressLevel != null) {
            scoreSum += (6 - stressLevel); // Invert stress (lower stress = higher score)
            totalFactors++;
        }
        if (sleepQuality != null) {
            scoreSum += sleepQuality;
            totalFactors++;
        }
        
        return totalFactors > 0 ? (double) scoreSum / totalFactors : null;
    }
    
    /**
     * Check if this entry indicates potential concern
     */
    public boolean indicatesConcern() {
        return (moodLevel != null && moodLevel.getValue() <= 2) ||
               (stressLevel != null && stressLevel >= 4) ||
               (energyLevel != null && energyLevel <= 2) ||
               (sleepQuality != null && sleepQuality <= 2);
    }
}