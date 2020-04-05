package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.repositories.ArticleTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleTagController  {

    @Autowired
    ArticleTagRepository articleTagRepository;

    private final String adminToken = "TotoJeAdmin69";

    @RequestMapping(value = "/tags", method = RequestMethod.POST)
    public ResponseEntity createNewTagForArticle(
            @RequestParam(value = "name") String tag_name,
            @RequestHeader(value = "token") String token
    ) {
        Tag tag = new Tag();
        Tag just_created_tag;

        if (token == null || new_tag_name == null)
            return ResponseEntity.status(401).body("Missing input data");

        if (new_tag_name.isEmpty())
            return ResponseEntity.status(401).body("Missing input data");

        if (token.compareTo(adminToken) != 0 || token.isEmpty())
            return ResponseEntity.status(401).body("Who are you?");

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

        if (token.compareTo(adminToken) != 0)
            return ResponseEntity.status(401).body("Who are you?");

        if (!tag.isPresent())
            return ResponseEntity.status(400).body("Invalid id");

        tagRepository.delete(tag.get());

        return ResponseEntity.status(200).body("Deleted.");
    }
}
