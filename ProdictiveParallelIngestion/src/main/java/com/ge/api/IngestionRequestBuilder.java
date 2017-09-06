package com.ge.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IngestionRequestBuilder {
    private static transient int MESSAGE_RESIDUAL_SIZE = 32;
    transient Logger logger = LoggerFactory.getLogger(IngestionRequestBuilder.class);
    private String messageId;
    private List<IngestionTag> body;
    private transient Gson gson = null;

    private IngestionRequestBuilder() {
        body = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Quality.class, new Quality.Serializer());
        builder.registerTypeAdapter(DataPoint.class, new DataPoint.IngestionSerializer());

        gson = builder.create();
    }

    public static IngestionRequestBuilder createIngestionRequest() {
        return new IngestionRequestBuilder();
    }

    public IngestionRequestBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public IngestionRequestBuilder addIngestionTag(IngestionTag tag) {
        body.add(tag);
        return this;
    }

    public List<String> build() throws IOException {
        List<String> messages = new ArrayList<>();

        int messageSize = estimateMessageSize();

        logger.debug("Estimated message size : " + messageSize);

        if (messageSize > Integer.parseInt(PredixProperties.getProperty(PredixProperties.Timeseries.MAX_INGESTION_MESSAGE_SIZE))) {
            // Break the request.
            messages.addAll(createSizedMessages(this.body));
        } else {
            String payload = gson.toJson(this);
            logger.debug("Actual message size : " + payload.length() + ". The estimation was off by " + (payload.length() - messageSize) + " bytes OR " + ((float) (payload.length() - messageSize) / payload.length() * 100) + " percent.");
            messages.add(payload);
        }

        return messages;
    }

    public int estimateMessageSize() {
        int messageSize = IngestionRequestBuilder.MESSAGE_RESIDUAL_SIZE + messageId.length();
        for (IngestionTag tag : this.body) {
            logger.debug("Estimated tag size : " + tag.getSerializedJsonSizeEstimate());
            messageSize += tag.getSerializedJsonSizeEstimate();
        }
        return messageSize;
    }

    private List<String> createSizedMessages(List<IngestionTag> body) {
        //TODO implement the message splitting algorithm.
        return Arrays.asList(gson.toJson(this));
    }

//    public static class IngestionRequestSerializer implements JsonSerializer<IngestionRequestBuilder>
//    {
//        @Override
//        public JsonElement serialize(IngestionRequestBuilder ingestionRequestBuilder, Type type, JsonSerializationContext jsonSerializationContext) {
////            final JsonObject jsonObject = new JsonObject();
////            jsonObject.add("message");
//            return jsonSerializationContext.serialize(ingestionRequestBuilder);
//        }
//    }
}