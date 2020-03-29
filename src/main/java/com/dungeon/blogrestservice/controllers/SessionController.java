package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.LoginForm;
import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Greeting;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.BloggerRepository;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        if (blogger == null){
            return ResponseEntity.status(400).body("Username does not exist.");
        }
        else {
            if (uname.isEmpty() || pwd.isEmpty())
                return ResponseEntity.status(400).body("Missing attributes.");
            else {
                if (pwd.compareTo(blogger.getPassword()) != 0)
                    return ResponseEntity.status(401).body("Wrong data.");
                else {
                    // create token
                        Session new_session;
                        new_session = new Session();

                        new_session.setBlogger_id(blogger.getId());
                        String new_token = "generated_token";
                        new_session.setToken(new_token);

                        repository.save(new_session);
                        return ResponseEntity.status(200).header("token", new_token).body("user_id:" + blogger.getId());
                    }
                }
            }
        }
    // delete token
    @RequestMapping(value = "/sessions", method = RequestMethod.DELETE)
    public ResponseEntity deleteToken(@RequestHeader String token){

        repository.deleteByToken(token);

        return ResponseEntity.status(200).body("User was successfully logged out.");
    }


}
