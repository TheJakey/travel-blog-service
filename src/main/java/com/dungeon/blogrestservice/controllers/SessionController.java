package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.LoginForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@RestController
public class SessionController {

    @Autowired
    SessionRepository repository;

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
            if (repository.findByBloggerId(blogger.getId()).isPresent())
                return ResponseEntity.status(400).body("User is already logged in.");

            Session newSession;
            newSession = new Session();

            newSession.setBloggerId(blogger.getId());

            String newToken = "generated_token";
//            String newToken = generateToken();                // TODO: Remove this before submit - TEST ONLY

            newSession.setToken(newToken);

            repository.save(newSession);
            return ResponseEntity.status(200).header("token", newToken).body("user_id:" + blogger.getId());
        }
    }

    // delete token
    @RequestMapping(value = "/sessions/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteToken(@RequestHeader String Token,
                                      @PathVariable long id) {

        Optional<Session> sessionToDelete;
        sessionToDelete = repository.findByBloggerId(id);

        if (!sessionToDelete.isPresent()) {
            return ResponseEntity.status(400).body("User is logged out or does not exist.");
        }
        if (Token.compareTo(sessionToDelete.get().getToken()) == 0) {

            repository.delete(sessionToDelete.get());
            return ResponseEntity.status(200).body("User was successfully logged out.");
        }
        else{
            return ResponseEntity.status(401).body("You are not allowed to log out this user");
    }

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    private String generateToken(){
        int len = 40;
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
