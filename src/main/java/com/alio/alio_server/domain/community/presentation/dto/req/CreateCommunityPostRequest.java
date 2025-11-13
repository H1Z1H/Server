package com.alio.alio_server.domain.community.presentation.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateCommunityPostRequest(
        @NotBlank String title,
        @NotBlank String content,
        @Min(1) @Max(5) Integer rating,
        @NotEmpty List<@NotBlank String> tags,
        @NotBlank String caseType
) {
}

