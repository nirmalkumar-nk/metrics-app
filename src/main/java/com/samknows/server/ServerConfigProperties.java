package com.samknows.server;

/**
 * Enumerating the server properties as in metrics.properties
 */

public enum ServerConfigProperties {
    FILTER_REQUESTS_SCHEMA("metrics.filters.requests.schema"),
    FILTER_REQUESTS_XML("metrics.filters.requests.xml");

    private final String property;
    ServerConfigProperties(String property){
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
