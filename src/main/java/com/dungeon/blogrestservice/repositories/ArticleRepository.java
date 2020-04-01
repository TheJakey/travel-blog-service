package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Article;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Long> {
}
