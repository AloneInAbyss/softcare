package br.com.fiap.softcare.enums;

/**
 * Enum representing different mood levels for emotional diary entries
 */
public enum MoodLevel {
    VERY_LOW(1, "Muito Baixo", "😞"),
    LOW(2, "Baixo", "😔"),
    NEUTRAL(3, "Neutro", "😐"),
    GOOD(4, "Bom", "🙂"),
    VERY_GOOD(5, "Muito Bom", "😊");

    private final int value;
    private final String description;
    private final String emoji;

    MoodLevel(int value, String description, String emoji) {
        this.value = value;
        this.description = description;
        this.emoji = emoji;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getEmoji() {
        return emoji;
    }

    public static MoodLevel fromValue(int value) {
        for (MoodLevel mood : MoodLevel.values()) {
            if (mood.value == value) {
                return mood;
            }
        }
        throw new IllegalArgumentException("Invalid mood value: " + value);
    }
}