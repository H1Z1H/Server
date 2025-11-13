package com.alio.alio_server.domain.recruitment.service;

import com.alio.alio_server.common.client.GeminiApiClient;
import com.alio.alio_server.common.util.WebScrapingUtil;
import com.alio.alio_server.domain.recruitment.presentation.dto.res.MarketTrendResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketTrendAnalysisService {

    private final GeminiApiClient geminiApiClient;
    private final WebScrapingUtil webScrapingUtil;
    private final Gson gson;

    public MarketTrendResponse analyzeMarketTrend() {
        log.info("채용 관련 뉴스 크롤링 시작");
        List<WebScrapingUtil.NewsArticle> articles = webScrapingUtil.crawlRecruitmentNews();
        log.info("총 {}개의 기사 크롤링 완료", articles.size());

        if (articles.isEmpty()) {
            log.error("크롤링된 기사가 없습니다!");
            return MarketTrendResponse.builder()
                    .trendSummary("크롤링된 뉴스가 없습니다.")
                    .keywords(List.of())
                    .newsSummaries(List.of())
                    .industries(List.of())
                    .build();
        }

        for (int i = 0; i < articles.size(); i++) {
            WebScrapingUtil.NewsArticle article = articles.get(i);
            log.info("크롤링된 기사 [{}]: {} - {} - {}",
                i + 1, article.keyword, article.title, article.url);
        }

        List<WebScrapingUtil.NewsArticle> overseasNews = articles.stream()
                .filter(article -> "해외취업".equals(article.keyword))
                .limit(5)
                .toList();

        log.info("해외취업 관련 뉴스 {}개 필터링 완료", overseasNews.size());

        String newsContent = buildNewsContent(articles);

        String systemPrompt = buildSystemPrompt();
        String userMessage = buildUserMessage(newsContent, articles);

        String aiResponse = geminiApiClient.sendMessage(systemPrompt, userMessage);

        return parseAiResponse(aiResponse, articles, overseasNews);
    }

    private String buildNewsContent(List<WebScrapingUtil.NewsArticle> articles) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < articles.size(); i++) {
            WebScrapingUtil.NewsArticle article = articles.get(i);
            sb.append(String.format("[기사 %d]\n", i + 1));
            sb.append(String.format("제목: %s\n", article.title));
            sb.append(String.format("출처: %s\n", article.source));
            sb.append(String.format("내용: %s\n", article.description));
            sb.append(String.format("키워드: %s\n", article.keyword));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String buildSystemPrompt() {
        return """
                당신은 채용 시장 전문 분석가입니다.
                채용 관련 뉴스와 기사를 분석하여 위험 트렌드, 주요 이슈, 키워드, 산업군을 파악하는 것이 목표입니다.

                다음 정보를 분석해주세요:
                1. 채용 관련 위험 트렌드와 주요 이슈 요약 (약 150자 정도로)
                2. 주요 키워드 (빈도수 포함, 많이 발생한 순서로)
                3. 해외취업 관련 뉴스 각각에 대한 요약 (약 5개)
                4. 주요 산업군 (이슈가 많이 되고 있는 순서로)

                응답은 반드시 아래 JSON 형식으로 작성해주세요:
                {
                  "trendSummary": "위험 트렌드와 주요 이슈에 대한 종합 요약",
                  "keywords": [
                    {"keyword": "키워드1", "frequency": 10},
                    {"keyword": "키워드2", "frequency": 8}
                  ],
                  "newsSummaries": [
                    {
                      "title": "기사 제목",
                      "summary": "기사 요약",
                      "articleIndex": 1
                    }
                  ],
                  "industries": [
                    {"industry": "산업군1", "issueCount": 15, "description": "이슈 설명"},
                    {"industry": "산업군2", "issueCount": 10, "description": "이슈 설명"}
                  ]
                }

                **중요: newsSummaries의 각 항목에는 반드시 articleIndex를 포함해야 합니다.
                articleIndex는 위에서 제공된 [기사 N] 번호를 그대로 사용하세요.**
                """;
    }

    private String buildUserMessage(String newsContent, List<WebScrapingUtil.NewsArticle> articles) {
        return String.format("""
                다음은 최근 채용 관련 뉴스 기사들입니다:

                %s

                위 기사들을 분석하여:
                1. 채용 시장의 위험 트렌드와 주요 이슈를 요약해주세요
                2. 자주 등장하는 키워드와 빈도수를 분석해주세요 (빈도수가 높은 순서로)
                3. 해외취업 관련 뉴스(키워드가 '해외취업'인 기사)를 각각 요약해주세요
                4. 주요 산업군과 해당 산업의 이슈 건수를 파악해주세요 (이슈가 많은 순서로)

                JSON 형식으로 응답해주세요.
                """, newsContent);
    }

    private MarketTrendResponse parseAiResponse(String aiResponse, List<WebScrapingUtil.NewsArticle> articles,
                                                 List<WebScrapingUtil.NewsArticle> overseasNews) {
        try {
            String jsonResponse = extractJson(aiResponse);
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            String trendSummary = jsonObject.has("trendSummary") ?
                    jsonObject.get("trendSummary").getAsString() : "";

            List<MarketTrendResponse.KeywordFrequency> keywords = new ArrayList<>();
            if (jsonObject.has("keywords")) {
                JsonArray keywordsArray = jsonObject.getAsJsonArray("keywords");
                for (int i = 0; i < keywordsArray.size(); i++) {
                    JsonObject keywordObj = keywordsArray.get(i).getAsJsonObject();
                    keywords.add(MarketTrendResponse.KeywordFrequency.builder()
                            .keyword(keywordObj.get("keyword").getAsString())
                            .frequency(keywordObj.get("frequency").getAsInt())
                            .build());
                }
            }

            List<MarketTrendResponse.NewsSummary> newsSummaries = new ArrayList<>();

            if (!overseasNews.isEmpty()) {
                log.info("해외취업 뉴스 {}개를 직접 반환합니다", overseasNews.size());

                JsonArray aiSummaries = jsonObject.has("newsSummaries") ?
                        jsonObject.getAsJsonArray("newsSummaries") : new JsonArray();

                for (int i = 0; i < overseasNews.size(); i++) {
                    WebScrapingUtil.NewsArticle article = overseasNews.get(i);

                    String summary = article.description;
                    if (i < aiSummaries.size()) {
                        JsonObject aiSummary = aiSummaries.get(i).getAsJsonObject();
                        if (aiSummary.has("summary")) {
                            summary = aiSummary.get("summary").getAsString();
                            log.info("AI 요약 사용 [{}]: {}", i + 1, article.title);
                        }
                    }

                    newsSummaries.add(MarketTrendResponse.NewsSummary.builder()
                            .title(article.title)
                            .url(article.url)
                            .summary(summary)
                            .source(article.source)
                            .build());

                    log.info("뉴스 추가 [{}]: {} -> {}", i + 1, article.title, article.url);
                }
            } else {
                log.warn("해외취업 관련 뉴스가 없습니다");
            }

            log.info("최종 뉴스 요약 개수: {}", newsSummaries.size());

            List<MarketTrendResponse.IndustryIssue> industries = new ArrayList<>();
            if (jsonObject.has("industries")) {
                JsonArray industriesArray = jsonObject.getAsJsonArray("industries");
                for (int i = 0; i < industriesArray.size(); i++) {
                    JsonObject industryObj = industriesArray.get(i).getAsJsonObject();
                    industries.add(MarketTrendResponse.IndustryIssue.builder()
                            .industry(industryObj.get("industry").getAsString())
                            .issueCount(industryObj.get("issueCount").getAsInt())
                            .description(industryObj.has("description") ?
                                    industryObj.get("description").getAsString() : "")
                            .build());
                }
            }

            return MarketTrendResponse.builder()
                    .trendSummary(trendSummary)
                    .keywords(keywords)
                    .newsSummaries(newsSummaries)
                    .industries(industries)
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
