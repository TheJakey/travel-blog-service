package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Long> {
    void deleteByToken(String token);
    Session findByBloggerId(long blogger_id);
}
