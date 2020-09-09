package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getBooleanNode;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.models.parameters.CookieParameter;
import com.github.dekaulitz.webcoffee.models.parameters.HeaderParameter;
import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import com.github.dekaulitz.webcoffee.models.parameters.PathParameter;
import com.github.dekaulitz.webcoffee.models.parameters.QueryParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebCoffeeParameterParser {

  private static final String QUERY_PARAMETER = "query";
  private static final String COOKIE_PARAMETER = "cookie";
  private static final String PATH_PARAMETER = "path";
  private static final String HEADER_PARAMETER = "header";

  public List<Parameter> getParameters(ArrayNode parameters,
      Map<String, WebCoffeeResources> resources) throws WebCoffeeException {
    List<Parameter> parameterList = new ArrayList<>();
    if (parameters == null) {
      return parameterList;
    }
    for (JsonNode item : parameters) {
      if (item.getNodeType().equals(JsonNodeType.OBJECT)) {
        Parameter parameter = getParameter(item);
        if (parameter != null) {
          parameterList.add(parameter);
        }
      }
    }
    return parameterList;
  }

  private Parameter getParameter(JsonNode item) throws WebCoffeeException {
    String value = getNodeString("in", true, item);
    Parameter parameter = null;
    if (QUERY_PARAMETER.equals(value)) {
      parameter = new QueryParameter();
    } else if (HEADER_PARAMETER.equals(value)) {
      parameter = new HeaderParameter();
    } else if (PATH_PARAMETER.equals(value)) {
      parameter = new PathParameter();
    } else if (COOKIE_PARAMETER.equals(value)) {
      parameter = new CookieParameter();
    } else {
      parameter = new Parameter();
    }
    parameter.setName(getNodeString("name",false,item));
    parameter.setIn(value);
    Boolean required = getBooleanNode("required", false, item);
    if (required != null) {
      parameter.setRequired(required);
    } else {
      parameter.setRequired(false);
    }
    parameter.setValue(getNodeString("value", false, item));
    return parameter;
  }
}
