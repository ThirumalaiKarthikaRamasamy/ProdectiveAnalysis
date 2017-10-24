package com.ge.timeseries.utils;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.ge.timeseries.constants.RegExPatterns;

@Component
public class Validations {

	public boolean nullCheck(Object inputData){
		if(null==inputData){
			return true;
		}
		else{
			return false;
		}
	}
	public boolean stringLengthCheck(String stringData){
		if(stringData==null || stringData.length()<=0){
			return true;
		}
		else
			return false;
	}
	public boolean notValidJSON(String stringData){
		try{
			JSONObject json= new JSONObject(stringData);
			if(!json.keys().hasNext()){
				return true;
			}
			else
				return false;
		}
		catch(JSONException ex){
			return true;
		}
	}
	public boolean validateMultipleDatapoint(String dataPoints){
		return Pattern.compile(RegExPatterns.MULTIPLE_DATAPOINTS).matcher(dataPoints).matches();  
	}
	public boolean validateSingleDatapoint(String dataPoints){
		return Pattern.compile(RegExPatterns.SINGLE_DATAPOINT).matcher(dataPoints).matches();  
	}
}
