package com.dungeon.blogrestservice.forms;

import java.util.Date;

public class ArticleForm {
    public String title;
    public String articleText;
    public Date published;
    public long bloggerId;
    public int numberOfLikes;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public Date getPublished(){
        return this.published;
    }

    public void setPublished(Date published){
        this.published = published;
    }

    public long getBloggerId(){
        return this.bloggerId;
    }

    public void setBloggerId(long bloggerId){
        this.bloggerId = bloggerId;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }
}

