package com.ge.util;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class TimeseriesFetchDataConnection {
	
	@Value("${cgMachine.timeseries.queryUrlPrefix}")
	String fetchDataUrl;
	HttpURLConnection con;
	URL obj;
	BufferedReader responseReader;
	DataOutputStream requestWriter;
	public TimeseriesFetchDataConnection() {

	}
	
	public JSONObject fetchDataTags(String accessToken, String zoneId) throws IOException
	{
		if(accessToken.indexOf("bearer")==-1 && accessToken.indexOf("Bearer")==-1){
			accessToken = "Bearer "+accessToken;
		}
		String responseCode ="";
		try{
			String url = fetchDataUrl+"/tags";
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", accessToken);
			con.setRequestProperty("predix-zone-id", zoneId);
			con.setDoOutput(true);
			responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			JSONObject responseObj = new JSONObject(responseReader.readLine());
			responseCode = new Integer(con.getResponseCode()).toString();
			responseObj.put("statusCode", responseCode);
			return responseObj;
		}
		catch(Exception e)
		{
			JSONObject error = new JSONObject();
			responseCode = new Integer(con.getResponseCode()).toString();
			error.put("statusCode", responseCode);
			error.put("error", e.getMessage());
			return error;
		}
	}

}
