package com.observe.os1.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_info")

public class SystemInfo extends PanacheEntity {

    @Column(nullable = false)
    public LocalDateTime startDateTime = LocalDateTime.now();

    @Column(nullable = false)
    public Time tmpPasswordExpiration = Time.valueOf("00:05:00"); // 5 minutes
}
