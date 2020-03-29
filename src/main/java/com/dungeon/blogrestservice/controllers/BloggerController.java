package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.RegisterForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.GreetingRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BloggerController {
    private final static String MAPPING_VALUE = "/bloggers";

    @Autowired
    BloggerRepository repository;

    @Autowired
    SessionRepository sessionRepository;

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

    @RequestMapping(value = MAPPING_VALUE + "/photos", method = RequestMethod.POST)
    public ResponseEntity uploadNewPhoto(
            @RequestParam(value = "bloggerId") long bloggerId,
            @RequestParam(value = "type") String type,
            @RequestHeader(value = "token") String requestToken,                // TODO: FIX token to Token with VIKI !!
            @RequestBody byte[] photo
    ) {
        Blogger blogger;
        Optional<Session> session_in_repo = sessionRepository.findById(bloggerId);
        Session session;

        // check if blogger, whose photo is requested to change is logged-in
        if (session_in_repo.isPresent())
            session = session_in_repo.get();
        else
            return ResponseEntity.status(400).body("Invalid bloggerId - blogger not logged-in");

        // verify if token for provided bloggerId matches to requestToken from header
        if (session.getToken().compareTo(requestToken) != 0)
            return ResponseEntity.status(403).body("You are forbidden to change this photo");

        // blogger is logged in, so blogger MUST exists in bloggers table.. so .get() right away
        blogger = repository.findById(bloggerId).get();






        return ResponseEntity.status(200).body("");
    }
}
