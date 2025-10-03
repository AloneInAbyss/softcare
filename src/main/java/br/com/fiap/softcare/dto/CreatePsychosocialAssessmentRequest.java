package br.com.fiap.softcare.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * DTO for creating Psychosocial Assessment
 */
@Data
public class CreatePsychosocialAssessmentRequest {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private String userId;
    
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
}