package com.alio.alio_server.domain.community.service;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommunitySummaryService {

    private static final List<String> RISK_KEYWORDS = List.of("문제", "위험", "경고", "사기", "피해", "미지급", "비자", "위반");
    private static final List<String> SUCCESS_KEYWORDS = List.of("성공", "추천", "팁", "도움", "해결", "합격");
    private static final int MAX_SENTENCE = 3;

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

