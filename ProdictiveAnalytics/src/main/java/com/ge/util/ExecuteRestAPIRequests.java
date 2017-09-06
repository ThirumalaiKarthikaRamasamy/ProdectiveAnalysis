package com.ge.util;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

class ExecuteRestAPIRequests implements Callable<String> {
	@Value("${cgMachine.timeseries.queryUrlPrefix}")
	String fetchDataUrl;
	
	HttpURLConnection con;
	
	BufferedReader responseReader;
	
	DataOutputStream requestWriter;
	
	StopWatch stopWatch = null;
	
    private JSONObject requestObject;
     
    private final Logger log = Logger.getLogger(this.getClass());
    
    JSONObject attributes;
    
	public JSONObject getRequestObject() {
		return requestObject;
	}
	public void setRequestObject(JSONObject requestObject) {
		this.requestObject = requestObject;
	}
	public ExecuteRestAPIRequests(JSONObject requestObject) {
		this.requestObject = requestObject;
	}
	@Override
    public String call() throws IOException
	{
		attributes = new JSONObject();
		attributes.put("host", "localhost");
		attributes.put("cause", "migration");
		attributes.put("uploaded-by", "Capgemini");
		
		stopWatch = new StopWatch();
		stopWatch.start();
    	String accessToken = requestObject.getString("accessToken"), 
    			zoneId = requestObject.getString("zoneId"),
    			fetchDataUrl = requestObject.getString("fetchDataUrl"),
    			urlEndPoint = requestObject.getString("urlEndPoint"),
    			requestBody = requestObject.getString("requestBody");
		String responseCode ="";
		if(accessToken.indexOf("bearer")==-1 && accessToken.indexOf("Bearer")==-1){
			accessToken = "Bearer "+accessToken;
		}
		try{
			String url = fetchDataUrl+"/datapoints"+urlEndPoint.trim();
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", accessToken);
			con.setRequestProperty("predix-zone-id", zoneId);
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(((new JSONObject(requestBody)).toString()).getBytes());
			os.close();
			responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			JSONObject response = new JSONObject(responseReader.readLine());
			
			responseCode = new Integer(con.getResponseCode()).toString();
			if(!responseCode.equalsIgnoreCase("200"))
				response.put("statusCode", responseCode);
			return response.toString();
		}
		catch(Exception e)
		{
			JSONObject error = new JSONObject();
			error.put("error", e.getMessage());
			return error.toString();
		}finally {
			stopWatch.stop();
			log.info("Tag Data Fetch terminated in: "+stopWatch.getTotalTimeSeconds()+"s");
		}
	}
}
