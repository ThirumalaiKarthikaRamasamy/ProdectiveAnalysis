package com.ge.util;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.json.JSONObject;
public class GetRequestTask {
        private ExecuteRestAPIRequests work;
        private FutureTask<String> task;
        public GetRequestTask(JSONObject requestObject, Executor executor) {
            this.work = new ExecuteRestAPIRequests(requestObject);
            this.task = new FutureTask<String>(work);
            executor.execute(this.task);
        }
        public boolean isDone() {
            return this.task.isDone();
        }
        public String getResponse() {
            try {
                return this.task.get();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }