package com.samknows.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A Utility class for JSON related operations
 */

public class JSONUtility {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static ObjectNode getJSONNode(){
        return mapper.createObjectNode();
    }

    public static boolean isValidJSON(String jsonString){
        try {
            mapper.readTree(jsonString);
        } catch (JsonMappingException e) {
            return false;
        } catch (JsonProcessingException e) {
            return false;
        }
        return true;
    }

    public static JsonNode getJSONNode(String jsonString){
        JsonNode jsonNode;
        try{
            jsonNode = mapper.readTree(jsonString);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return jsonNode;
    }
}
