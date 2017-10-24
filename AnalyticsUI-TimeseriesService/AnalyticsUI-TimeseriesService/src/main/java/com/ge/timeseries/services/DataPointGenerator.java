package com.ge.timeseries.services;

import java.util.Arrays;
import java.util.Random;

import org.springframework.stereotype.Component;
@Component
public class DataPointGenerator {

    static Random random = new Random(System.currentTimeMillis());

    public static String generateMessage(int dataPointAmount) {
        return "{\"messageId\":\"1453338376222\",\"body\":[{" +
                "\"name\":\"Compressor-2015:CompressionRatio\"," +
                "\"datapoints\":" + generateDataPoints(dataPointAmount) + "," +
                "\"attributes\":{\"host\":\"server1\",\"customer\":\"Acme\"}}]}";
    }


    public static String generateDataPoints(int amount) {
        String[] dataPoints = new String[amount];
        for (int i = 0; i < amount; i++) {
            dataPoints[i] = generateDataPoint();
        }
        return Arrays.toString(dataPoints);
    }

    public static String generateDataPoint() {
        long millis = System.currentTimeMillis();
        int value = random.nextInt(10);
        int quality = random.nextInt(3);
        return Arrays.toString(new Object[]{millis, value, quality});
    }

}
