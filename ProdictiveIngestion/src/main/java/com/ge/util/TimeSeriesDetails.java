package com.ge.util;
import org.springframework.stereotype.Component;

@Component
public class TimeSeriesDetails {

	public String start_time;
	public String end_time;
	public String sample_count;
	public String date_format;
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getSample_count() {
		return sample_count;
	}
	public void setSample_count(String sample_count) {
		this.sample_count = sample_count;
	}
	public String getDate_format() {
		return date_format;
	}
	public void setDate_format(String date_format) {
		this.date_format = date_format;
	}
	
	
	
}
