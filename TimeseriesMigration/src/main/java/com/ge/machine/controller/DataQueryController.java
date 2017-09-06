package com.ge.machine.controller;

import java.io.IOException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ge.machine.service.TimseriesDataService;
import com.ge.machine.util.FetchAccessTokens;
import com.ge.machine.util.TimeseriesFetchDataConnection;
@RestController
public class DataQueryController {
	@Autowired
	TimseriesDataService timeseriesService;
	@Value("${cgMachine.timeseries.ingestUrl}")
	String ingestUrl;
	@Value("${cgMachine.timeseries.zoneId}")
    String zoneId;
	@Autowired
    @Qualifier("restTemplate")
    private OAuth2RestTemplate restTemplate;
	@Autowired
	FetchAccessTokens fetchAccessToken;
	@Autowired
	TimeseriesFetchDataConnection fetchData;
	@RequestMapping(value="/data/displayTags", produces={"application/json"})
	public ResponseEntity<String> displayTags()  {
		try {
			return new ResponseEntity<String>(fetchData.fetchDataTags(fetchAccessToken.getBasicEnvToken(), zoneId).toString(), HttpStatus.OK);
		} catch (IOException e) {
			JSONObject error = new JSONObject();
			error.put("errorMsg", e.getMessage());
			return new ResponseEntity<String>(e.toString(), HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value="/data/getTimeseriesData", produces={"application/json"})
	public ResponseEntity<String> getTimeseriesData()  {
		JSONObject timeseriesTags;
		int statusCode = 200;
		try {
			timeseriesTags = new JSONObject(fetchData.fetchDataTags(fetchAccessToken.getBasicEnvToken(), zoneId).toString());
			if(!timeseriesTags.has("error")){
				return new ResponseEntity<String>(timeseriesService.getTimeseriesData(timeseriesTags.getJSONArray("results"), fetchAccessToken.getBasicEnvToken(), zoneId).toString(), HttpStatus.valueOf(statusCode));
			}else{
				return new ResponseEntity<String>(timeseriesTags.toString(), HttpStatus.valueOf(500));
			}
		} catch (Exception e) {
			timeseriesTags = new JSONObject();
			timeseriesTags.put("status", 500);
			timeseriesTags.put("error", e.getMessage());
			e.printStackTrace();
			statusCode = 500;
			return new ResponseEntity<String>(timeseriesTags.toString(), HttpStatus.valueOf(statusCode));
		}
	}
	
	@RequestMapping(value="/data/{urlEndpoint}",method=RequestMethod.POST, produces={"application/json"})
    public ResponseEntity<String> queryLatestValues(@RequestBody(required=true) String requestBody, @PathVariable(value="urlEndpoint") String urlEndPoint) {
        JSONObject responseObj;
		try {
			if(urlEndPoint.trim().equals("queryDataPoints"))
				responseObj = fetchData.createQuery(fetchAccessToken.getBasicEnvToken(), zoneId, requestBody, "");
			else if(urlEndPoint.trim().equals("latestDataPoints"))
					responseObj = fetchData.createQuery(fetchAccessToken.getBasicEnvToken(), zoneId, requestBody, "/latest");
			else
				responseObj = new JSONObject().put("statusCode", "3001");
			if(responseObj.get("statusCode").equals("200")||responseObj.get("statusCode").equals("3000")){
	        	return new ResponseEntity<String>(responseObj.toString(), HttpStatus.OK);
	        }
	        else{
	        	return new ResponseEntity<String>(responseObj.toString(), HttpStatus.BAD_REQUEST);
	        }
		} catch (IOException e) {
			JSONObject error = new JSONObject();
			error.put("errorMsg", e.getMessage());
			return new ResponseEntity<String>(e.toString(), HttpStatus.EXPECTATION_FAILED);
		}
        
    }

}
