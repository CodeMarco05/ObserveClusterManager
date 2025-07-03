package com.observe.os1.models;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_info")
@SequenceGenerator(
        name = "system_info_seq",
        sequenceName = "system_info_seq",
        allocationSize = 1
)
public class SystemInfo extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_info_seq")
    public Long id;

    @Column(nullable = false)
    public LocalDateTime startTime = LocalDateTime.now();

    @Column(nullable = false)
    public Time tmpPasswordExpiration = Time.valueOf("00:05:00"); // 5 minutes
}
