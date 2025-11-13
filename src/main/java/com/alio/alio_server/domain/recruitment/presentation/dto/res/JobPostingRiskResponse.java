package com.alio.alio_server.domain.recruitment.presentation.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingRiskResponse {
    private String title;

    private String riskLevel;

    private List<String> riskKeywords;

    private String analysisResult;

    private String comprehensiveDiagnosis;

    private String actionGuidelines;

    private String summary;
}
