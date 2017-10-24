package com.ge.timeseries.controllers;

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

import com.ge.timeseries.utils.FetchDataConnection;
@RestController
public class DataQueryController {
	@Value("${brilliantLab.timeseries.ingestUrl}")
	String ingestUrl;
	@Value("${brilliantLab.timeseries.zoneId}")
    String zoneId;
	@Autowired
    @Qualifier("restTemplate")
    private OAuth2RestTemplate restTemplate;
	@Autowired
	FetchDataConnection fetchData;
	@RequestMapping("/data/displayTags")
	public ResponseEntity<String> displayTags()  {
		try {
			System.out.println(restTemplate.getAccessToken().toString());
			return new ResponseEntity<String>(fetchData.fetchDataTags(restTemplate.getAccessToken().toString(), zoneId).toString(), HttpStatus.OK);
		} catch (IOException e) {
			JSONObject error = new JSONObject();
			error.put("errorMsg", e.getMessage());
			return new ResponseEntity<String>(e.toString(), HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(value="/data/{urlEndpoint}",method=RequestMethod.POST)
    public ResponseEntity<String> queryLatestValues(@RequestBody(required=true) String requestBody, @PathVariable(value="urlEndpoint") String urlEndPoint) {
        JSONObject responseObj;
		try {
			if(urlEndPoint.trim().equals("queryDataPoints"))
				responseObj = fetchData.createQuery(restTemplate.getAccessToken().toString(), zoneId, requestBody, "");
			else if(urlEndPoint.trim().equals("latestDataPoints"))
					responseObj = fetchData.createQuery(restTemplate.getAccessToken().toString(), zoneId, requestBody, "/latest");
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
