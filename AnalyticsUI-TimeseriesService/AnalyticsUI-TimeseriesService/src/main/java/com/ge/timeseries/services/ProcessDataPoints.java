package com.ge.timeseries.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.timeseries.timeseries.api.DataPoint;
import com.ge.timeseries.timeseries.api.Quality;
import com.ge.timeseries.utils.Validations;

@Service
public class ProcessDataPoints {
	static Random random = new Random(System.currentTimeMillis());
	@Autowired
	Validations validationUtil;
	public String generateEpochTime(){
		Date date = new Date();
		String epoch = new Long(date.getTime()).toString();
		return epoch;
	}
	private final static Integer TIMESTAMP_INDEX = 0; 
	private final static Integer DATA_INDEX = 1;
	private final static Integer QUALITY_INDEX = 2;
	public String processData(String requestData){
		JSONObject outputJSON = new JSONObject();
		boolean checkFlag = false;
		if(!validationUtil.nullCheck(requestData) && !validationUtil.stringLengthCheck(requestData) &&
				!validationUtil.notValidJSON(requestData)){
			JSONObject inputJSON = new JSONObject(requestData);
			if(inputJSON.has("datapoints") && inputJSON.has("name")){
				if(validationUtil.validateMultipleDatapoint(inputJSON.getString("datapoints"))){
					outputJSON.put("messageId", generateEpochTime());
					outputJSON.put("body",inputJSON );
					checkFlag = true;
				}
				else if(validationUtil.validateSingleDatapoint(inputJSON.getString("datapoints"))){
					outputJSON.put("messageId", generateEpochTime());
					outputJSON.put("body",inputJSON );
					checkFlag = true;
				}
			}
		}
		if(checkFlag==true)
			return outputJSON.toString();
		else
			throw new NullPointerException();
	}
	public List<DataPoint> generateDataPoints(int limit){
		List<DataPoint> dataPoints = new ArrayList<>();
		long timeInMillis = System.currentTimeMillis();
		for(int index=0; index<(int)limit/2; index++){
			dataPoints.add(new DataPoint(timeInMillis++, randomDoubleValue(), Quality.NOT_APPLICABLE));
			dataPoints.add(new DataPoint(timeInMillis++, randomIntValue(), Quality.GOOD));
		}
		return dataPoints;
	}
	public List<DataPoint> saveDataPoints(JSONArray measurementDetails){
		List<DataPoint> dataPoints = new ArrayList<>();
		JSONArray innerData = null; Quality quality=Quality.GOOD;
		for(int index=0; index<measurementDetails.length();index++){
			innerData = (JSONArray) measurementDetails.get(index);
			try{
				if(!innerData.isNull(2)){
					quality= Quality.fromString(innerData.get(QUALITY_INDEX).toString());
					if(quality==null){
						throw new Exception("Null Quality Excpetion");
					}
				}else{
					throw new Exception("Null Quality Excpetion");
				}
			}catch(Exception e){
				quality = Quality.GOOD;
			}
			dataPoints.add(new DataPoint(Long.parseLong(innerData.get(TIMESTAMP_INDEX).toString()),innerData.get(DATA_INDEX).toString(), quality));
		}
		return dataPoints;
	}
	public List<List<Object>> generateDataPointsManually(int limit){
		List<List<Object>> dataPoints = new ArrayList<>();
		List<Object> singleDataPoint = null;
		long timeInMillis = System.currentTimeMillis();
		for(int index=0; index<(int)limit; index++){
			singleDataPoint = new ArrayList<>();
			singleDataPoint.add(timeInMillis++);
			singleDataPoint.add(randomDoubleValue());
			singleDataPoint.add(random.nextInt(3));			
			dataPoints.add(singleDataPoint);
		}
		return dataPoints;
	}
	public Integer randomIntValue(){
		return (int) (Math.random()*5);
	}
	public Double randomDoubleValue(){
		return new BigDecimal(Math.random()*10).setScale(0, RoundingMode.HALF_UP).doubleValue();
	}

}
