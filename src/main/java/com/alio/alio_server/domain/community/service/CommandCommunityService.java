package com.alio.alio_server.domain.community.service;

import com.alio.alio_server.domain.community.domain.CommunityCaseType;
import com.alio.alio_server.domain.community.domain.CommunityPost;
import com.alio.alio_server.domain.community.domain.CommunityTag;
import com.alio.alio_server.domain.community.domain.repository.CommunityPostRepository;
import com.alio.alio_server.domain.community.domain.repository.CommunityTagRepository;
import com.alio.alio_server.domain.community.presentation.dto.req.CreateCommunityPostRequest;
import com.alio.alio_server.domain.community.presentation.dto.res.CommunityPostResponse;
import com.alio.alio_server.domain.user.domain.Users;
import com.alio.alio_server.domain.user.domain.repository.UserRepository;
import com.alio.alio_server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommandCommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityTagRepository tagRepository;
    private final UserRepository userRepository;
    private final CommunitySummaryService summaryService;

    @Transactional
    public CommunityPostResponse createPost(Long userId, CreateCommunityPostRequest request) {
        if (userId == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        Users author = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        CommunityCaseType caseType = CommunityCaseType.from(request.caseType());
        Set<CommunityTag> tags = resolveTags(request);

        CommunityPost post = CommunityPost.builder()
                .author(author)
                .title(request.title())
                .content(request.content())
                .rating(request.rating())
                .caseType(caseType)
                .isAnonymous(request.isAnonymous() != null ? request.isAnonymous() : false)
                .country(request.country())
                .tags(tags)
                .build();

        String summary = summaryService.summarizeWithGemini(request.content(), caseType.name());
        post.updateSummary(summary);

        CommunityPost saved = postRepository.save(post);
        return CommunityPostResponse.from(saved);
    }

    private Set<CommunityTag> resolveTags(CreateCommunityPostRequest request) {
        Set<CommunityTag> tags = new HashSet<>();
        request.tags().stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(String::toLowerCase)
                .forEach(tagName -> {
                    CommunityTag tag = tagRepository.findByNameIgnoreCase(tagName)
                            .orElseGet(() -> tagRepository.save(CommunityTag.of(tagName)));
                    tags.add(tag);
                });
        return tags;
    }
}
