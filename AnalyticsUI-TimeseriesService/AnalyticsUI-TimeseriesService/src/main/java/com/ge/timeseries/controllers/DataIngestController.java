package com.ge.timeseries.controllers;

import java.io.IOException;

import org.apache.log4j.Logger;
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

import com.ge.timeseries.services.ProcessDataPoints;
import com.ge.timeseries.timeseries.api.IngestionRequestBuilder;
import com.ge.timeseries.timeseries.api.IngestionTag;
import com.ge.timeseries.utils.Validations;
import com.ge.timeseries.utils.WebSocketClient;
@RestController
public class DataIngestController {
	private final Logger log = Logger.getLogger(this.getClass());

	@Value("${brilliantLab.timeseries.ingestUrl}")
	String ingestUrl;

	@Value("${brilliantLab.timeseries.zoneId}")
	String zoneId;
	
	@Autowired
	WebSocketClient wsc;

	@Autowired
	@Qualifier("restTemplate")
	private OAuth2RestTemplate restTemplate;

	@Autowired
	ProcessDataPoints processDataPoints;
	JSONObject responseObj = null;

	@Autowired
	Validations validationUtil;

	@Autowired
	ProcessDataPoints processTsData;
	
	@RequestMapping(value="/data/ingestData/{tagName}", method=RequestMethod.POST)
	public String ingest(@PathVariable String tagName, @RequestBody String ingestorDocument) throws IOException {
		try {
			JSONObject json = new JSONObject(ingestorDocument);
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
			JSONObject responseObj = null;
			if(wsc.getWebsocket().isOpen()){
				log.info("Ingesting Timeseries Data.");
				responseObj = new org.json.JSONObject(wsc.getWebsocket().sendText(timeseriesJSON.toString()));
				responseObj.put("timeseriesData", timeseriesJSON);
			}
			return responseObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return new org.json.JSONObject().put("errorMsg", e.getMessage()).toString();
		}
	}
}
