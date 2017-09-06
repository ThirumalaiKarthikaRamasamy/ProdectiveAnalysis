package com.ge.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class IngestionTag {

    private static transient int TAG_RESIDUAL_SIZE = 50;
    private static transient int DATAPOINTS_RESIDUAL_SIZE = 16;
    private static transient int ATTRIBUTES_RESIDUAL_SIZE = 16;
    private static transient int DATAPOINT_RESIDUAL_SIZE = 6;
    private static transient int ATTRIBUTE_RESIDUAL_SIZE = 6;


    @SerializedName("name")
    private String name;

    @SerializedName("datapoints")
    private List<DataPoint> datapoints;

    @SerializedName("attributes")
    private Map<String, String> attributes;


    private IngestionTag() {
        datapoints = new ArrayList<>();
        attributes = new HashMap<>();
    }

    public int getSerializedJsonSizeEstimate() {
        int jsonSerializationEstimate = IngestionTag.TAG_RESIDUAL_SIZE; // Residual size;
        jsonSerializationEstimate += this.name.length() + 2;

        if (null != this.datapoints) {
            jsonSerializationEstimate += IngestionTag.DATAPOINTS_RESIDUAL_SIZE;
            for (DataPoint dp : this.datapoints) {
                jsonSerializationEstimate += (IngestionTag.DATAPOINT_RESIDUAL_SIZE + dp.getSerializedJsonSizeEstimate());
            }
        }

        if (null != this.attributes) {
            jsonSerializationEstimate += IngestionTag.ATTRIBUTES_RESIDUAL_SIZE;
            for (String key : this.attributes.keySet()) {
                jsonSerializationEstimate += (IngestionTag.ATTRIBUTE_RESIDUAL_SIZE + key.length() + this.attributes.get(key).length());
            }
        }

        return jsonSerializationEstimate;
    }

//    public class Serializer implements JsonSerializer<IngestionTag> {
//
//        @Override
//        public JsonElement serialize(IngestionTag ingestionTag, Type type, JsonSerializationContext jsonSerializationContext) {
//
//        }
//    }

    public static final class Builder {

        private IngestionTag tag = new IngestionTag();

        public static IngestionTag.Builder createIngestionTag() {
            return new Builder();
        }

        public IngestionTag.Builder withTagName(String tagName) {
            this.tag.name = tagName;
            return this;
        }

        public IngestionTag.Builder addDataPoints(List<DataPoint> dataPoints) {
            this.tag.datapoints.addAll(dataPoints);
            return this;
        }

        public IngestionTag.Builder withAttributes(Map<String, String> attributes) {
            this.tag.attributes.putAll(attributes);
            return this;
        }

        public IngestionTag.Builder addAttribute(String key, String value) {
            this.tag.attributes.put(key, value);
            return this;
        }


        public IngestionTag build() {


            return this.tag;
        }
    }

}
