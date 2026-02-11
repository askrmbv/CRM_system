package models;

// User roles
public enum Role {
    ADMIN,    // Full access to everything
    MANAGER,  // Can add and edit, but not delete
    EDITOR    // View and edit only
}
