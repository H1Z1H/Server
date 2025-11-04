package com.brick.brick_server.domain.admin.service.implementation;

import com.brick.brick_server.domain.admin.domain.Article;
import com.brick.brick_server.domain.admin.domain.repository.ArticleRepository;
import com.brick.brick_server.domain.admin.exception.ArticleNotFoundException;
import com.brick.brick_server.domain.admin.presentation.dto.req.SetArticleTimeRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.UpdateArticleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUpdater {
    private final ArticleRepository articleRepository;

    public void updateArticle(UpdateArticleRequest request) {
        Article article = articleRepository.findById(request.id())
                .orElseThrow(ArticleNotFoundException::new);

        Article updated = Article.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(request.content())
                .date(article.getDate())
                .time(article.getTime())
                .build();

        articleRepository.save(updated);
    }

    public void setArticleTime(SetArticleTimeRequest request) {
        Article article = articleRepository.findById(request.id())
                .orElseThrow(ArticleNotFoundException::new);

        Article updated = Article.builder()
                .id(article.getId())
                .content(article.getContent())
                .date(request.date())
                .time(request.time())
                .build();

        articleRepository.save(updated);
    }

    public void publishArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        article.publish();
    }
}