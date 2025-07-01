package com.observe.os1.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "clients")
public class ClientModel extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(name = "api_key", nullable = false, unique = true)
    public String apiKey;
}