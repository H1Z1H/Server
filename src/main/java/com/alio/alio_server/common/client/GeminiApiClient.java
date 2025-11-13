package com.alio.alio_server.common.client;

import com.alio.alio_server.common.config.GeminiConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiApiClient {

    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1/models";

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;
    private final Gson gson;

    /*
    public void listModels() {
        try {
            String url = String.format("%s?key=%s", GEMINI_API_BASE_URL, geminiConfig.getApiKey());
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            log.info("ListModels response: {}", response.getBody());
            JsonObject json = gson.fromJson(response.getBody(), JsonObject.class);
            if (json.has("models")) {
                JsonArray models = json.getAsJsonArray("models");
                for (int i = 0; i < models.size(); i++) {
                    JsonObject m = models.get(i).getAsJsonObject();
                    String name = m.has("name") ? m.get("name").getAsString() : "unknown";
                    log.info("Available model: {}", name);
                }
            }
        } catch (Exception e) {
            log.error("Failed to list models", e);
        }
    }
    */

    public String sendMessage(String systemPrompt, String userMessage) {
        try {
            String url = String.format("%s/%s:generateContent?key=%s",
                    GEMINI_API_BASE_URL,
                    geminiConfig.getModel(),
                    geminiConfig.getApiKey());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            JsonObject requestBody = new JsonObject();

            // System prompt를 user message 앞에 추가하는 방식으로 변경
            String combinedMessage = userMessage;
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                combinedMessage = systemPrompt + "\n\n" + userMessage;
            }

            // Contents 추가
            JsonArray contents = new JsonArray();
            JsonObject content = new JsonObject();
            content.addProperty("role", "user");

            JsonArray parts = new JsonArray();
            JsonObject part = new JsonObject();
            part.addProperty("text", combinedMessage);
            parts.add(part);

            content.add("parts", parts);
            contents.add(content);
            requestBody.add("contents", contents);

            // Generation config 추가
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("maxOutputTokens", geminiConfig.getMaxTokens());
            generationConfig.addProperty("temperature", 0.7);
            requestBody.add("generationConfig", generationConfig);

            HttpEntity<String> entity = new HttpEntity<>(gson.toJson(requestBody), headers);

            log.info("Calling Gemini API with model: {}", geminiConfig.getModel());
            log.debug("Request body: {}", gson.toJson(requestBody));

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Gemini API response received");
            log.debug("Response: {}", response.getBody());

            JsonObject responseJson = gson.fromJson(response.getBody(), JsonObject.class);

            // candidates[0].content.parts[0].text 추출
            if (responseJson.has("candidates")) {
                JsonArray candidates = responseJson.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    if (candidate.has("content")) {
                        JsonObject contentObj = candidate.getAsJsonObject("content");
                        if (contentObj.has("parts")) {
                            JsonArray partsArray = contentObj.getAsJsonArray("parts");
                            if (partsArray.size() > 0) {
                                JsonObject firstPart = partsArray.get(0).getAsJsonObject();
                                if (firstPart.has("text")) {
                                    return firstPart.get("text").getAsString();
                                }
                            }
                        }
                    }
                }
            }

            log.error("Failed to extract text from Gemini response");
            return null;
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }
}
