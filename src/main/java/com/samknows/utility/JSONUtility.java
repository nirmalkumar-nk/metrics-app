package com.samknows.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A Utility class for JSON related operations
 */

public class JSONUtility {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static ObjectNode getJSONNode(){
        return mapper.createObjectNode();
    }

    public static ArrayNode getArrayNode(){
        return mapper.createArrayNode();
    }

    public static boolean isValidJSON(String jsonString){
        try {
            mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            return false;
        }
        return true;
    }

    public static JsonNode getJSONNode(String jsonString){
        JsonNode jsonNode;
        try{
            jsonNode = mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonNode;
    }
}
