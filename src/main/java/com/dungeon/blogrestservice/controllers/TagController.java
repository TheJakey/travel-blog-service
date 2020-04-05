package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.models.Tag;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TagController {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    SessionRepository sessionRepository;

    @RequestMapping(value = "/tags", method = RequestMethod.POST)
    public ResponseEntity createNewTag(
            @RequestParam(value = "name") String new_tag_name,
            @RequestParam(value = "bloggerId") long bloggerId,
            @RequestHeader(value = "token") String token
    ) {
        Tag tag = new Tag();
        Tag just_created_tag;
        Optional<Session> optionalSession = sessionRepository.findByBloggerId(bloggerId);
//        SessionHandler sessionHandler = new SessionHandler(bloggerId, requestToken, optionalSession);
//
//        if(!sessionHandler.isBloggerLoggedIn())
//            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");
//
//        if(!sessionHandler.isTokenMatching())
//            return ResponseEntity.status(403).body("You are forbidden to change this photo");
//
//        if (token == null || new_tag_name == null)
//            return ResponseEntity.status(401).body("Missing input data");
//
//        if (new_tag_name.isEmpty())
//            return ResponseEntity.status(401).body("Missing input data");
//
//        if (token.isEmpty())
//            return ResponseEntity.status(401).body("Who are you?");

        tag.setTag(new_tag_name);

        just_created_tag = tagRepository.save(tag);

        return ResponseEntity.status(201).body(just_created_tag.getId());
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ResponseEntity getAllTags() {
        Iterable<Tag> tags = tagRepository.findAll();

        return ResponseEntity.status(200).body(tags);
    }

    @RequestMapping(value = "/tags", method = RequestMethod.DELETE)
    public ResponseEntity deleteTag(
            @RequestParam(value = "id") long id,
            @RequestHeader(value = "token") String token
    ) {
        Optional<Tag> tag = tagRepository.findById(id);

        if (token == null)
            return ResponseEntity.status(401).body("Who are you?");

        if (token.compareTo("aha") != 0)
            return ResponseEntity.status(401).body("Who are you?");

        if (!tag.isPresent())
            return ResponseEntity.status(400).body("Invalid id");

        tagRepository.delete(tag.get());

        return ResponseEntity.status(200).body("Deleted.");
    }
}
