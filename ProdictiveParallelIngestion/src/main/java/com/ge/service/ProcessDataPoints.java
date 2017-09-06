package com.ge.service;

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

import com.ge.api.DataPoint;
import com.ge.api.Quality;


@Service
public class ProcessDataPoints {
	static Random random = new Random(System.currentTimeMillis());
	
	public static String generateEpochTime(){
		Date date = new Date();
		String epoch = new Long(date.getTime()).toString();
		return epoch;
	}
	private final static Integer TIMESTAMP_INDEX = 0; 
	private final static Integer DATA_INDEX = 1;
	private final static Integer QUALITY_INDEX = 2;
	
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
	
	public Integer randomIntValue(){
		return (int) (Math.random()*5);
	}
	public Double randomDoubleValue(){
		return new BigDecimal(Math.random()*10).setScale(0, RoundingMode.HALF_UP).doubleValue();
	}

}
