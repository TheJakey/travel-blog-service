package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.CommentForm;
import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.Comment;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.ArticleRepository;
import com.dungeon.blogrestservice.repositories.CommentRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    ArticleRepository articleRepository;

    @RequestMapping(value = "/articles/{article_id}/comments", method = RequestMethod.POST)
    public ResponseEntity createComment(
            @PathVariable long article_id,
            @RequestBody CommentForm commentForm
    ) {
        Optional<Session> session;
        Optional<Article> article;
        Comment new_comment = new Comment();
        long bloggerId = commentForm.getBlogger_id();
        String comment_text = commentForm.getComment();

        // any nulls?
        if (bloggerId == 0 || comment_text == null)
            return ResponseEntity.status(400).body("Missing attribute");

        // has comment body?
        if (comment_text.isEmpty())
            return ResponseEntity.status(400).body("Missing attribute");

        // article exists?
        article = articleRepository.findById(article_id);
        if (!article.isPresent())
            return ResponseEntity.status(400).body("Cannot comment non-existing article");

        // is user logged in?
        session = sessionRepository.findByBloggerId(bloggerId);
        if (!session.isPresent())
            return ResponseEntity.status(401).body("Only logged users can comment");

        // all good, add comment to DB
        new_comment.setArticleId(article_id);
        new_comment.setAuthorId(bloggerId);
        new_comment.setComment(comment_text);

        commentRepository.save(new_comment);

        return ResponseEntity.status(201).body("");
    }
}
