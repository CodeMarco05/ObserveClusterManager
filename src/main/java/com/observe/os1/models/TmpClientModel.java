package com.observe.os1.models;

import com.observe.os1.utils.TmpPasswordGenerator;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "tmp_clients")
public class TmpClientModel extends PanacheEntity {

    @Column
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    //TODO make it so the length of the default password can be configured in configs
    @Column
    private String temporaryPassword = TmpPasswordGenerator.genPassword(4, 3);

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
