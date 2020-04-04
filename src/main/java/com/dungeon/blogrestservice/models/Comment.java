package com.dungeon.blogrestservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comments")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "article_id")
    private long articleId;

    @Column(name = "author_id")
    private long authorId;

    @Column(name = "comment", length = 4000)
    private String comment;

    @Column(name = "published")
    private Date published;

    @OneToMany(mappedBy = "comment_id")
    private List<CommentRection> commentRection;

    public Comment(){}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public List<CommentRection> getCommentRection() {
        return commentRection;
    }

    public void setCommentRection(List<CommentRection> commentRection) {
        this.commentRection = commentRection;
    }
}
