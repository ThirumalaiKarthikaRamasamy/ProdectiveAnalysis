package com.ge.machine.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.ge.machine.util.GetRequestTask;
import com.ge.machine.util.IngestData;
@Service
public class TimseriesDataService {
	private Executor executor;

	public TimseriesDataService(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(100);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("TimeseriesDataThread-");
		executor.initialize();
		this.executor=executor;
	}
	
	StopWatch stopWatch = null;
	
	private final Logger log = Logger.getLogger(this.getClass());
	
	@Value("${cgMachine.timeseries.queryUrlPrefix}")
	String fetchDataUrl;
	
	@Autowired
	IngestData ingestData;

	public static final String START_TIME = "100y-ago";
	public static final String DATA_ORDER = "asc";

	public JSONArray getTimeseriesData(JSONArray tags, String accessToken, String zoneId){
		stopWatch = new StopWatch();
		stopWatch.start();
		List<GetRequestTask> tasks = new ArrayList<GetRequestTask>();
		JSONArray responseList = new JSONArray();
		String tagName = "";
		JSONObject requestObject = null, fetchDataRequest = null;
		JSONArray tagsArray = null;
		GetRequestTask requestTask = null;
		for(int index=0; index<tags.length(); index++){
			fetchDataRequest = new JSONObject();
			requestObject = new JSONObject();
			tagsArray = new JSONArray();
			tagName = tags.get(index).toString();
			//Timeseries Request Object
			requestObject.put("name", tagName);
			requestObject.put("order", DATA_ORDER);
			tagsArray.put(requestObject);
			requestObject = new JSONObject();
			requestObject.put("tags", tagsArray);
			requestObject.put("start", START_TIME);
			//RestTemplate Request Object
			fetchDataRequest.put("accessToken", accessToken);
			fetchDataRequest.put("zoneId", zoneId);
			fetchDataRequest.put("urlEndPoint", "");
			fetchDataRequest.put("fetchDataUrl", fetchDataUrl);
			fetchDataRequest.put("requestBody", requestObject.toString());
			//Parallel Executor
			requestTask = new GetRequestTask(fetchDataRequest, executor);
			tasks.add(requestTask);
		}
		while(!tasks.isEmpty()) {
			for(Iterator<GetRequestTask> it = tasks.iterator(); it.hasNext();) {
				GetRequestTask task = it.next();
				if(task.isDone()) {
					String response = task.getResponse();
					try{
						//Parsable JSON
						JSONObject responseJson = new JSONObject(response);
						responseJson.put("ingestion", ingestData.ingestTimeseriesData(responseJson));
						responseList.put(responseJson);
					}catch(Exception ex){
						//Unparsable JSON
						responseList.put(response);
					}
					it.remove();
				}
			}
			if(!tasks.isEmpty())
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		stopWatch.stop();
		log.info("Timeseries data fetch completed in "+stopWatch.getTotalTimeSeconds()+"s");
		return responseList;
	}
}
