package com.dungeon.blogrestservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "articles")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "blogger_id")
    private long bloggerId;

    @Column(name = "title")
    private String title;

    @Column(name = "article_text", length = 100000)
    private String articleText;

    @Column(name = "published")
    private Date published;

    @Column(name = "likes")
    private int likes;

    protected Article(){}

    public Article(long bloggerId, String title, String articleText, Date published, int likes){
        this.bloggerId = bloggerId;
        this.title = title;
        this.articleText = articleText;
        this.published = published;
        this.likes = likes;
    }

    public long getBloggerId() {
        return bloggerId;
    }

    public void setBloggerId(long bloggerId) {
        this.bloggerId = bloggerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String article_text) {
        this.articleText = article_text;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int numberOfLikes) {
        this.likes = numberOfLikes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
