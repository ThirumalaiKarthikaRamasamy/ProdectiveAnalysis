package com.ge.machine.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class IngestData {
	@Value("${cgMachine.timeseries.ingestUrl}")
	String ingestUrl;
	@Value("${cgMachine.timeseries.zoneId}")
    String zoneId;
	@Value("${cgMachine.timeseries.ingestorUrl}")
    String ingestorUrl;
	@Autowired
	FetchAccessTokens fetchAccessToken;
	public JSONObject ingestTimeseriesData(JSONObject timeseriesData) {
		JSONObject responseObject = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			//Set Header
			headers.set("Authorization", fetchAccessToken.getBasicToken());
			headers.set("Pragma", "no-cache");
			headers.set("content-type", "application/json");
			headers.set("Cache-Control", "no-cache");
			//Make HTTP Request
			if(timeseriesData.has("name")) {
				String url = ingestorUrl.replace("$tagName", timeseriesData.getString("name"));
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(timeseriesData.toString(), headers), String.class);
				if(response.getStatusCode()==HttpStatus.OK) {
					responseObject = new JSONObject(response);
				}
			}else {
				throw new Exception("Timeseries data is not valid");
			}
		}catch(Exception e) {
			responseObject = new JSONObject();
			responseObject.put("error", e.getMessage());
		}
		return responseObject;
		
	}
}
