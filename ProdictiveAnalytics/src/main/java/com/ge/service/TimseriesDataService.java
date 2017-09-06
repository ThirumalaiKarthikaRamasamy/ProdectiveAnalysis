package com.ge.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.ge.util.GetRequestTask;
import com.ge.util.TimeSeriesDetails;

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
	

	//public static final Integer DATA_POINT = 512;
	
	public static final String DATA_ORDER = "asc";

	public static final String TYPE = "interpolate";
	
	public JSONArray getTimeseriesData(TimeSeriesDetails timeseries,JSONArray tags, String accessToken, String zoneId){
		stopWatch = new StopWatch();
		stopWatch.start();
		long start_time =0;
		long end_time =0;
		try{
			if(timeseries.getFormat().equals("date")){
			 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 Date date = df.parse(timeseries.getStart_time());
			 start_time = date.getTime();
			 log.info("start_time : "+start_time);
			 Date date1 = df.parse(timeseries.getEnd_time());
			 end_time = date1.getTime();
			 log.info("end_time : "+end_time);
			}
			else if(timeseries.getFormat().equals("epoch")){
				start_time = Long.parseLong(timeseries.getStart_time());
				end_time = Long.parseLong(timeseries.getEnd_time());
				 log.info("start_time : "+start_time);
				 log.info("end_time : "+end_time);
			}
		}
		catch(Exception e){
			e.getMessage();	
		}
		List<GetRequestTask> tasks = new ArrayList<GetRequestTask>();
		JSONArray responseList = new JSONArray();
		JSONArray responset = new JSONArray();
		String tagName = "";
		JSONObject requestObject = null, fetchDataRequest = null,requestaggObject= null;
		JSONArray tagsArray = null;
		JSONArray aggregationsArray = null;
		GetRequestTask requestTask = null;

		ArrayList<String> taglist = timeseries.getTag_list();
		
		for(int index=0; index<tags.length(); index++){
			for(int i=0;i<taglist.size();i++){
			if(taglist.get(i).equals(tags.get(index).toString())){
			fetchDataRequest = new JSONObject();
			requestObject = new JSONObject();
			tagsArray = new JSONArray();
			aggregationsArray = new JSONArray();
			tagName = tags.get(index).toString();
			
			requestaggObject = new JSONObject();
			requestaggObject.put("datapoints", timeseries.getSample_count());
			requestObject = new JSONObject();
			requestObject.put("type", TYPE);
			requestObject.put("sampling", requestaggObject);
			aggregationsArray.put(requestObject);
			
			//Timeseries Request Object
			requestObject = new JSONObject();
			requestObject.put("name", tagName);
			requestObject.put("order", DATA_ORDER);
			requestObject.put("aggregations", aggregationsArray);
			tagsArray.put(requestObject);
			
			requestObject = new JSONObject();
			requestObject.put("tags", tagsArray);
			requestObject.put("start", start_time);
			requestObject.put("end", end_time);
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
			}
		}
		while(!tasks.isEmpty()) {
			for(Iterator<GetRequestTask> it = tasks.iterator(); it.hasNext();) {
				GetRequestTask task = it.next();
				if(task.isDone()) {
					String response = task.getResponse();
					try{
						//Parsable JSON
						JSONObject responseJson = new JSONObject(response);
						if(timeseries.getFormat().equals("date")){
						JSONObject responsesmpl = new JSONObject();
						JSONArray tagJsonArray = (JSONArray)responseJson.get("tags");
						JSONArray resultsJsonAry=(JSONArray) tagJsonArray.getJSONObject(0).get("results");
						JSONArray valuesJsonAry=(JSONArray)resultsJsonAry.getJSONObject(0).get("values");
						JSONArray datelistAry = new JSONArray();
						for(int j=0;j<valuesJsonAry.length();j++){
						JSONArray arra =(JSONArray) valuesJsonAry.get(j);
						String epocVal = arra.get(0).toString();
						 log.info("Response epocVal : "+epocVal);
						 Date dateformt = new Date(Long.parseLong(epocVal));
						 DateFormat readFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss zzz yyyy");

						    DateFormat writeFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS");
						    Date date = null;
						    try {
						       date = readFormat.parse( dateformt.toString() );
						    } catch ( ParseException e ) {
						        e.printStackTrace();
						    }

						    String formattedDate = "";
						    if( date != null ) {
						    formattedDate = writeFormat.format( date );
						    }

						    log.info("Response formatted Date : "+formattedDate);
						
						JSONArray dateformtAry = new JSONArray();
						dateformtAry.put(formattedDate);
						dateformtAry.put(arra.get(1));
						dateformtAry.put(arra.get(2));
						datelistAry.put(dateformtAry);
						}
						responsesmpl.put("values", datelistAry);
						valuesJsonAry = new JSONArray();
						valuesJsonAry.put(0, responsesmpl);

						tagJsonArray.getJSONObject(0).put("results", valuesJsonAry);
						responseJson.put("tags", tagJsonArray);
						responseList.put(responseJson);
						}
						else if(timeseries.getFormat().equals("epoch")){
							responseList.put(responseJson);
						}
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
