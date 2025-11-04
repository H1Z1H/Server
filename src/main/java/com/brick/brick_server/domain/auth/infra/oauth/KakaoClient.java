package com.brick.brick_server.domain.auth.infra.oauth;


import com.brick.brick_server.common.config.KakaoConfig;
import com.brick.brick_server.domain.auth.infra.dto.SocialPlatformUserInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoClient
{
    private static final String GET_ACCESS_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String GET_MEMBER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final KakaoConfig kakaoConfig;
    private final RestTemplate restTemplate;

    public SocialPlatformUserInfo getUserInfo(String accessToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GET_MEMBER_INFO_URL,
                HttpMethod.GET,
                request,
                String.class
        );

        JSONObject responseBody = new JSONObject(response.getBody());
        Long id = responseBody.getLong("id");
        String uid = id.toString();

        JSONObject kakaoAccount = responseBody.getJSONObject("kakao_account");
        JSONObject profile = kakaoAccount.getJSONObject("profile");

        return new SocialPlatformUserInfo(uid);
    }

    public String getAccessToken(String code)
    {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("redirect_uri", kakaoConfig.getRedirectUri());
        requestBody.add("code", code);
        requestBody.add("client_id", kakaoConfig.getClientId());
        requestBody.add("client_secret", kakaoConfig.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(GET_ACCESS_TOKEN_URL, entity, String.class);

        JSONObject responseBody = new JSONObject(response.getBody());


        return responseBody.getString("access_token");
    }

    private HttpHeaders getAccessTokenRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }
}
