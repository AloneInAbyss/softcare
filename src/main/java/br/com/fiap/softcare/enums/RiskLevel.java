package br.com.fiap.softcare.enums;

/**
 * Enum representing different risk levels for psychosocial assessments
 */
public enum RiskLevel {
    LOW("LOW", "Baixo", "#4CAF50"),
    MODERATE("MODERATE", "Moderado", "#FF9800"),
    HIGH("HIGH", "Alto", "#F44336"),
    CRITICAL("CRITICAL", "Crítico", "#9C27B0");

    private final String code;
    private final String description;
    private final String color;

    RiskLevel(String code, String description, String color) {
        this.code = code;
        this.description = description;
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public static RiskLevel fromCode(String code) {
        for (RiskLevel level : RiskLevel.values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid risk level code: " + code);
    }
}