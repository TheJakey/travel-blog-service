package com.dungeon.blogrestservice.models;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Bloggers")
public class Blogger implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "about_me", length = 4000)
    private String aboutMe;

    @Column(name = "profile_photo")
    private byte[] profilePhoto;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    protected Blogger() {}
}
