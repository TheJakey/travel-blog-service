package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.RegisterForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.GreetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BloggerController {
    private final static String MAPPING_VALUE = "/bloggers";

    @Autowired
    BloggerRepository repository;

    @RequestMapping(value = MAPPING_VALUE + "/{id}", method = RequestMethod.GET)
    public ResponseEntity getBlogger(@PathVariable long id) {
        Optional<Blogger> blogger;

        blogger = repository.findById(id);

        if (!blogger.isPresent())
            return ResponseEntity.status(400).body("Invalid ID - User with such ID doesn't exists");

        return ResponseEntity.status(200).header("Token", "totojetoken").body(blogger.get());
    }

    @RequestMapping(value = MAPPING_VALUE, method = RequestMethod.POST)
    public ResponseEntity createBlogger(@RequestBody RegisterForm registerForm) {
        Blogger new_blogger;
        String username = registerForm.getUsername();
        String email = registerForm.getEmail();
        Boolean password_empty = registerForm.getPassword().isEmpty();

        // verify if all mandatory data are provided
        if (username.isEmpty() || email.isEmpty() || password_empty)
            return ResponseEntity.status(400).body("Missing mandatory field");

        // verify if username already exists
        if (repository.findBloggerByUsername(username) != null)
            return ResponseEntity.status(400).body("Username already exists");

        new_blogger = new Blogger(username, registerForm.getAbout_me(), email, registerForm.getPassword());

        repository.save(new_blogger);

        return ResponseEntity.status(201).body("");
    }
}
