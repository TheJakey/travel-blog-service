package com.dungeon.blogrestservice.models;

import com.dungeon.blogrestservice.forms.LoginForm;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.swing.*;
import java.io.Serializable;

@Entity
@Table(name = "sessions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "blogger_id")
    private long blogger_id;

    @Column(name = "token")
    private String token;

    public Session() {}

    public Session(Long blogger_id, String token){
        this.blogger_id = blogger_id;
        this.token = token;
    }

    public long getId() {
        return this.id;
    }

    public long getBlogger_id() {
        return this.blogger_id;
    }

    public String getToken() {
        return this.token;
    }

    public void setBlogger_id(long blogger_id) {
        this.blogger_id = blogger_id;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
