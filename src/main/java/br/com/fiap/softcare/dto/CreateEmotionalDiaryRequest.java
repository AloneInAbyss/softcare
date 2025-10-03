package br.com.fiap.softcare.dto;

import br.com.fiap.softcare.enums.MoodLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating Emotional Diary entry
 */
@Data
public class CreateEmotionalDiaryRequest {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private String userId;
    
    @NotNull(message = "Data da entrada é obrigatória")
    private LocalDate entryDate;
    
    @NotNull(message = "Nível de humor é obrigatório")
    private MoodLevel moodLevel;
    
    @Min(value = 1, message = "Nível de energia deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de energia deve ser entre 1 e 5")
    private Integer energyLevel;
    
    @Min(value = 1, message = "Nível de stress deve ser entre 1 e 5")
    @Max(value = 5, message = "Nível de stress deve ser entre 1 e 5")
    private Integer stressLevel;
    
    @Min(value = 1, message = "Qualidade do sono deve ser entre 1 e 5")
    @Max(value = 5, message = "Qualidade do sono deve ser entre 1 e 5")
    private Integer sleepQuality;
}