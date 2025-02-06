package com.lec.packages.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WeatherApiController {

    @Value("${weather.api.key}") // ✅ 환경 변수에서 API 키 가져오기
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/weather")
    public String getWeather(@RequestParam("lat") double lat, @RequestParam("lon")  double lon) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat +
                "&lon=" + lon + "&appid=" + apiKey + "&units=metric&lang=kr";
        return restTemplate.getForObject(url, String.class); // ✅ OpenWeather API 호출
    }
}
