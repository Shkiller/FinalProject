package main.model;

public enum Permission {
    USER("user:write"),
    ANYONE("user:nothing"),
    MODERATE("user:moderate");
    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
