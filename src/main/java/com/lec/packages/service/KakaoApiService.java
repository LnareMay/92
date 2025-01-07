package com.lec.packages.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lec.packages.dto.MemberSecurityDTO;

@Service
public class KakaoApiService {

	 @Value("${kakao.rest.api.key}")
	    private String kakaoApiKey;

    /**
     * 현재 로그인된 사용자의 memAddress를 가져와서 지역 정보 추출
     */
    public String extractRegionFromAuthenticatedMember() {
        // 현재 로그인된 사용자 가져오기
        MemberSecurityDTO memberSecurityDTO = getAuthenticatedMember();
        if (memberSecurityDTO == null) {
            return "로그인이 필요합니다."; // 인증되지 않은 사용자 처리
        }

        // memAddress 추출
        String memAddress = memberSecurityDTO.getMemAddress();
        if (memAddress == null || memAddress.isEmpty()) {
            return "주소 정보가 없습니다."; // 주소가 없는 사용자 처리
        }

        // 지역 정보 추출
        String region = extractRegionFromAddress(memAddress);
        return region != null ? region : "지역 정보가 없습니다."; // 지역 정보 반환
    }

    /**
     * 주소 문자열에서 지역 정보 추출
     */
    public String extractRegionFromAddress(String memAddress) {
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        String result = null;

        try {
            // URL 인코딩
            String query = URLEncoder.encode(memAddress, "UTF-8");
            String requestUrl = apiUrl + "?query=" + query;

            // HTTP 요청
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "KakaoAK " + kakaoApiKey);

            // 응답 처리
            int responseCode = connection.getResponseCode();
            System.out.println("Request URL: " + requestUrl);
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) { // HTTP OK
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                System.out.println("Response: " + response.toString());

                // JSON 파싱
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray documents = jsonObject.getJSONArray("documents");

                if (documents.length() > 0) {
                    JSONObject address = documents.getJSONObject(0).getJSONObject("address");
                    result = address.optString("region_3depth_name", "구/동/면 정보 없음");
                }
            } else {
                System.err.println("Error: HTTP response code " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result != null ? result : "지역 정보가 없습니다.";
    }


    /**
     * 현재 인증된 사용자 정보를 가져오는 메서드
     */
    private MemberSecurityDTO getAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
            return (MemberSecurityDTO) authentication.getPrincipal();
        }
        return null; // 인증되지 않은 사용자일 경우 null 반환
    }
}
