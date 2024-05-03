package ori.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private final String roleName;

    private UserRole(String role) {
        this.roleName = role;
    }

    public static UserRole getEnum(String role) {
        for (UserRole v : values())
            if (v.getRoleName().equals(role)) return v;
        throw new IllegalArgumentException();
    }

}
