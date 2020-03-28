package com.dungeon.blogrestservice.controllers;

import com.dungeon.blogrestservice.repositories.GreetingRepository;
import com.dungeon.blogrestservice.models.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GreetingController {

    @Autowired
    GreetingRepository repository;

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public ResponseEntity<List<Greeting>> getGreetings() {
        List<Greeting> greetings;

        greetings = repository.findAll();

        return ResponseEntity.status(200).body(greetings);
    }

    @RequestMapping(value = "/greeting", method = RequestMethod.POST)
    public ResponseEntity saveGreetings(@RequestParam(value = "content", defaultValue = "") String content) {
        Greeting new_greeting;

        if (content.length() == 0)
            return ResponseEntity.status(400).body("Si hlupy. My potrebujeme CONTENT!!!!");

        new_greeting = new Greeting(content);
        repository.save(new_greeting);

        return ResponseEntity.status(201).body("");
    }

}
