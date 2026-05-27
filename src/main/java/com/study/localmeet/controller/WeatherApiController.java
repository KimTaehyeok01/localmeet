package com.study.localmeet.controller;

import com.study.localmeet.client.FastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WeatherApiController {

    private final FastApiClient fastApiClient;

    @GetMapping("/api/weather/recommend")
    public ResponseEntity<?> recommend(
            @RequestParam double lat,
            @RequestParam double lng) {

        Map<String, Object> result = fastApiClient.getWeatherRecommendation(lat, lng);
        if (result == null) {
            return ResponseEntity.ok(Map.of("error", "날씨 정보를 가져올 수 없습니다."));
        }
        return ResponseEntity.ok(result);
    }
}
