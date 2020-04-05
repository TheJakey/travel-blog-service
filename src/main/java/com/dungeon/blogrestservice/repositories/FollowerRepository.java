package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Follower;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends CrudRepository<Follower, Long> {

    Iterable<Follower> findAllByBloggerId(long id);

    Iterable<Follower> findAllByFollowerId(long id);
}
