/**
 * 
 */
package com.ge.util;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

/**@created: Feb 9, 2017
 * @author: Avneesh Srivastava
 *
 */
@Component
public class FetchAccessTokens {
	/*Predix Basic Environment*/
	@Value("${security.oauth2.client.accessTokenUri}")
	private String basicTokenUrl;
	@Value("${security.oauth2.client.clientId}")
	private String basicClientId;
	@Value("${security.oauth2.client.clientSecret}")
	private String basicClientSecret;

	private String basicToken;

	public String getBasicToken() {
		return basicToken;
	}


	public void setBasicToken(String basicToken) {
		this.basicToken = basicToken;
	}


	private final Logger log = Logger.getLogger(this.getClass());
	
	@PostConstruct
	@Scheduled(cron = "0 0/5 * * * ?")
	public void fetchAccessTokens() throws Exception{
		log.info("Refreshing Access Token");
		this.basicToken = getBasicEnvToken();
	}

	public String getBasicEnvToken(){
		try {
			String token = getToken(basicClientId, basicClientSecret, basicTokenUrl);
			if(token.equalsIgnoreCase("-1")){
				throw new Exception("Failed to extract access token in Predix Basic Environmnent");
			}else{
				return token;
			}
		}catch(Exception e) {
			return "ERR: "+e.getMessage();
		}
	}

	private String getToken(String clientId, String clientSecret, String uaaUrl){
		JSONObject responseObject = null;
		try{
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			StringBuilder authCreds = new StringBuilder();
			authCreds.append(clientId).append(":").append(clientSecret);
			String base64AuthCreds = Base64.encodeBase64String(authCreds.toString().getBytes());
			base64AuthCreds = "Basic "+base64AuthCreds;
			String requestBody ="client_id=$clientId&grant_type=client_credentials";
			requestBody=requestBody.replace("$clientId", clientId);
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			//Set Header
			headers.set("Authorization", base64AuthCreds);
			headers.set("Pragma", "no-cache");
			headers.set("content-type", "application/x-www-form-urlencoded");
			headers.set("Cache-Control", "no-cache");
			ResponseEntity<String> response = rest.exchange(uaaUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);
			if(response.getStatusCode().equals(HttpStatus.OK)){
				responseObject = new JSONObject(response.getBody());
				authCreds = new StringBuilder();
				authCreds.append(responseObject.get("token_type").toString()).append(" ").append(responseObject.get("access_token").toString());
				stopWatch.stop();
			}else{
				stopWatch.stop();
				throw new Exception("Failure in fetch Predix Access Token");
			}
			return authCreds.toString();
		}catch(Exception e){
			e.getMessage();
			return "-1";
		}
	}
}
