package com.observe.os1.models;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "clients")
public class Client extends PanacheMongoEntity {
    public String name;
    public String apiKey;
}
