package com.ge.api;
import com.google.gson.annotations.SerializedName;

public class IngestionResponse {

    @SerializedName("statusCode")
    private Integer statusCode;

    @SerializedName("messageId")
    private String messageId;


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

	@Override
	public String toString() {
		return "{\"statusCode\":\"" + statusCode + "\",\"messageId\":\""
				+ messageId + "\"}";
	}
    
}
