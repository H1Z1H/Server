package com.brick.brick_server.domain.admin.service.coin;

import com.brick.brick_server.domain.admin.presentation.dto.req.ChatRequest;
import com.brick.brick_server.domain.admin.presentation.dto.res.ArticleWithFluctuation;
import com.brick.brick_server.domain.admin.presentation.dto.res.ChatResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ArticleGenerator {

    private static final Logger log = LoggerFactory.getLogger(ArticleGenerator.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL = "http://kangsiwoo.com:8080/api/chat/completions";
    private static final String API_KEY = "sk-d6da6c1716364949ba985d8b99a9283d";

    public ArticleGenerator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        this.restTemplate.setRequestFactory(
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
        );

        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            log.info(">>> 요청 URI: {}", request.getURI());
            log.info(">>> 요청 메서드: {}", request.getMethod());
            log.info(">>> 요청 헤더: {}", request.getHeaders());
            log.info(">>> 요청 바디: {}", new String(body, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        });
    }

    public List<ArticleWithFluctuation> generateArticles() {
        ChatRequest request = new ChatRequest(
                "exaone3.5:32b-instruct-q4_K_M",
                List.of(new ChatRequest.Message(
                        "user",
                        "부정적이거나 긍정적인 뉴스를 작성해줘\n" +
                                "작성한 다음 어떤 주식이 얼마나 변동이 있을지 예측해줘\n" +
                                "뉴스는 주식의 이름을 직접적으로 언급하면 안되고 간접적으로 미치는 영향에는 주식 소개글에 관련 기술이 있을 시에만 변동을 줘야해\n" +
                                "출력은 json 코드블록에 묶어서 출력해주고 다른 내용은 포함하지 말아줘\n" +
                                "뉴스 내용은 350자를 무조건 넘겨야해\n" +
                                "fluctuation은 서비스의 영어 약자를 무조건 사용해줘"
                ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponse> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                ChatResponse.class
        );

        String content = response.getBody().choices().get(0).message().content();
        log.info("[AI 응답 content]\n{}", content);

        return parseArticlesFromJsonBlocks(content);
    }

    private List<ArticleWithFluctuation> parseArticlesFromJsonBlocks(String rawContent) {
        List<ArticleWithFluctuation> results = new ArrayList<>();

        // ```json ... ``` 코드블럭 전체를 캡처 (멀티라인 포함)
        Pattern pattern = Pattern.compile("```json\\s*(\\{.*?\\})\\s*```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(rawContent);

        int index = 0;
        while (matcher.find()) {
            String json = matcher.group(1).trim();
            log.info("[{}번째 JSON 블럭 원본]\n{}", index + 1, json);

            try {
                ArticleWithFluctuation article = objectMapper.readValue(json, ArticleWithFluctuation.class);
                log.info("[{}번째 JSON 블럭 파싱 결과] {}", index + 1, article);
                results.add(article);
            } catch (JsonProcessingException e) {
                log.error("[{}번째 JSON 블럭 파싱 실패] json={}", index + 1, json, e);
                throw new RuntimeException("AI 응답 파싱 실패", e);
            }
            index++;
        }

        log.info("총 {}개의 기사 파싱 완료", results.size());
        return results;
    }
}