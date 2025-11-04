package com.brick.brick_server.domain.admin.service;

import com.brick.brick_server.domain.admin.domain.Article;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleContentResponse;
import com.brick.brick_server.domain.admin.presentation.dto.res.CoinFluctuationResponse;
import com.brick.brick_server.domain.admin.service.implementation.AdminReader;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAdminService {

    private final AdminReader adminReader;

    public List<Article> getAllArticles() {
        return adminReader.getAllArticles();
    }

    public List<Article> getAllAdminArticles() {
        return adminReader.getAllAdminArticles();
    }

    public ArticleContentResponse getArticleDetail(Long articleId) {
        return adminReader.getArticleDetail(articleId);
    }


    public List<CoinFluctuationResponse> getAllCoinFluctuations() {
        return adminReader.getAllCoinFluctuations();
    }
}
