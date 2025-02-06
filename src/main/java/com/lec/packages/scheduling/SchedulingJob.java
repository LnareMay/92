package com.lec.packages.scheduling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lec.packages.domain.Reservation;
import com.lec.packages.repository.FacilityRepository;
import com.lec.packages.repository.ReservationRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class SchedulingJob {

    @Value("${00Data.admin.memId}")
	private String memId;

    @Value("${00Data.admin.keyString}")
    private String apiKey;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Scheduled(fixedDelay = 10000)
    public void removeReservationRecord() {
        LocalDate date = LocalDate.now();

        List<Reservation> reservations = reservationRepository.findAllByDeleteFlag(false);
        
    }

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

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result);

            JSONArray publicTrainingFacilitySoccer = (JSONArray) object.get("PublicTrainingFacilitySoccer");

            JSONObject datas = (JSONObject) publicTrainingFacilitySoccer.get(1);
            
            JSONArray rows = (JSONArray) datas.get("row");

            for (Object row : rows) {
                JSONObject facilityInfo = (JSONObject) row;
                
            }
            
        } catch (Exception e) {
            log.error(e.getMessage());
        }
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
