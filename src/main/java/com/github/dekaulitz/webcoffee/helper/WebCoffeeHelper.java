package com.github.dekaulitz.webcoffee.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;

public class WebCoffeeHelper {

  public static String getNodeString(String key, boolean required, JsonNode jsonNode)
      throws WebCoffeeException {
    JsonNode value = jsonNode.get(key);
    if (required && value == null) {
      throw new WebCoffeeException(key + " is required ");
    }
    return value == null ? "" : value.asText();
  }

  public static Integer getNodeInteger(String key, boolean required, JsonNode jsonNode)
      throws WebCoffeeException {
    JsonNode value = jsonNode.get(key);
    if (required && value == null) {
      throw new WebCoffeeException(key + " is required ");
    }
    if(!value.isIntegralNumber()){
      throw new WebCoffeeException("invalid value for "+key);
    }
    return value.asInt();
  }

  public static ObjectNode getNodeObject(String key, boolean required, JsonNode jsonNode)
      throws WebCoffeeException {
    JsonNode value = jsonNode.get(key);
    if (required && value == null) {
      throw new WebCoffeeException(key + " is required ");
    }
    return (ObjectNode) value;
  }

  public static String[] extractString(String value) {
    return value.split("#");
  }


}
