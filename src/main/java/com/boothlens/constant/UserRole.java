package com.boothlens.constant;

/**
 * Matches {@code user_tb.role}: 1=Client, 2=Contractor (reserved), 3=Admin, 4=SuperAdmin.
 * Aligns with Design.md RBAC levels for MVP.
 */
public enum UserRole {
    CLIENT(1),
    CONTRACTOR(2),
    ADMIN(3),
    SUPER_ADMIN(4);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return this.name();
    }

    public static UserRole fromValue(int value) {
        for (UserRole role : UserRole.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid UserRole value: " + value);
    }

    public static String getNameByValue(int value) {
        return fromValue(value).getName();
    }
}
