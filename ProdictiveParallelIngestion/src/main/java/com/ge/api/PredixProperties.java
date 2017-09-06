package com.ge.api;

import java.io.IOException;
public class PredixProperties {


    private static java.util.Properties props;
    private static void loadProperties() throws IOException {
        java.util.Properties p = new java.util.Properties();
        p.load(PredixProperties.class.getResourceAsStream("/predix-timeseries.properties"));
        props = p;
    }

    public static String getProperty(String key) throws IOException {
        if (null == props)
            loadProperties();
        return props.getProperty(key);
    }

    public static class Timeseries extends PredixProperties {
        public static final String MAX_TAGS_PER_QUERY = "predix.timeseries.maxTagsPerQuery";
        public static final String MAX_INGESTION_MESSAGE_SIZE = "predix.timeseries.maxIngestionMessageSize";
    }

    public static class Plan extends PredixProperties {
        public static final String INGESTION_CONCURRENT_CONNECTIONS_MAX = "plan.ingestion.concurrent.connections.max";
        public static final String QUERY_CONCURRENT_CONNECTIONS_MAX = "plan.query.concurrent.connections.max";
    }

    public static class Operational extends PredixProperties {
        public static final String UAA_URI = "predix.timeseries.uaa.uri";

        public static final String INGESTION_URI = "predix.timeseries.ingestion.uri";
        public static final String INGESTION_ZONE_HEADER_NAME = "predix.timeseries.ingestion.zone-http-header-name";
        public static final String INGESTION_CLIENT_ID = "predix.timeseries.ingestion.client.id";
        public static final String INGESTION_CLIENT_SECRET = "predix.timeseries.ingestion.client.secret";
        public static final String INGESTION_CLIENT_SECRET_ENV_VARIABLE = "predix.timeseries.ingestion.client.secret.env.variable";

        public static final String QUERY_URI = "predix.timeseries.query.uri";
        public static final String QUERY_ZONE_HEADER_NAME = "predix.timeseries.query.zone-http-header-name";
        public static final String QUERY_CLIENT_ID = "predix.timeseries.query.client.id";
        public static final String QUERY_CLIENT_SECRET = "predix.timeseries.query.client.secret";
        public static final String QUERY_CLIENT_SECRET_ENV_VARIABLE = "predix.timeseries.query.client.secret.env.variable";
    }
}
