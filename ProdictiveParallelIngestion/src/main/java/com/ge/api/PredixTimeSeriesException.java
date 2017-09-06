package com.ge.api;

/**
 * Created by 212563761 on 4/19/16.
 */
public class PredixTimeSeriesException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7047974835664314075L;

	public PredixTimeSeriesException(String message) {
        super(message);
    }

    public PredixTimeSeriesException(String message, Exception e) {
        super(message, e);
    }
}
