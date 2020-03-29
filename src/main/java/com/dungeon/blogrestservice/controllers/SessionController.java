package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.LoginForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class SessionController {

    @Autowired
    SessionRepository repository;

    @Autowired
    BloggerRepository bloggerRepository;

    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    public ResponseEntity createToken(
            @RequestBody LoginForm loginform) {

        String uname = loginform.getUsername();
        String pwd = loginform.getPassword();

        Blogger blogger = bloggerRepository.findBloggerByUsername(uname);

        if (blogger == null) {
            return ResponseEntity.status(400).body("Username does not exist.");
        }
        if (uname.isEmpty() || pwd.isEmpty())
            return ResponseEntity.status(400).body("Missing attributes.");
        if (pwd.compareTo(blogger.getPassword()) != 0)
            return ResponseEntity.status(401).body("Wrong data.");
        else {
            // create token
            if (repository.findByBloggerId(blogger.getId()) != null)
                return ResponseEntity.status(400).body("User is already logged in.");

            Session newSession;
            newSession = new Session();

            newSession.setBloggerId(blogger.getId());
            String newToken = "generated_token";
            newSession.setToken(newToken);

            repository.save(newSession);
            return ResponseEntity.status(200).header("token", newToken).body("user_id:" + blogger.getId());
        }
    }

    // delete token
    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteToken(@RequestHeader String token,
                                      @PathVariable long id) {

        Session sessionToDelete;
        sessionToDelete = repository.findByBloggerId(id);

        if (sessionToDelete != null) {
            if (token.compareTo(sessionToDelete.getToken()) == 0) {
                repository.delete(sessionToDelete);
                return ResponseEntity.status(200).body("User was successfully logged out.");
            }
        }
        return ResponseEntity.status(500).body("Oops. Something went wrong.");
    }

}
