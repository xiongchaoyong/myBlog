package com.x.utils;

import com.x.exception.BaseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

public class GithubLogin {
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";

    private static final String REDIRECT_URL = "http://localhost:5173/callback";

    public static Map<String,Object> githubLogin(String code,String clientId,String clientSecret){

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("client_id",clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", REDIRECT_URL);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response;
        try {
             response = restTemplate.postForEntity(TOKEN_URL, entity, Map.class);
        }catch (Exception e){
            throw new BaseException("获取accessToken失败");
        }

        String accessToken = (String) response.getBody().get("access_token");
        if (accessToken == null) {
            throw new BaseException("GitHub返回的access_token为空");
        }
        // 2. 用 access_token 获取用户信息
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<String> userEntity = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResp = restTemplate.exchange(
                "https://api.github.com/user",
                GET,
                userEntity,
                Map.class
        );
        return userResp.getBody();
    }
}
