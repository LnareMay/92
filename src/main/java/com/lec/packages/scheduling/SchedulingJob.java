package com.lec.packages.scheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        String jsonPath = "./KS_AREA_ACCTO_SPORTS_CLUB_CRSTAT_INFO_202407.json";
    }

    @Scheduled(fixedDelay = 10000)
    public void getSetFacilityFromAPI() {
        
        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        String result = null;

        String urlString = "https://openapi.gg.go.kr/PublicTrainingFacilitySoccer?" 
                            + "KEY=" + apiKey
                            + "&Type=json"
                            + "&pSize=1000";
        
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

        log.info(result);
    }

    private InputStream getNetworkConnection(HttpURLConnection urlConnection) throws IOException{
        
        urlConnection.setRequestMethod("POST");

        if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("OPEN API ERROR" + urlConnection.getResponseCode());
        }

        return urlConnection.getInputStream();
    }

    private String readStreamToString(InputStream stream) throws IOException{
        StringBuilder sbResult = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        String brString;
        while ((brString = br.readLine()) != null) {
            sbResult.append(brString);
        }

        br.close();

        return sbResult.toString();
    }
}
