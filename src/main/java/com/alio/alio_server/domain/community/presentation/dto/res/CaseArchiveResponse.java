package com.alio.alio_server.domain.community.presentation.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CaseArchiveResponse {
    private final String caseType;
    private final String summary;
    private final List<String> highlightTags;
    private final List<Long> relatedPostIds;
}

