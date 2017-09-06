package com.ge.controller;

import java.io.IOException;




import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ge.api.IngestionRequestBuilder;
import com.ge.api.IngestionTag;
import com.ge.service.ProcessDataPoints;
import com.ge.util.WebSocketClient;




@RestController
@RequestMapping("/prodictive")
public class ProdictiveController {
	
	
	
	
	@Autowired
	ProcessDataPoints processDataPoints;
	JSONObject responseObj = null;
	
	@Value("${cgMachine.timeseries.zoneId}")
    String zoneId;
	
	@RequestMapping("/getData")
	public String getUser(){
		return "Prodictive Analytics PoC";
	}
	
	@Autowired
	WebSocketClient wsc;

	@Autowired
	@Qualifier("restTemplate")
	private OAuth2RestTemplate restTemplate;
	
	
	@RequestMapping(value="/data/ingestData/{tagName}", method=RequestMethod.POST)
	public String ingest(@PathVariable String tagName, @RequestBody String ingestorDocument) throws IOException {
		try {
			JSONObject json = new JSONObject(ingestorDocument);
			//String sdf = (String)json.get("measurement");
			JSONArray measurementDetails = (JSONArray) json.get("measurement");
			String ingestor = (String) json.get("ingestor");
			IngestionRequestBuilder ingestionBuilder = IngestionRequestBuilder.createIngestionRequest()
					.withMessageId(processDataPoints.generateEpochTime())
					.addIngestionTag(IngestionTag.Builder.createIngestionTag()
							.withTagName(tagName)
							.addDataPoints(processDataPoints.saveDataPoints(measurementDetails))
							.addAttribute("host", "java_application")
							.addAttribute("ingestor", ingestor)
							.addAttribute("comments", "Ongoing_Process_Details")
							.build());
			JSONObject timeseriesJSON = new JSONObject(ingestionBuilder.build().get(0));
			if(wsc.getWebsocket().isOpen()){
				responseObj = new org.json.JSONObject(wsc.getWebsocket().sendText(timeseriesJSON.toString()));
				responseObj.put("timeseriesData", timeseriesJSON);
			}
			return responseObj.toString();
		} catch (Exception e) {	
			e.printStackTrace();
			return new org.json.JSONObject().put("errorMsg", e.getMessage()).toString();
		}
	}
	
	
	
	
	
}

