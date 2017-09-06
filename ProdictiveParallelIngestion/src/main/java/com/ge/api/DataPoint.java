package com.ge.api;
import java.lang.reflect.Type;
import java.util.Iterator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
/**
 * A measurement. Contains the time when the measurement occurred and its value.
 */
public class DataPoint {
    private static final int ASSUMED_VALUE_SIZE = 18;
    private Long timestamp;
    private Object value;
    private Quality quality;

    public DataPoint() {
    }

    public DataPoint(Long timestamp, Object value) {
        this(timestamp, value, Quality.GOOD);
    }

    public DataPoint(Long timestamp, Object value, Quality quality) {
        this.timestamp = timestamp;
        this.value = value;
        this.quality = quality;
    }

    /**
     * Time when the data point was measured.
     *
     * @return time when the data point was measured
     */
    public long getTimestamp() {
        return timestamp.longValue();
    }

    public Object getValue() {
        return value;
    }

    public String stringValue() {
        return value.toString();
    }

    public long longValue() throws PredixTimeSeriesException {
        try {
            return ((Number) value).longValue();
        } catch (Exception e) {
            throw new PredixTimeSeriesException("Value is not a long");
        }
    }

    public double doubleValue() throws PredixTimeSeriesException {
        try {
            return ((Number) value).doubleValue();
        } catch (Exception e) {
            throw new PredixTimeSeriesException("Value is not a double");
        }
    }

    public Quality getQuality() {
        return quality;
    }

    public boolean isDoubleValue() {
        return !(((Number) value).doubleValue() == Math.floor(((Number) value).doubleValue()));
    }

    public boolean isIntegerValue() {
        return ((Number) value).doubleValue() == Math.floor(((Number) value).doubleValue());
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataPoint dataPoint = (DataPoint) o;

        return timestamp == dataPoint.timestamp && value.equals(dataPoint.value);

    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + value.hashCode();
        return result;
    }

    public int getSerializedJsonSizeEstimate() {
        return 15 + ((value instanceof String) ? ((String) value).length() : DataPoint.ASSUMED_VALUE_SIZE) + 2;
    }

    public static class Deserializer implements JsonDeserializer<DataPoint> {

        @Override
        public DataPoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
            long ts = iterator.next().getAsLong();
            Object val = iterator.next();
            String q = iterator.next().getAsString();
            DataPoint dp = new DataPoint(ts, val, Quality.fromString(q));

            return dp;
        }
    }

    public static class IngestionSerializer implements JsonSerializer<DataPoint> {

        @Override
        public JsonElement serialize(DataPoint dataPoint, Type type, JsonSerializationContext jsonSerializationContext) {
            Object[] dp = new Object[3];
            dp[0] = dataPoint.getTimestamp();
            dp[1] = dataPoint.getValue();
            dp[2] = dataPoint.getQuality().getName();
            return jsonSerializationContext.serialize(dp);
        }
    }
}