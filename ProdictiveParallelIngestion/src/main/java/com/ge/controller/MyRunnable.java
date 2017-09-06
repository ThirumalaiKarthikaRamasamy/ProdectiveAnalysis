package com.ge.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.ge.api.IngestionRequestBuilder;
import com.ge.api.IngestionTag;
import com.ge.service.ProcessDataPoints;
import com.ge.util.WebSocketClient;

@Component
public class MyRunnable implements Runnable {
	WebServicesBarrier partsWsBarrier=null;
	JSONArray ingestorContent;
	JSONObject responseObj;
	JSONObject json;
	int i;
	
	@Autowired
	ProcessDataPoints processDataPoints;
	@Autowired
	WebSocketClient wsc;
	

	@Value("${cgMachine.timeseries.zoneId}")
    String zoneId;
	@Autowired
	@Qualifier("restTemplate")
	private OAuth2RestTemplate restTemplate;
	
public MyRunnable(){
	
}
public MyRunnable(WebServicesBarrier partsWsBarrier, JSONObject json,ProcessDataPoints processDataPoints,WebSocketClient wsc, OAuth2RestTemplate restTemplate2,int i){
	this.partsWsBarrier=partsWsBarrier;
	this.json=json;
	this.processDataPoints=processDataPoints;
	this.wsc=wsc;
	this.restTemplate=restTemplate2;
	this.i=i;
}

public void run() {
	   
	        //do something
	       
	ArrayList<Object> data = new ArrayList<Object>();
    JSONArray ingestorContent = (JSONArray) json.get("data");
 
	System.out.println(ingestorContent);
	JSONObject firstElement = null; 
	
	if(ingestorContent.length()==2){
	 firstElement = ingestorContent.getJSONObject(i);
	} else{
		firstElement = ingestorContent.getJSONObject(0);
	}
	String ingestor = (String) firstElement.get("ingestor");
	System.out.println(ingestor);
	String tag=(String) firstElement.get("tag");
	System.out.println(tag);
	JSONArray measurementDetails = (JSONArray) firstElement.get("measurement");
	System.out.println(measurementDetails);
	//System.out.println(measurementDetails.length());
	IngestionRequestBuilder ingestionBuilder = IngestionRequestBuilder.createIngestionRequest()
			.withMessageId(ProcessDataPoints.generateEpochTime())
			.addIngestionTag(IngestionTag.Builder.createIngestionTag()
					.withTagName(tag)
					.addDataPoints(processDataPoints.saveDataPoints(measurementDetails))
					.addAttribute("host", "java_application")
					.addAttribute("ingestor", ingestor)
					.addAttribute("comments", "Ongoing_Process_Details")
					.build());
	JSONObject timeseriesJSON=null;
	
	try {
		timeseriesJSON = new JSONObject(ingestionBuilder.build().get(0));
	} catch (JSONException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	}
	 
	if(wsc.getWebsocket().isOpen()){
		responseObj = new org.json.JSONObject(wsc.getWebsocket().sendText(timeseriesJSON.toString()));
		responseObj.put("timeseriesData", timeseriesJSON);
	}
	

  //  data.add(responseObj);
  responseObj.toString();
  partsWsBarrier.reportDone();


	} 
}
