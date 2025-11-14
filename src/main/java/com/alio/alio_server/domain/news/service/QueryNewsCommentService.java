package com.alio.alio_server.domain.news.service;

import com.alio.alio_server.domain.news.domain.NewsComment;
import com.alio.alio_server.domain.news.domain.repository.NewsCommentRepository;
import com.alio.alio_server.domain.news.presentation.dto.res.NewsCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryNewsCommentService {

    private final NewsCommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<NewsCommentResponse> getRecentComments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<NewsComment> comments = commentRepository.findTopNOrderByCreatedAtDesc(pageable);
        return comments.stream()
                .map(NewsCommentResponse::from)
                .collect(Collectors.toList());
    }
}

