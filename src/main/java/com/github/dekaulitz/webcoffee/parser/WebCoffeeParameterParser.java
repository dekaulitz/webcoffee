package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.models.parameters.CookieParameter;
import com.github.dekaulitz.webcoffee.models.parameters.HeaderParameter;
import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import com.github.dekaulitz.webcoffee.models.parameters.PathParameter;
import com.github.dekaulitz.webcoffee.models.parameters.QueryParameter;
import java.util.HashSet;
import java.util.Set;

public class WebCoffeeParameterParser {

  private static final String QUERY_PARAMETER = "query";
  private static final String COOKIE_PARAMETER = "cookie";
  private static final String PATH_PARAMETER = "path";
  private static final String HEADER_PARAMETER = "header";

  public static Set<Parameter> getParameters(ArrayNode parameters) {
    Set<Parameter> webCoffeeParameters = new HashSet<>();
    parameters.forEach(jsonNode -> {
      webCoffeeParameters.add(getParameter(jsonNode));
    });
    return webCoffeeParameters;
  }

  private static Parameter getParameter(JsonNode item) throws WebCoffeeException {
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
    parameter.setName(NodeHelper.getNodeString((ObjectNode) item, "name", false));
    parameter.setIn(value);
    parameter.setValue(NodeHelper.getNodeString((ObjectNode) item, "value", false));
    parameter.setArgument(NodeHelper.getNodeString((ObjectNode) item, "argument", false));
    return parameter;
  }
}
