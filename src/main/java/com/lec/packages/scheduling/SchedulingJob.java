package com.lec.packages.scheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SchedulingJob {

    @Value("${00Data.admin.memId}")
	private String memId;

    @Value("${00Data.admin.keyString}")
    private String apiKey;

    @Scheduled(fixedDelay = 1000)
    public void getSetClubFromJsonFile() {
        //log.info(Thread.currentThread().getName() + " getSetClubFromJsonFile " + LocalDateTime.now());
        String jsonPath = "./KS_AREA_ACCTO_SPORTS_CLUB_CRSTAT_INFO_202407.json";
    }

    @Scheduled(fixedDelay = 10000)
    public void getSetFacilityFromAPI() {
        //log.info("getSetFacilityFromAPI");
        
        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        String result = null;

        String urlString = "https://openapi.gg.go.kr/PublicLivelihood?" 
                            + "serviceKey=" + apiKey
                            + "&Type=json"
                            + "&pSize=150";
        
        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            stream = getNetworkConnection(urlConnection);
            
            result = readStreamToString(stream);
            if(stream != null) stream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if(urlConnection != null) urlConnection.disconnect();
        }

        //slog.info(result);
    }

    private InputStream getNetworkConnection(HttpURLConnection urlConnection) throws IOException{
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(3000);
        urlConnection.setRequestMethod("GET");

        if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("OPEN API ERROR" + urlConnection.getResponseCode());
        }

        return urlConnection.getInputStream();
    }

    private String readStreamToString(InputStream stream) throws IOException{
        StringBuilder sbResult = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        while (br.readLine() != null) {
            sbResult.append(br.readLine() + "\n\r");
        }

        br.close();

        return sbResult.toString();
    }
}
