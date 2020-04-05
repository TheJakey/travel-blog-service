package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Tag;
import org.springframework.data.repository.CrudRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    Optional<Tag> findByTag(String tag);
}
