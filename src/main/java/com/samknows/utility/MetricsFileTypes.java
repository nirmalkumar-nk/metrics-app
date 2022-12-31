package com.samknows.utility;

/**
 * Enum for all file types used in metrics
 */

public enum MetricsFileTypes {
    JSON("json");

    private final String fileType;

    MetricsFileTypes(String fileType){
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}
