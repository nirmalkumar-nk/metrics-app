package com.samknows.responses;

/**
 * ResponseBuilder is a utility class that is to be used for Generating Responses
 */

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samknows.utility.JSONUtility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {

    public static ResponseEntity<Object> success(){
        Map<String, Object> responseMap = new HashMap<String, Object>();

        responseMap.put(ResponseKey.CODE.name().toLowerCase(), SuccessResponse.SUCCESS.getCode());
        responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), SuccessResponse.SUCCESS.getMessage());

        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(responseMap, responseHeader, HttpStatus.OK);
    }

    public static HttpServletResponse requestNotFound(ServletResponse servletResponse){
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        return response;
    }

    public static HttpServletResponse badRequest(ServletResponse servletResponse, FailureResponse failureResponse) throws IOException {
        ObjectNode responseMap = JSONUtility.getJSONNode();

        responseMap.put(ResponseKey.CODE.name().toLowerCase(), failureResponse.getCode());
        responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), failureResponse.getMessage());

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        PrintWriter responseWriter = response.getWriter();
        responseWriter.print(responseMap);
        responseWriter.close();

        return response;
    }

    public static ResponseEntity badRequest(FailureResponse failureResponse) {
        ObjectNode responseMap = JSONUtility.getJSONNode();

        responseMap.put(ResponseKey.CODE.name().toLowerCase(), failureResponse.getCode());
        responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), failureResponse.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseMap);
    }

    public static HttpServletResponse internalServerError(ServletResponse servletResponse) {
        ObjectNode responseMap = JSONUtility.getJSONNode();

        responseMap.put(ResponseKey.CODE.name().toLowerCase(), FailureResponse.INTERNAL_EXCEPTION.getCode());
        responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), FailureResponse.INTERNAL_EXCEPTION.getMessage());

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            PrintWriter responseWriter = response.getWriter();
            responseWriter.print(responseMap);
            responseWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public static ResponseEntity internalServerError(){
        ObjectNode responseMap = JSONUtility.getJSONNode();

        responseMap.put(ResponseKey.CODE.name().toLowerCase(), FailureResponse.INTERNAL_EXCEPTION.getCode());
        responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), FailureResponse.INTERNAL_EXCEPTION.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseMap);
    }

    public static ResponseEntity writeFile(byte[] file){
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);
    }

}
