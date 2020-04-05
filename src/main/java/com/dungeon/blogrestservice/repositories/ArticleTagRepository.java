package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.ArticleTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleTagRepository extends CrudRepository<ArticleTag, Long> {
    List<ArticleTag> findAllByArticleId(long articleId);
}
