package com.dungeon.blogrestservice.models;


import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Bloggers")
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public Blogger(String username, String aboutMe, String email, String password) {
        this.username = username;
        this.aboutMe = aboutMe;
        this.email = email;
        this.password = password;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public long getId() {return id;}

    public String getUsername() {
        return username;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
