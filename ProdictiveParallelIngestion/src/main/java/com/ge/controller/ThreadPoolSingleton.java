/**
 * Copyright (C) 2016 GE Renewables
 * All rights reserved
 * @version 1.0
 * @author Capgemini
 */
package com.ge.controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPoolSingleton {

	private static ThreadPoolSingleton threadPoolSingleton = null;
	private ThreadPoolExecutor threadPoolExecutor = null;
	int poolSize = 100;
	int maxPoolSize = 150;
	long keepAliveTime = 60;
	final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(20);

	/**
	 * Create a private constructor so that the singleton class cannot be
	 * instantiated by the developer explicitly.
	 */
	private ThreadPoolSingleton() {
		threadPoolExecutor = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
	}

	/**
	 * Function to get the singleton instance. It is public static so that any
	 * object can call this function without an instance * of "Singleton" and
	 * get access to the one and only instance of * this class.
	 */
	public static ThreadPoolSingleton getInstance() {
		if (threadPoolSingleton == null) {
			threadPoolSingleton = new ThreadPoolSingleton();
		}

		return threadPoolSingleton;
	}

	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

}
