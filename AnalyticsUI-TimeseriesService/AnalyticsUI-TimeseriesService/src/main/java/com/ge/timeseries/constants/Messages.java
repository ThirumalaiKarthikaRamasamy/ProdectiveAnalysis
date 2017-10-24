package com.ge.timeseries.constants;

public class Messages {
	/*Exception*/
	public final static String SESSION_OPEN_MSG ="Another Session In Progress";
	public final static String SESSION_OPEN_CODE ="2001";
	/*Connection*/
	public final static String SUCCESSFUL_CONNECTION_MSG ="Successfully Connected";
	public final static String SUCCESSFUL_CONNECTION_CODE ="1000";
	public final static String UNSUCCESSFUL_CONNECTION_MSG ="Could Not Connect";
	public final static String UNSUCCESSFUL_CONNECTION_CODE ="1001";
	/*Disconnection*/
	public final static String SUCCESSFUL_DISCONNECTION_MSG ="Successfully Disconnected";
	public final static String SUCCESSFUL_DISCONNECTION_CODE ="2000";
	public final static String UNSUCCESSFUL_DISCONNECTION_CODE ="2002";
	public final static String UNSUCCESSFUL_DISCONNECTION_MSG ="Could Not Disconnect";
	/*Query Messages*/
	public final static String FETCH_TAG_SUCCESSFUL_CODE ="3000";
	public final static String FETCH_TAG_INCOMPLETE_CODE ="3000";
	public final static String FETCH_TAG_INCOMPLETE_MSG ="JSON Format Invalid. Please Re-Check Data";
	public final static String FETCH_TAG_UNSUCCESSFUL_CODE ="3002";

}
