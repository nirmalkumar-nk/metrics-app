package com.samknows.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * ResponseBuilder is a utility class that is to be used for Generating Responses
 */
public class ResponseBuilder {

    public static ResponseEntity<Object> success(){
        Map<String, Object> map = new HashMap<>();

        map.put(ResponseKey.CODE.name().toLowerCase(), SuccessResponse.SUCCESS.getCode());
        map.put(ResponseKey.MESSAGE.name().toLowerCase(), SuccessResponse.SUCCESS.getMessage());

        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(map, responseHeader, HttpStatus.OK);
    }
}
