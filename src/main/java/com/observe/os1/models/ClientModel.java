package com.observe.os1.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "clients")
public class ClientModel extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String name;

    @Column(nullable = false)
    public String passwordHash;

    public String roles; // e.g. "admin,user" or "user,admin" or "admin" etc.

    public static ClientModel findByName(String name) {
        return find("name", name).firstResult();
    }

    public Set<String> getRoles() {
        if (roles == null || roles.isEmpty()) {
            return Set.of(); // Return an empty set if roles are not defined
        }
        return Set.of(roles.split(","));
    }
}