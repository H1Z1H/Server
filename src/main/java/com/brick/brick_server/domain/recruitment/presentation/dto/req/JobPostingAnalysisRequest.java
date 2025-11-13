package com.brick.brick_server.domain.recruitment.presentation.dto.req;

import jakarta.validation.constraints.NotNull;

public record JobPostingAnalysisRequest(
        String url,
        String text,
        @NotNull String type  // "url" or "text"
) {
}
