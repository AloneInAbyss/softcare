package br.com.fiap.softcare.enums;

/**
 * Enum representing the different roles a user can have in the system
 */
public enum UserRole {
    EMPLOYEE("EMPLOYEE", "Funcionário"),
    MANAGER("MANAGER", "Gestor"),
    SYSTEM_ADMIN("SYSTEM_ADMIN", "Administrador do Sistema");

    private final String code;
    private final String displayName;

    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role code: " + code);
    }
}