package com.ge.util;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class TimeSeriesDetails {

	public ArrayList<String> tag_list;
	public String start_time;
	public String end_time;
	public String sample_count;
	public String format;
	
	
	public ArrayList<String> getTag_list() {
		return tag_list;
	}
	public void setTag_list(ArrayList<String> tag_list) {
		this.tag_list = tag_list;
	}
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	
	
	
	
}
