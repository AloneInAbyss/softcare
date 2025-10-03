package br.com.fiap.softcare.model;

import br.com.fiap.softcare.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;

/**
 * PsychosocialAssessment entity representing user's psychosocial risk evaluation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "psychosocial_assessments")
public class PsychosocialAssessment {
    
    @Id
    private String id;
    
    @NotNull(message = "ID do usuário é obrigatório")
    @Indexed
    private String userId;
    
    // Assessment scores (1-5 scale)
    @Min(value = 1, message = "Score deve ser entre 1 e 5")
    @Max(value = 5, message = "Score deve ser entre 1 e 5")
    private Integer workStressLevel;
    
    @Min(value = 1, message = "Score deve ser entre 1 e 5")
    @Max(value = 5, message = "Score deve ser entre 1 e 5")
    private Integer workLifeBalance;
    
    @Min(value = 1, message = "Score deve ser entre 1 e 5")
    @Max(value = 5, message = "Score deve ser entre 1 e 5")
    private Integer jobSatisfaction;
    
    @Min(value = 1, message = "Score deve ser entre 1 e 5")
    @Max(value = 5, message = "Score deve ser entre 1 e 5")
    private Integer relationshipWithColleagues;
    
    @Min(value = 1, message = "Score deve ser entre 1 e 5")
    @Max(value = 5, message = "Score deve ser entre 1 e 5")
    private Integer personalWellbeing;
    
    // Calculated fields
    private Double overallScore;
    private RiskLevel riskLevel;
    
    @Builder.Default
    private Boolean isComplete = false;
    
    // Audit fields
    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * Calculate overall score based on individual assessment scores
     */
    public void calculateOverallScore() {
        if (workStressLevel != null && workLifeBalance != null && jobSatisfaction != null && 
            relationshipWithColleagues != null && personalWellbeing != null) {
            
            // Work stress is inverted (higher stress = lower score)
            double adjustedWorkStress = 6 - workStressLevel;
            
            this.overallScore = (adjustedWorkStress + workLifeBalance + jobSatisfaction + 
                               relationshipWithColleagues + personalWellbeing) / 5.0;
            
            this.isComplete = true;
        }
    }
    
    /**
     * Determine risk level based on overall score
     */
    public void determineRiskLevel() {
        if (overallScore == null) {
            calculateOverallScore();
        }
        
        if (overallScore != null) {
            if (overallScore >= 4.0) {
                this.riskLevel = RiskLevel.LOW;
            } else if (overallScore >= 3.0) {
                this.riskLevel = RiskLevel.MODERATE;
            } else if (overallScore >= 2.0) {
                this.riskLevel = RiskLevel.HIGH;
            } else {
                this.riskLevel = RiskLevel.CRITICAL;
            }
        }
    }
}