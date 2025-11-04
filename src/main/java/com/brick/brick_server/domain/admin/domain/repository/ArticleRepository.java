package com.brick.brick_server.domain.admin.domain.repository;

import com.brick.brick_server.domain.admin.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByPublishTrue();

    List<Article> findAllByPublishFalse();
}