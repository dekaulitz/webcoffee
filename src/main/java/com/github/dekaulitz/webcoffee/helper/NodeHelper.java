package com.github.dekaulitz.webcoffee.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;


public class NodeHelper {

  public static String getNodeString(ObjectNode objectNode, String key, Boolean required) {
    JsonNode result = objectNode.get(key);
    isRequired(result, key, required);
    return result == null ? null : result.asText();
  }

  public static Integer getNodeInteger(ObjectNode objectNode, String key, Boolean required) {
    JsonNode result = objectNode.get(key);
    isRequired(result, key, required);
    return result == null ? 0 : result.asInt();
  }

  public static ObjectNode getObjectNode(JsonNode jsonNode, String key, Boolean required) {
    ObjectNode result = (ObjectNode) jsonNode.get(key);
    isRequired(result, key, required);
    return result;
  }

  public static ArrayNode getArrayNode(JsonNode jsonNode, String key, Boolean required) {
    ArrayNode result = (ArrayNode) jsonNode.get(key);
    isRequired(result, key, required);
    return result;
  }

  public static boolean getBooleanNode(ObjectNode jsonNode, String key, Boolean required) {
    JsonNode result = jsonNode.get(key);
    isRequired(result, key, required);
    return result.asBoolean();
  }

  private static void isRequired(Object isNull, String key, Boolean required) {
    if (Boolean.TRUE.equals(required)) {
      if (isNull == null) {
        throw new WebCoffeeValidationExcepton(key + "is required");
      }
    }
  }
}