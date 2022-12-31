package com.samknows.responses;

/**
 * Enum that stores all custom failure codes and messages for this application
 * To be used with ResponseBuilder
 */
public enum FailureResponse {
    //failure responses code start with 4000
    FILE_TYPE_NOT_ALLOWED(4001, "The filetype of this attachment is not allowed"),
    ATTACHMENTS_MISSING(4002, "This request expects attachments / multipart form data"),

    //Other Failures
    //Malformed content failures start with code 4401
    JSON_NOT_FORMED_WELL(4401, "The given JSON is not formed well"),

    //Internal Errors
    INTERNAL_EXCEPTION(5001, "An Internal Exception occurred while handling the request");
    private final int code;
    private final String message;

    FailureResponse(int code, String message){
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
