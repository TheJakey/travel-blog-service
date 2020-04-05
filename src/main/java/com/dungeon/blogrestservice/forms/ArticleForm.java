package com.dungeon.blogrestservice.forms;

import com.dungeon.blogrestservice.models.Comment;
import com.dungeon.blogrestservice.models.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleForm {
    private String title;
    private String article_text;
    private Date published;
    private long blogger_id;
    private int likes;
    private int numberOfPhotosInGallery;
    private List<Tag> tags;
    private List<String> selected_tags;
    private List<Comment> comments;

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getArticle_text() {
        return article_text;
    }

    public void setArticle_text(String article_text) {
        this.article_text = article_text;
    }

    public Date getPublished(){
        return this.published;
    }

    public void setPublished(Date published){
        this.published = published;
    }

    public long getBlogger_id(){
        return this.blogger_id;
    }

    public void setBlogger_id(long blogger_id){
        this.blogger_id = blogger_id;
    }

    public int getLikes() { return likes; }

    public void setLikes(int likes) { this.likes = likes; }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getNumberOfPhotosInGallery() {
        return numberOfPhotosInGallery;
    }

    public void setNumberOfPhotosInGallery(int numberOfPhotosInGallery) {
        this.numberOfPhotosInGallery = numberOfPhotosInGallery;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<String> getSelected_tags() {
        return selected_tags;
    }

    public void setSelected_tags(List<String> selected_tags) {
        this.selected_tags = selected_tags;
    }
}

