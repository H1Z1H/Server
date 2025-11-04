package com.brick.brick_server.domain.admin.service;

import com.brick.brick_server.domain.admin.presentation.dto.req.LoginRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.SetArticleTimeRequest;
import com.brick.brick_server.domain.admin.presentation.dto.req.UpdateArticleRequest;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleWithFluctuation;
import com.brick.brick_server.domain.admin.service.coin.ArticleGenerator;
import com.brick.brick_server.domain.admin.service.coin.ArticleRegistrar;
import com.brick.brick_server.domain.admin.service.implementation.AdminCreator;
import com.brick.brick_server.domain.admin.service.implementation.AdminUpdater;
import com.brick.brick_server.domain.auth.presentation.dto.res.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandAdminService {
    private final AdminCreator adminCreator;
    private final AdminUpdater adminUpdater;
    private final ArticleGenerator articleGenerator;
    private final ArticleRegistrar articleRegistrar;

    public LoginResponse loginAdmin(LoginRequest req) {
        return adminCreator.login(req);
    }

    public void updateArticle(UpdateArticleRequest request) {
        adminUpdater.updateArticle(request);
    }

    public void setArticleTime(SetArticleTimeRequest request) {
        adminUpdater.setArticleTime(request);
    }

    public void publishArticle(Long articleId) {
        adminUpdater.publishArticle(articleId);
    }

    public void generateNewsWithCoinImpact(){
        List<ArticleWithFluctuation> articles = articleGenerator.generateArticles();
        articleRegistrar.registerArticles(articles);
    }
}
