package com.brick.brick_server.domain.recruitment.service;

import com.brick.brick_server.common.client.GeminiApiClient;
import com.brick.brick_server.common.util.WebScrapingUtil;
import com.brick.brick_server.domain.recruitment.presentation.dto.req.JobPostingAnalysisRequest;
import com.brick.brick_server.domain.recruitment.presentation.dto.res.JobPostingRiskResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingRiskAnalysisService {

    private final GeminiApiClient geminiApiClient;
    private final WebScrapingUtil webScrapingUtil;
    private final Gson gson;

    public JobPostingRiskResponse analyzeJobPosting(JobPostingAnalysisRequest request) {
        String jobPostingContent;

        if ("url".equalsIgnoreCase(request.type())) {
            if (request.url() == null || request.url().isEmpty()) {
                throw new IllegalArgumentException("URL이 제공되지 않았습니다.");
            }
            jobPostingContent = webScrapingUtil.extractTextFromUrl(request.url());
        } else if ("text".equalsIgnoreCase(request.type())) {
            if (request.text() == null || request.text().isEmpty()) {
                throw new IllegalArgumentException("텍스트가 제공되지 않았습니다.");
            }
            jobPostingContent = request.text();
        } else {
            throw new IllegalArgumentException("유효하지 않은 타입입니다. 'url' 또는 'text'를 사용하세요.");
        }

        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(jobPostingContent);

        String aiResponse = geminiApiClient.sendMessage(systemPrompt, userMessage);

        log.info(aiResponse);

        return parseAiResponse(aiResponse);
    }

    private String buildSystemPrompt() {
        return """
                당신은 채용 공고의 위험도를 분석하는 전문가입니다.
                채용 사기, 불법 고용, 착취 등의 위험 요소를 파악하여 구직자를 보호하는 것이 목표입니다.

                다음 기준으로 채용 공고를 분석해주세요:
                1. 위험도 레벨: 매우 안전 / 안전 / 주의 / 위험 / 매우 위험
                2. 주요 위험 키워드: 선입금, 보증금, 개인정보 과다 요구, 불명확한 업무 내용, 과도한 급여 제시 등
                3. 상세한 분석 결과
                4. AI 종합 진단
                5. 구직자를 위한 행동 요령
                6. 결과 요약

                응답은 반드시 아래 JSON 형식으로 작성해주세요:
                {
                  "title": "공고 제목",
                  "riskLevel": "위험도",
                  "riskKeywords": ["키워드1", "키워드2"],
                  "analysisResult": "분석 결과 상세 내용",
                  "comprehensiveDiagnosis": "AI 종합 진단",
                  "actionGuidelines": "행동 요령",
                  "summary": "결과 요약"
                }
                """;
    }

    private String buildUserMessage(String jobPostingContent) {
        return String.format("""
                다음 채용 공고를 분석해주세요:

                %s

                위 공고의 위험도를 분석하고, JSON 형식으로 응답해주세요.
                """, jobPostingContent);
    }

    private JobPostingRiskResponse parseAiResponse(String aiResponse) {
        try {
            String jsonResponse = extractJson(aiResponse);

            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            String title = jsonObject.has("title") ? jsonObject.get("title").getAsString() : "제목 없음";
            String riskLevel = jsonObject.has("riskLevel") ? jsonObject.get("riskLevel").getAsString() : "알 수 없음";
            String analysisResult = jsonObject.has("analysisResult") ? jsonObject.get("analysisResult").getAsString() : "";
            String comprehensiveDiagnosis = jsonObject.has("comprehensiveDiagnosis") ? jsonObject.get("comprehensiveDiagnosis").getAsString() : "";
            String actionGuidelines = jsonObject.has("actionGuidelines") ? jsonObject.get("actionGuidelines").getAsString() : "";
            String summary = jsonObject.has("summary") ? jsonObject.get("summary").getAsString() : "";

            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> riskKeywords = jsonObject.has("riskKeywords") ?
                    gson.fromJson(jsonObject.get("riskKeywords"), listType) :
                    List.of();

            return JobPostingRiskResponse.builder()
                    .title(title)
                    .riskLevel(riskLevel)
                    .riskKeywords(riskKeywords)
                    .analysisResult(analysisResult)
                    .comprehensiveDiagnosis(comprehensiveDiagnosis)
                    .actionGuidelines(actionGuidelines)
                    .summary(summary)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", aiResponse, e);
            throw new RuntimeException("AI 응답 파싱 실패", e);
        }
    }

    private String extractJson(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }
}
