package com.samknows.responses;
/**
 * Enum that stores all custom success codes and messages for this application
 * To be used with ResponseBuilder
 */

public enum SuccessResponse {
    SUCCESS(1000, "success");
    private final int code;
    private final String message;
    SuccessResponse(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
