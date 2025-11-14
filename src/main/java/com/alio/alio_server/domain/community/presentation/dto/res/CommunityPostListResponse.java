package com.alio.alio_server.domain.community.presentation.dto.res;

import com.alio.alio_server.domain.community.domain.CommunityPost;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CommunityPostListResponse {
    private final Long id;
    private final String title;
    private final String author;
    private final Integer rating;
    private final String caseType;
    private final String summary;
    private final Boolean isAnonymous;
    private final Long viewCount;
    private final LocalDateTime createdAt;
    private final List<String> tags;
    private final String country;

    public static CommunityPostListResponse from(CommunityPost post) {
        return CommunityPostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(resolveAuthor(post))
                .rating(post.getRating())
                .caseType(post.getCaseType().name())
                .summary(post.getSummary())
                .isAnonymous(post.getIsAnonymous())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .tags(post.getTags().stream()
                        .map(tag -> tag.getName().toLowerCase())
                        .collect(Collectors.toList()))
                .country(post.getCountry())
                .build();
    }

    private static String resolveAuthor(CommunityPost post) {
        if (post.getIsAnonymous()) {
            return "익명";
        }
        String nickname = post.getAuthor().getNickname();
        if (StringUtils.hasText(nickname)) {
            return nickname;
        }
        return post.getAuthor().getUsername();
    }
}

