package com.dungeon.blogrestservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "greetings")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Greeting implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content")
    private String content;

    protected Greeting() {}

    public Greeting(String content) {
        this.id = 5;
        this.content = content;
    }

    public long getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String new_content) {
        this.content = new_content;
    }
}
