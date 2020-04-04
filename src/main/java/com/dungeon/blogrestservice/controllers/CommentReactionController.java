package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.models.CommentRection;
import com.dungeon.blogrestservice.repositories.CommentRectionRepository;
import com.dungeon.blogrestservice.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentReactionController {

    @Autowired
    CommentRectionRepository commentReactionRepository;

//    @RequestMapping(value = "/comment", method = RequestMethod.GET)
//    public Comment getComments() {
//        Comment comment = commentRepository.findById((long)1).get();
//
//        return comment;
//    }

    @RequestMapping(value = "/commentReactions", method = RequestMethod.POST)
    public CommentRection createComment() {
        CommentRection commentRection = new CommentRection();

        commentRection.setComment_id(2);
        commentRection.setAuthor_id(5);
        commentRection.setComment("Prvy bol dost lacny podla mnma");

        commentReactionRepository.save(commentRection);

        return commentRection;
    }
}
