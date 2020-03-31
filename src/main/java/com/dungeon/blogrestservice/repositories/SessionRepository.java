package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, Long> {
    void deleteByToken(String token);
    Optional<Session> findByBloggerId(long blogger_id);
}
