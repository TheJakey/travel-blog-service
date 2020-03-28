package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Greeting;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GreetingRepository extends CrudRepository<Greeting, Long> {
    List<Greeting> findAll();
}
