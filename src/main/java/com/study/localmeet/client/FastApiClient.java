package com.study.localmeet.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiClient {

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    /**
     * 채팅 메시지 욕설 여부 확인
     * FastAPI가 꺼져 있으면 false(통과) 반환 — fail-open
     */
    public boolean isProfane(String content) {
        try {
            Map<String, String> body = Map.of("content", content);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = restTemplate.postForObject(
                    fastApiUrl + "/chat/filter", body, Map.class);
            return result != null && Boolean.FALSE.equals(result.get("clean"));
        } catch (Exception e) {
            log.warn("FastAPI 욕설 감지 호출 실패 (통과 처리): {}", e.getMessage());
            return false;
        }
    }

    /**
     * 날씨 기반 모임 추천
     * FastAPI가 꺼져 있으면 null 반환
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getWeatherRecommendation(double lat, double lng) {
        try {
            return restTemplate.getForObject(
                    fastApiUrl + "/weather/recommend?lat=" + lat + "&lng=" + lng,
                    Map.class);
        } catch (Exception e) {
            log.warn("FastAPI 날씨 추천 호출 실패: {}", e.getMessage());
            return null;
        }
    }
}
