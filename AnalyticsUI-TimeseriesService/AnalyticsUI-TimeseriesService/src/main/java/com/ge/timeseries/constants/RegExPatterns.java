package com.ge.timeseries.constants;

public class RegExPatterns {
	public final static String SINGLE_DATAPOINT="^\\[(\\[{1}[0-9]{3,}[,]{1}[0-9.]{1,}[,]{1,}[0-9]{1,}\\]{1})\\]$";
	public final static String MULTIPLE_DATAPOINTS="^\\[((\\[{1}[0-9]{3,}[,]{1}[0-9.]{1,}[,]{1,}[0-9]{1}\\]{1}[,]{1}){1,}"
			+ "((\\[{1}[0-9]{3,}[,]{1}[0-9.]{1,}[,]{1,}[0-9]{1,}\\]{1}){1,})\\])$";
	
}
