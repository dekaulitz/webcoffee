package com.github.dekaulitz.webcoffee.modules.openapi.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.modules.openapi.parameters.CookieParameter;
import com.github.dekaulitz.webcoffee.modules.openapi.parameters.HeaderParameter;
import com.github.dekaulitz.webcoffee.modules.openapi.parameters.Parameter;
import com.github.dekaulitz.webcoffee.modules.openapi.parameters.ParameterFrom;
import com.github.dekaulitz.webcoffee.modules.openapi.parameters.PathParameter;
import com.github.dekaulitz.webcoffee.modules.openapi.parameters.QueryParameter;
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

  private static Parameter getParameter(JsonNode item) throws RuntimeException {
    String value = NodeHelper.getNodeString((ObjectNode) item,"in", true);
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
    parameter.setPrefix(NodeHelper.getNodeString((ObjectNode) item, "prefix", false));
    parameter.setSuffix(NodeHelper.getNodeString((ObjectNode) item, "suffix", false));
    parameter.setArgument(NodeHelper.getNodeString((ObjectNode) item, "argument", false));
    parameter.setRequired(NodeHelper.getBooleanNode((ObjectNode) item, "required", false));
    ObjectNode parameterFromExists = NodeHelper.getObjectNode(item, "from", false);
    if (parameterFromExists != null) {
      ParameterFrom parameterFrom = new ParameterFrom();
      parameterFrom
          .set$ref(NodeHelper.getNodeString((ObjectNode) parameterFromExists, "$ref", false));
      parameterFrom.setValue(
          NodeHelper.getNodeString((ObjectNode) parameterFromExists, "value", false));
      parameter.setFrom(parameterFrom);
    }
    return parameter;
  }
}
