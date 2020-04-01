package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Article;
import com.dungeon.blogrestservice.models.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    Optional<Article> findByBloggerId(long blogger_id);
}
