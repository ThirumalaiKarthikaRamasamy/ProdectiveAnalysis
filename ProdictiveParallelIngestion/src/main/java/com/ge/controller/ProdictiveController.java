package com.ge.controller;

import java.io.IOException;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Scope;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.ge.api.DataPoint;
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
	@Qualifier("restTemplate")
	private OAuth2RestTemplate restTemplate;
	@Autowired
	WebSocketClient wsc;
	@Autowired
	
	MyRunnable myrunnable;
	@RequestMapping(value="data/myRunnableClass", method=RequestMethod.POST)
	public String getdetails( @RequestBody String ingestorDocument)throws IOException{
		
		ArrayList<Object> data = new ArrayList<Object>();
        
	      JSONObject json = new JSONObject(ingestorDocument);
	      ThreadPoolExecutor pool = ThreadPoolSingleton.getInstance()
                                  .getThreadPoolExecutor();
	      Map<String,Object> mapData=new HashMap<String, Object>();

                           // getting first index value
	      				JSONObject result;
                          
                           int sitesCnt = 2; // 5

                           WebServicesBarrier partsWsBarrier;

                           // Adding timeout as maximum as possible
                           partsWsBarrier = new WebServicesBarrier(sitesCnt, 3600000);

                          MyRunnable[] task = new MyRunnable[sitesCnt];
                        
                           // Creating tasks with split set of sites
                          
                           for (int arr = 0; arr <2; arr++) {
                                  task[arr] = new MyRunnable(partsWsBarrier,json,processDataPoints,wsc,restTemplate,arr);
                           }
                           for (int arr = 0; arr <2; arr++) {
	
                        	   	Thread t = new Thread(task[arr]);
                        	   	t.start();
                        	   	t.run();

                        	   	result=task[arr].responseObj;
                        	   	System.out.println("************"+result);
                        	   	
                        	   	data.add(result);
                        	   	t.stop();
                            //  pool.execute(task[arr]);
                           }   
                        
                           System.out.println(data);
                           // Triggering the barrier with added tasks
                           partsWsBarrier.waitForWorkCompletion();
				
                    return data.toString();

	}


	
	
	@RequestMapping(value="/data/ingestData", method=RequestMethod.POST)
	public String ingest( @RequestBody String ingestorDocument) throws IOException {
		try {
	      
			ArrayList<Object> data = new ArrayList<Object>();
            
	      JSONObject json = new JSONObject(ingestorDocument);
	      JSONArray ingestorContent = (JSONArray) json.get("data");
	      System.out.println(ingestorContent.length());
	      int sitesCnt = 2;// 5
	     
	       for (int i = 0, size = ingestorContent.length(); i < size; i++){
	        
	    	   JSONObject object= (JSONObject)ingestorContent.get(i);
	        	
				String ingestor = (String) object.get("ingestor");
				String tag=(String) object.get("tag");
				JSONArray measurementDetails = (JSONArray) object.get("measurement");
				IngestionRequestBuilder ingestionBuilder = IngestionRequestBuilder.createIngestionRequest()
						.withMessageId(ProcessDataPoints.generateEpochTime())
						.addIngestionTag(IngestionTag.Builder.createIngestionTag()
								.withTagName(tag)
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
				
	            data.add(responseObj);
	       }
	        
	       
	       System.out.println(data);
			return data.toString();
		} catch (Exception e) {	
			e.printStackTrace();
			return new org.json.JSONObject().put("errorMsg", e.getMessage()).toString();
		}
	}
	
	
	
}

