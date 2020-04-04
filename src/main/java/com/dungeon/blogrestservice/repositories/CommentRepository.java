package com.dungeon.blogrestservice.repositories;

import com.dungeon.blogrestservice.models.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByArticleId(long articleId);
}
