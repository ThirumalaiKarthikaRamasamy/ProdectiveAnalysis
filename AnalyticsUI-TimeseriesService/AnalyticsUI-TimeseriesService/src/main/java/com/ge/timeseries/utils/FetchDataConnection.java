package com.ge.timeseries.utils;

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
public class FetchDataConnection {
	@Value("${brilliantLab.timeseries.queryUrlPrefix}")
	String fetchDataUrl;
	HttpURLConnection con;
	URL obj;
	BufferedReader responseReader;
	DataOutputStream requestWriter;
	public FetchDataConnection() {
		/*System.setProperty("http.proxyHost", "sjc1intproxy01.crd.ge.com"); 
		System.setProperty("http.proxyPort", "8080"); 
		System.setProperty("https.proxyHost", "sjc1intproxy01.crd.ge.com"); 
		System.setProperty("https.proxyPort", "8080");*/
	}
	public void addDataToConnection(HttpURLConnection connection, String urlParameters) throws IOException
	{
		requestWriter = new DataOutputStream(connection.getOutputStream());
		requestWriter.writeBytes(urlParameters);
		requestWriter.flush();
		requestWriter.close();
	}
	public JSONObject createQuery(String accessToken, String zoneId, String requestBody, String urlEndPoint) throws IOException
	{
		String responseCode = "";
		try{
			String url = fetchDataUrl+"/datapoints"+urlEndPoint.trim();
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Authorization", accessToken);
			con.setRequestProperty("predix-zone-id", zoneId);
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write((requestBody.toString()).getBytes());
			os.close();
			responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			JSONObject response = new JSONObject(responseReader.readLine());
			responseCode = new Integer(con.getResponseCode()).toString();
			response.put("statusCode", responseCode);
			return response;
		}
		catch(Exception e)
		{
			JSONObject error = new JSONObject();
			error.put("error", e.getMessage());
			responseCode = new Integer(con.getResponseCode()).toString();
			error.put("statusCode", responseCode);
			return error;
		}
	}
	public JSONObject fetchDataTags(String accessToken, String zoneId) throws IOException
	{
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
			return error;
		}
	}

}
