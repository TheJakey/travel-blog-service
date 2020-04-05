package com.dungeon.blogrestservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "followers")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Follower implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "blogger_id")
    private long bloggerId;

    @Column(name = "follower_id")
    private long followerId;

    protected Follower(){};

    public Follower(long bloggerId, long followerId){
        this.bloggerId = bloggerId;
        this.followerId = followerId;
    };

    public long getBloggerId() {
        return bloggerId;
    }

    public void setBloggerId(long bloggerId) {
        this.bloggerId = bloggerId;
    }

    public long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(long followerId) {
        this.followerId = followerId;
    }
}
