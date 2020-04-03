package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.LoginForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import com.dungeon.blogrestservice.security.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Optional;

@RestController
public class SessionController {

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    BloggerRepository bloggerRepository;

    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    public ResponseEntity createToken(@RequestBody LoginForm loginform) {

        String uname = loginform.getUsername();
        String pwd = loginform.getPassword();

        Blogger blogger = bloggerRepository.findBloggerByUsername(uname);

        if (blogger == null)
            return ResponseEntity.status(400).body("Username does not exist.");
        if (uname.isEmpty() || pwd.isEmpty())
            return ResponseEntity.status(400).body("Missing attributes.");
        if (pwd.compareTo(blogger.getPassword()) != 0)
            return ResponseEntity.status(401).body("Wrong data.");
        else {
            // create token
            if (sessionRepository.findByBloggerId(blogger.getId()).isPresent())
                return ResponseEntity.status(400).body("User is already logged in.");

            Session newSession;
            newSession = new Session();

            newSession.setBloggerId(blogger.getId());

            String newToken = "generated_token";
//            String newToken = generateToken();                // TODO: Remove this before submit - TEST ONLY

            newSession.setToken(newToken);

            sessionRepository.save(newSession);
            return ResponseEntity.status(200).header("token", newToken).body("user_id:" + blogger.getId());
        }
    }


    // delete token
    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteToken(@RequestHeader String Token,
                                      @PathVariable long id) {

        Optional<Session> optionalSession = sessionRepository.findByBloggerId(id);
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(id);
        Session session;
        Blogger blogger;

        SessionHandler sessionHandler = new SessionHandler(id, Token, optionalSession);

        if(!sessionHandler.isBloggerLoggedIn())
            return ResponseEntity.status(400).body("Invalid id - blogger not logged-in");

        if(!sessionHandler.isTokenMatching())
            return ResponseEntity.status(403).body("You are forbidden to manipulate with this blogger");

        if (optionalBlogger.isPresent())
            blogger = optionalBlogger.get();
        else
            return ResponseEntity.status(400).body("Invalid id - blogger is logged in, but not registered");

        sessionRepository.delete(optionalSession.get());
        return ResponseEntity.status(200).body("User was successfully logged out.");
    }

        private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        private static SecureRandom rnd = new SecureRandom();

        private String generateToken () {
            int len = 40;
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++)
                sb.append(AB.charAt(rnd.nextInt(AB.length())));
            return sb.toString();
        }
    }
