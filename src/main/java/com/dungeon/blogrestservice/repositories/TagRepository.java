package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Tag;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, Long> {}
