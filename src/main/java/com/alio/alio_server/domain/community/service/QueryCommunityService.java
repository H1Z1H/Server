package com.alio.alio_server.domain.community.service;

import com.alio.alio_server.domain.community.domain.CommunityCaseType;
import com.alio.alio_server.domain.community.domain.CommunityPost;
import com.alio.alio_server.domain.community.domain.repository.CommunityPostRepository;
import com.alio.alio_server.domain.community.exception.CommunityPostNotFoundException;
import com.alio.alio_server.domain.community.presentation.dto.res.CaseArchiveResponse;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostCardResponse;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostListResponse;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryCommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunitySummaryService summaryService;

    public List<CommunityPostListResponse> getPosts(String tag) {
        List<CommunityPost> posts = StringUtils.hasText(tag)
                ? postRepository.findByTags_NameIgnoreCaseOrderByCreatedAtDesc(tag)
                : postRepository.findAllByOrderByCreatedAtDesc();

        return posts.stream()
                .map(CommunityPostListResponse::from)
                .toList();
    }

    public CommunityPostResponse getPost(Long postId) {
        CommunityPost post = postRepository.findWithTagsById(postId)
                .orElseThrow(CommunityPostNotFoundException::new);
        return CommunityPostResponse.from(post);
    }

    public List<CommunityPostCardResponse> getCards(int limit) {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .map(CommunityPostCardResponse::from)
                .toList();
    }

    public List<CaseArchiveResponse> getCaseArchive() {
        return Arrays.stream(CommunityCaseType.values())
                .map(this::buildArchive)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<CaseArchiveResponse> buildArchive(CommunityCaseType caseType) {
        List<CommunityPost> posts = postRepository.findByCaseTypeOrderByCreatedAtDesc(caseType);
        if (posts.isEmpty()) {
            return Optional.empty();
        }

        String combined = posts.stream()
                .map(CommunityPost::getSummary)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(" "));

        if (!StringUtils.hasText(combined)) {
            combined = posts.stream()
                    .map(CommunityPost::getContent)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.joining(" "));
        }

        String summary = summaryService.summarize(combined);

        List<String> topTags = posts.stream()
                .flatMap(post -> post.getTags().stream())
                .collect(Collectors.groupingBy(tag -> tag.getName().toLowerCase(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        if (topTags.isEmpty()) {
            topTags = summaryService.extractTopKeywords(
                    posts.stream()
                            .map(CommunityPost::getContent)
                            .toList(),
                    5
            );
        }

        List<Long> relatedIds = posts.stream()
                .map(CommunityPost::getId)
                .limit(6)
                .toList();

        return Optional.of(CaseArchiveResponse.builder()
                .caseType(caseType.name())
                .summary(summary)
                .highlightTags(topTags)
                .relatedPostIds(relatedIds)
                .build());
    }
}

