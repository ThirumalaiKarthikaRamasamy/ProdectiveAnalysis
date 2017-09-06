package com.ge.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ge.service.ProcessDataPoints;
import com.ge.service.ProdictiveServiceImpl;
import com.ge.service.TimseriesDataService;
import com.ge.util.FetchAccessTokens;
import com.ge.util.TimeSeriesDetails;
import com.ge.util.TimeseriesFetchDataConnection;
import com.ge.util.WorkflowStep;




@RestController
@RequestMapping("/prodictive")
public class ProdictiveController {
	
	
	@Autowired
	FetchAccessTokens fetchAccessToken;
	
	@Autowired
	TimeseriesFetchDataConnection fetchData;
	
	@Autowired
	TimseriesDataService timeseriesService;
	
	@Autowired
	ProcessDataPoints processDataPoints;
	JSONObject responseObj = null;
	
	@Autowired
	ProdictiveServiceImpl prodictiveServiceImpl;
	
	
	@Value("${cgMachine.timeseries.zoneId}")
    String zoneId;
	
	@RequestMapping("/getData")
	public String getUser(){
		return "Prodictive Analytics PoC";
	}
	
	@RequestMapping(value="/pts/getData", produces={"application/json"}, method=RequestMethod.POST)
	public ResponseEntity<String> getTimeseriesData(@RequestBody TimeSeriesDetails timeseries)  {
		JSONObject timeseriesTags;
		int statusCode = 200;
		try {
			timeseriesTags = new JSONObject(fetchData.fetchDataTags(fetchAccessToken.getBasicEnvToken(), zoneId).toString());
			if(!timeseriesTags.has("error")){
				return new ResponseEntity<String>(timeseriesService.getTimeseriesData(timeseries,timeseriesTags.getJSONArray("results"), fetchAccessToken.getBasicEnvToken(), zoneId).toString(), HttpStatus.valueOf(statusCode));
			}else{
				return new ResponseEntity<String>(timeseriesTags.toString(), HttpStatus.valueOf(500));
			}
		} catch (Exception e) {
			timeseriesTags = new JSONObject();
			timeseriesTags.put("status", 500);
			timeseriesTags.put("error", e.getMessage());
			e.printStackTrace();
			statusCode = 500;
			return new ResponseEntity<String>(timeseriesTags.toString(), HttpStatus.valueOf(statusCode));
		}
	}
	
	@RequestMapping(value="/data/insertWorkflowStep", method=RequestMethod.POST)
	public Boolean insertWorkflowTimeRetrieval(@RequestBody Map<String, ArrayList<String>> workflowStep) {
		return prodictiveServiceImpl.insertWorkflowTimeRetrieval(workflowStep);
	}
	
		
}

