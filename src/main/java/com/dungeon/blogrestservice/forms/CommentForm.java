package com.dungeon.blogrestservice.forms;

public class CommentForm {
    private long blogger_id;
    private String comment;

    public long getBlogger_id() {
        return blogger_id;
    }

    public void setBlogger_id(long blogger_id) {
        this.blogger_id = blogger_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
