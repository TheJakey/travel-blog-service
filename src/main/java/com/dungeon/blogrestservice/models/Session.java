package com.dungeon.blogrestservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sessions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "blogger_id")
    private long bloggerId;

    @Column(name = "token")
    private String token;

    public Session() {}

    public Session(Long blogger_id, String token){
        this.bloggerId = blogger_id;
        this.token = token;
    }

    public long getId() {
        return this.id;
    }

    public long getBloggerId() {
        return this.bloggerId;
    }

    public String getToken() {
        return this.token;
    }

    public void setBloggerId(long blogger_id) {
        this.bloggerId = blogger_id;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
