package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.ArticlePhoto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticlePhotoRepository extends CrudRepository<ArticlePhoto, Long> {
    List<ArticlePhoto> findAllByArticleId(long article_id);
}
