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
public class CommunityPostCardResponse {
    private final Long id;
    private final String title;
    private final String summary;
    private final String author;
    private final String caseType;
    private final LocalDateTime createdAt;
    private final List<String> tags;

    public static CommunityPostCardResponse from(CommunityPost post) {
        return CommunityPostCardResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .summary(post.getSummary())
                .author(resolveAuthor(post))
                .caseType(post.getCaseType().name())
                .createdAt(post.getCreatedAt())
                .tags(post.getTags().stream()
                        .map(tag -> tag.getName().toLowerCase())
                        .collect(Collectors.toList()))
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

