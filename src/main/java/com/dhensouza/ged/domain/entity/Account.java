package com.dhensouza.ged.domain.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    protected Account() {
    }

    public Account(String username, String password, String role, String tenantId) {
        this.id = UUID.randomUUID();
        this.setUsername(username);
        this.password = password;
        this.role = role;
        this.tenantId = tenantId;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getTenantId() {
        return tenantId;
    }


    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void changeRole(String newRole) {
        this.role = newRole;
    }

    public static Account create(String username, String password, String role, String tenantId) {
        return new Account(username, password, role, tenantId);
    }
}
