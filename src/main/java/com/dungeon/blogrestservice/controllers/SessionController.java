package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.forms.LoginForm;
import com.dungeon.blogrestservice.models.Greeting;
import com.dungeon.blogrestservice.models.Session;
import com.dungeon.blogrestservice.repositories.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                if (blogger.getPassword() != pwd)
                    return ResponseEntity.status(401).body("Wrong data.");
                else {
                    // create token
                    if (blogger.getPassword() == pwd) {
                        Session new_session;
                        new_session = new Session();

                        new_session.setBlogger_id(blogger.id);
                        String new_token = "generated_token";
                        new_session.setToken(new_token);

                        return ResponseEntity.status(200).header("token", new_token).body("user_id:" + blogger.id);
                    } else {
                        return ResponseEntity.status(404).body("Zabudli ste osetrit nejaky stav pri prihlaseni");
                    }
                }
            }
        }
    }
    // delete token
    @RequestMapping(value = "/sessions", method = RequestMethod.DELETE)
    public ResponseEntity deleteToken(@RequestHeader String token){
        repository.delete(token);

        return ResponseEntity.status(200).body("User was successfully logged out.");
    }


}
