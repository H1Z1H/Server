package com.alio.alio_server.domain.recruitment.presentation;

import com.alio.alio_server.domain.recruitment.presentation.dto.req.JobPostingAnalysisRequest;
import com.alio.alio_server.domain.recruitment.presentation.dto.res.JobPostingRiskResponse;
import com.alio.alio_server.domain.recruitment.presentation.dto.res.MarketTrendResponse;
import com.alio.alio_server.domain.recruitment.service.JobPostingRiskAnalysisService;
import com.alio.alio_server.domain.recruitment.service.MarketTrendAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "채용 공고 분석")
@Slf4j
@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

    private final JobPostingRiskAnalysisService jobPostingRiskAnalysisService;
    private final MarketTrendAnalysisService marketTrendAnalysisService;

    @Operation(summary = "채용 공고 위험도 분석", description = "URL 또는 텍스트로 제공된 채용 공고의 위험도를 분석합니다.")
    @PostMapping("/analyze")
    public ResponseEntity<JobPostingRiskResponse> analyzeJobPosting(
            @Valid @RequestBody JobPostingAnalysisRequest request
    ) {
        log.info("채용 공고 위험도 분석 요청: type={}", request.type());
        JobPostingRiskResponse response = jobPostingRiskAnalysisService.analyzeJobPosting(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "채용 시장 트렌드 분석", description = "채용 시장 전반의 위험 트렌드와 주요 이슈를 분석합니다.")
    @GetMapping("/market-trend")
    public ResponseEntity<MarketTrendResponse> analyzeMarketTrend() {
        log.info("채용 시장 트렌드 분석 요청");
        MarketTrendResponse response = marketTrendAnalysisService.analyzeMarketTrend();
        return ResponseEntity.ok(response);
    }
}
