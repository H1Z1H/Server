package com.alio.alio_server.domain.news.service;

import com.alio.alio_server.domain.news.domain.NewsComment;
import com.alio.alio_server.domain.news.domain.repository.NewsCommentRepository;
import com.alio.alio_server.domain.news.presentation.dto.req.CreateNewsCommentRequest;
import com.alio.alio_server.domain.news.presentation.dto.res.NewsCommentResponse;
import com.alio.alio_server.domain.user.domain.Users;
import com.alio.alio_server.domain.user.domain.repository.UserRepository;
import com.alio.alio_server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommandNewsCommentService {

    private final NewsCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public NewsCommentResponse createComment(Long userId, CreateNewsCommentRequest request) {
        if (userId == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        Users user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        NewsComment comment = NewsComment.builder()
                .user(user)
                .content(request.content().trim())
                .build();

        NewsComment saved = commentRepository.save(comment);
        return NewsCommentResponse.from(saved);
    }
}

