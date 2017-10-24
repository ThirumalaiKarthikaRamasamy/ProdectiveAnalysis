package com.ge.timeseries.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

import com.ge.timeseries.exception.ExSessionOpen;
import com.ge.timeseries.timeseries.api.IngestionResponse;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
@Component
public class WebSocketClient {
	private WebSocketFactory factory;
	private ProxySettings settings;
	protected Session userSession = null;
	private final Logger log = Logger.getLogger(this.getClass());
	
	@Value("${brilliantLab.timeseries.ingestUrl}")
	String ingestUrl;

	@Value("${brilliantLab.timeseries.zoneId}")
	String zoneId;

	@Autowired
	@Qualifier("restTemplate")
	private OAuth2RestTemplate restTemplate;
	
	WebSocket ws = null; 
	public WebSocketClient(){
		this.factory = new WebSocketFactory().setConnectionTimeout(3600000);
		this.settings = this.factory.getProxySettings();
	}
	
	public boolean getWebsocketConnection(){
		try{
			WebSocketAdapter onConnectedListener = onConnectAdapter();
			settings.setServer("http://sjc1intproxy01.crd.ge.com:8080");
			ws = factory.createSocket(ingestUrl);
			ws.addHeader("Predix-Zone-Id", zoneId);
			ws.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+restTemplate.getAccessToken());
			ws.addHeader(HttpHeaders.ORIGIN, "http://localhost");
			ws.connect();
			ws.addListener(onConnectedListener);
			if(ws.isOpen())
				return true;
			else
				throw new Exception("Failed to connect to websocket");
		}catch(Exception e){
			log.error("Error in connecting to websocket: "+e.getMessage());
			e.printStackTrace();
			return false;
		}
			
	}
	
	@SuppressWarnings("nls")
    public WebSocket getWebsocket()
    {
        try
        {
        	if(ws==null){
        		getWebsocketConnection();
        	}else{
        		if(!(ws.getState()==WebSocketState.OPEN || ws.getState()==WebSocketState.CONNECTING)){
        			getWebsocketConnection();
        		}
        	}
            return ws;
        }
        catch (Throwable e)
        {
        	e.printStackTrace();
            throw new RuntimeException("Unable to get websocket connection");
        }
    }
	
	public WebSocketAdapter onConnectAdapter(){
		WebSocketAdapter onConnectedListener = new WebSocketAdapter()
	    {
			private final Logger log = Logger.getLogger(WebSocketClient.class);
	        @SuppressWarnings("nls")
	        @Override
	        public void onConnected(WebSocket websocket, Map<String, List<String>> wsHeaders)
	        {
	            log.info("Websocket Connected.");
	        }
	        
	        @Override
	        public void onDisconnected(WebSocket websocket,
	            WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
	            boolean closedByServer) throws Exception
	        {
	        	log.info("Websocket Disconnected.");
	        }
	        
	        @Override
	        public void onTextMessage(WebSocket websocket, String text) throws Exception
	        {
	        	log.info("Received Response From Timeseries.");
	        }
	        @Override
	        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception
	        {
	        	if(!(newState==WebSocketState.OPEN||newState==WebSocketState.CREATED)){
	        		websocket.recreate();
	        		websocket.connect();
	        	}
	        	log.info("WebSocket in state: "+websocket.getState());
	        }
	    };
		return onConnectedListener;
	}
	public Session getSession() {
		return userSession;
	}

	public void connect(String server, String accessToken, String zoneId) throws DeploymentException, IOException {
		userSession = WebSocketUtils.openWebSocket(server, this, zoneId, accessToken);
		userSession.setMaxIdleTimeout(60000);
	}
	public String sendMessage(String message) {
		final IngestionResponse response = new IngestionResponse();
		CountDownLatch latch = new CountDownLatch(1);
		Session session = getSession();
		if(session.getMessageHandlers().size()<=0){
			session.addMessageHandler(new MessageHandler.Whole<String>() {
				private final Logger logger = Logger.getLogger(this.getClass());
				@Override
				public void onMessage(String message) {
					logger.info("Received message: " + message);
					IngestionResponse resp = new Gson().fromJson(message, IngestionResponse.class);
					response.setMessageId(resp.getMessageId());
					response.setStatusCode(resp.getStatusCode());
					latch.countDown();
				}
			});
		}
		try {
			session.getBasicRemote().sendText(message);
			latch.await();
			userSession.close();
			return (response.toString());
		} catch (IOException | InterruptedException e) {
			return new JSONObject().put("errMsg", e.getMessage()).toString();
		}

	}

	public void disconnect() throws ExSessionOpen{
		if (userSession != null) {
			try {
				if(userSession.getOpenSessions().size()>0){
					log.info("Connection Not Closed. Sessions Open.");
					throw new ExSessionOpen("Another Session In Progress. ");
				}
				else
					userSession.close();
			} catch (IOException e) {
				log.error("ERROR"+this.getClass()+": "+e.getMessage());
			}
		}
	}
    
	public boolean isConnected() {
		return userSession != null;
	}

	@OnOpen
	public void onOpen(Session session) {
		log.info("Connection to WebSocket Established.");
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		log.info("Connection to WebSocket Closed.");
	}
}

