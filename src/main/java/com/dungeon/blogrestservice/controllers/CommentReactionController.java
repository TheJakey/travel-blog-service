package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.CommentForm;
import com.dungeon.blogrestservice.models.Comment;
import com.dungeon.blogrestservice.models.CommentRection;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.CommentRectionRepository;
import com.dungeon.blogrestservice.repositories.CommentRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Optional;

@RestController
public class CommentReactionController {

    @Autowired
    CommentRectionRepository commentReactionRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    CommentRepository commentRepository;

    @RequestMapping(value = "/articles/{article_id}/comments/{comment_id}/comment_reaction", method = RequestMethod.POST)
    public ResponseEntity createCommentReaction(
            @PathVariable long article_id,
            @PathVariable long comment_id,
            @RequestBody CommentForm commentForm
            ) {
        Optional<Session> session;
        Optional<Comment> comment_instance;
        CommentRection new_comment_reaction = new CommentRection();
        long bloggerId = commentForm.getBlogger_id();
        String comment_text = commentForm.getComment();
        CommentRection just_added_comment_reaction;

        // any nulls?
        if (bloggerId == 0 || comment_text == null)
            return ResponseEntity.status(400).body("Missing attribute");

        // has comment body?
        if (comment_text.isEmpty())
            return ResponseEntity.status(400).body("Missing attribute");

        // is there comment, that we are trying react to?
        comment_instance = commentRepository.findById(article_id);
        if (!comment_instance.isPresent())
            return ResponseEntity.status(400).body("Cannot react to non-existing comment");

        // is user logged in?
        session = sessionRepository.findByBloggerId(bloggerId);
        if (!session.isPresent())
            return ResponseEntity.status(401).body("Only logged users can comment");

        // all good, add comment to DB
        new_comment_reaction.setCommentId(comment_id);
        new_comment_reaction.setAuthorId(bloggerId);
        new_comment_reaction.setComment(comment_text);
        new_comment_reaction.setPublished(Calendar.getInstance().getTime());

        just_added_comment_reaction = commentReactionRepository.save(new_comment_reaction);

        return ResponseEntity.status(201).body(just_added_comment_reaction.getId());
    }
}
