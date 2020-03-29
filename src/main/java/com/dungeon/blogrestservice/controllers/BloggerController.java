package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BloggerController {
    private final static String MAPPING_VALUE = "/greeting";

    @Autowired
    BloggerRepository repository;

    @RequestMapping(value = MAPPING_VALUE, method = RequestMethod.GET)
    public ResponseEntity<Blogger> getBlogger(@RequestParam(value = "id") long id) {
        Optional<Blogger> blogger;

        blogger = repository.findById(id);

        return ResponseEntity.status(200).body(blogger.get());
    }

    @RequestMapping(value = MAPPING_VALUE, method = RequestMethod.POST)
    public ResponseEntity createBlogger() {

        return ResponseEntity.status(201).body("");
    }
}
