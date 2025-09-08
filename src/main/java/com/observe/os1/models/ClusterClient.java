package com.observe.os1.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@ApplicationScoped
@Entity
@Table(name = "cluster_clients")
public class ClusterClient extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String name;

    @Column(nullable = false)
    public String hostOrIp;

    @Column(nullable = false)
    public int port;

    @Column(nullable = false)
    public String clientToken;

    public ClusterClient(String name, String hostOrIp, int port, String clientToken) {
        this.name = name;
        this.hostOrIp = hostOrIp;
        this.port = port;
        this.clientToken = clientToken;
    }

    public ClusterClient() {
    }

    public void createClient() {
        persist(this);
    }

    public String getUriByName(String nodeName) {
        ClusterClient client = find("name", nodeName).firstResult();
        if (client != null) {
            String url = "http://" + client.hostOrIp + ":" + client.port;
            return url;
        }
        return null;
    }
}
