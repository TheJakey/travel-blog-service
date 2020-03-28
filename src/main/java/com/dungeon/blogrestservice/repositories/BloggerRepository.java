package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Blogger;
import org.springframework.data.repository.CrudRepository;

public interface BloggerRepository extends CrudRepository<Blogger, Long> {
}
