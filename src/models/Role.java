package models;

// User roles for access control system
public enum Role {
    ADMIN,    // Full access to everything
    MANAGER,  // Can add and edit, but not delete
    EDITOR    // View and edit only
}
