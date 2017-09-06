/**
 * Copyright (C) 2016 GE Renewables
 * All rights reserved
 * @version 1.0
 * @author Capgemini
 */
package com.ge.controller;

import java.util.GregorianCalendar;
import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;


public class WebServicesBarrier {
	static final Logger logger = Logger.getLogger(WebServicesBarrier.class);
	int doneWork = 0;
	int totalWork = 0;
	GregorianCalendar startTime = new GregorianCalendar();
	int timeOut = 0;

	public WebServicesBarrier (int totWork, int theTimeOut) {
		totalWork = totWork;
		timeOut = theTimeOut;
	}
	/**
	 * This method is for counting the number of web service calls completed.
	 */
	public  synchronized void reportDone() { 
		//this method has to be synchronized for wait()/notify()/notifyall() to work.
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		++doneWork;
		//if all the work is done let the main waiting thread know the work is done
		if (doneWork == totalWork) {
			notifyAll();
		}
		stopWatch.stop();
	}
	/**
	 * This method allow us to wait until all threads are completed.
	 */
	public synchronized void waitForWorkCompletion () 	{

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		while ((doneWork < totalWork)  && (new GregorianCalendar().getTimeInMillis() < (startTime.getTimeInMillis() + timeOut))) {
			//logger.info("Waiting for completion, Work done: "+doneWork+" Total Work: "+totalWork);
			try {
				wait(1000);	//wait some interval less than the time out
			}
			catch (Exception ex){
				logger.info("WebServicesBarrier,waitForWorkCompletion] Exception "+ex);
			}

		}
		logger.info("All tasks completed");
		stopWatch.stop();
		logger.info("***Total time taken for parallel execution in secs: "+stopWatch.getTotalTimeSeconds()+" ***");

	}


}
