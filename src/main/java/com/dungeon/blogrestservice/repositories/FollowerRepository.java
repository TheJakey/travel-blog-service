package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Blogger;
import com.dungeon.blogrestservice.models.Follower;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends CrudRepository<Follower, Long> {

    List<Follower> findAllByBloggerId(long id);

    List<Follower> findAllByFollowerId(long id);
}
