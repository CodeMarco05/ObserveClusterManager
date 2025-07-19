package com.observe.os1.v1.metricModels;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cpu_metrics")
public class CpuMetrics extends PanacheEntity {

    @Column
    public String clientId;
}
