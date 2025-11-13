package com.alio.alio_server.domain.recruitment.presentation.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketTrendResponse {
    private String trendSummary;

    private List<KeywordFrequency> keywords;

    private List<NewsSummary> newsSummaries;

    private List<IndustryIssue> industries;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordFrequency {
        private String keyword;
        private Integer frequency;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsSummary {
        private String title;
        private String url;
        private String summary;
        private String source;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndustryIssue {
        private String industry;
        private Integer issueCount;
        private String description;
    }
}
