package com.alio.alio_server.domain.community.service;

import com.alio.alio_server.common.client.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunitySummaryService {

    private static final List<String> RISK_KEYWORDS = List.of("문제", "위험", "경고", "사기", "피해", "미지급", "비자", "위반");
    private static final List<String> SUCCESS_KEYWORDS = List.of("성공", "추천", "팁", "도움", "해결", "합격");
    private static final int MAX_SENTENCE = 3;

    private final GeminiApiClient geminiApiClient;

    public String summarize(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        List<String> sentences = splitSentences(content);
        if (sentences.isEmpty()) {
            return "";
        }

        List<String> important = prioritize(sentences, RISK_KEYWORDS);
        if (important.isEmpty()) {
            important = prioritize(sentences, SUCCESS_KEYWORDS);
        }
        if (important.isEmpty()) {
            important = sentences.stream().limit(MAX_SENTENCE).collect(Collectors.toList());
        }

        return String.join(" ", important);
    }

    public String summarizeWithGemini(String content, String caseType) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        try {
            String systemPrompt = caseType.equals("RISK") 
                ? "당신은 해외 취업 관련 위험 사례를 분석하는 전문가입니다. 주어진 텍스트에서 가장 중요한 위험 요소와 경고 사항을 한 문장으로 간결하게 요약해주세요."
                : "당신은 해외 취업 관련 성공 사례를 분석하는 전문가입니다. 주어진 텍스트에서 가장 중요한 성공 포인트와 추천 사항을 한 문장으로 간결하게 요약해주세요.";

            String userMessage = "다음 텍스트를 요약해주세요:\n\n" + content;

            String summary = geminiApiClient.sendMessage(systemPrompt, userMessage);
            if (StringUtils.hasText(summary)) {
                return summary.trim();
            }
        } catch (Exception e) {
            log.error("Failed to generate summary with Gemini", e);
        }

        // Fallback to simple summarization
        return summarize(content);
    }

    public List<String> extractTopKeywords(Collection<String> contents, int limit) {
        Map<String, Long> counter = new HashMap<>();
        contents.stream()
                .filter(StringUtils::hasText)
                .map(this::normalize)
                .map(text -> text.split("\\s+"))
                .forEach(words -> {
                    for (String word : words) {
                        if (word.length() < 2) {
                            continue;
                        }
                        counter.merge(word, 1L, Long::sum);
                    }
                });

        return counter.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String normalize(String text) {
        return text.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", " ");
    }

    private List<String> splitSentences(String content) {
        return Arrays.stream(content.replace("\r", " ").split("(?<=[.!?\\n])\\s+"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private List<String> prioritize(List<String> sentences, List<String> keywords) {
        return sentences.stream()
                .filter(sentence -> keywords.stream().anyMatch(sentence::contains))
                .limit(MAX_SENTENCE)
                .collect(Collectors.toList());
    }
}

