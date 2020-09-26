package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.NodeHelper.getNodeString;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.helper.ReferenceHandler;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.models.parameters.CookieParameter;
import com.github.dekaulitz.webcoffee.models.parameters.HeaderParameter;
import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import com.github.dekaulitz.webcoffee.models.parameters.PathParameter;
import com.github.dekaulitz.webcoffee.models.parameters.QueryParameter;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs.PathItem;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecsRequestBody;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecsRequestBodyContentRequest;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebCoffeeSpecParser {

  private static final String QUERY_PARAMETER = "query";
  private static final String COOKIE_PARAMETER = "cookie";
  private static final String PATH_PARAMETER = "path";
  private static final String HEADER_PARAMETER = "header";
  private final ObjectNode specs;

  public WebCoffeeSpecParser(ObjectNode specs) {
    this.specs = specs;
  }

  public Map<String, WebCoffeeSpecs> getSpecs(
      WebCoffee webCoffee) {
    Map<String, WebCoffeeSpecs> webCoffeeSpecsMap = new HashMap<>();
    specs.fields().forEachRemaining(nodeEntry -> {
      String specsKey = nodeEntry.getKey();
      webCoffeeSpecsMap.put(specsKey, this.getSpecContent(nodeEntry.getValue(), webCoffee));
    });
    return webCoffeeSpecsMap;
  }

  private WebCoffeeSpecs getSpecContent(JsonNode value, WebCoffee webCoffee) {
    WebCoffeeSpecs webCoffeeSpecs = new WebCoffeeSpecs();
    ObjectNode pathNode = NodeHelper.getObjectNode(value, "pathItem", true);
    WebCoffeeSpecs.PathItem pathItem = new PathItem();
    pathItem.set$ref(getNodeString(pathNode, "$ref", true));
    webCoffeeSpecs.setPathItem(pathItem);
    webCoffeeSpecs
        .setParameters(getParameters(NodeHelper.getArrayNode(value, "parameters", false)));
    ReferenceHandler referenceHandler = ReferenceHandler
        .getReference(webCoffeeSpecs.getPathItem().get$ref());
    webCoffeeSpecs.setOperation(getPathOperation(referenceHandler, webCoffee));
    webCoffeeSpecs.setHttpMethod(referenceHandler.getHttpMethod());
    webCoffeeSpecs.setRequestBody(
        getRequestBody(NodeHelper.getObjectNode(value, "requestBody", true), webCoffee));
    return webCoffeeSpecs;
  }

  private WebCoffeeSpecsRequestBody getRequestBody(ObjectNode content, WebCoffee webCoffee) {
    WebCoffeeSpecsRequestBody webCoffeeRequestBody = new WebCoffeeSpecsRequestBody();
    ObjectNode objectNode = NodeHelper.getObjectNode(content, "content", true);
    objectNode.fields().forEachRemaining(nodeEntry -> {
      final String contentKey = nodeEntry.getKey();
      Map<String, WebCoffeeSpecsRequestBodyContentRequest> requestBodyContent = new HashMap<>();
      WebCoffeeSpecsRequestBodyContentRequest webCoffeeSpecsRequestBodyContentRequest = new WebCoffeeSpecsRequestBodyContentRequest();
      ReferenceHandler referenceHandler = ReferenceHandler
          .getReference(NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "$ref", true));
      webCoffeeSpecsRequestBodyContentRequest
          .set$ref(referenceHandler.getReference());
      webCoffeeSpecsRequestBodyContentRequest
          .setSchema(getSchema(referenceHandler, webCoffee));
      webCoffeeSpecsRequestBodyContentRequest.setPayload(
          SchemaParser.createSchemaFromNode(
              NodeHelper.getObjectNode(nodeEntry.getValue(), "payload", true)));
      requestBodyContent.put(contentKey, webCoffeeSpecsRequestBodyContentRequest);
      webCoffeeRequestBody.setContent(requestBodyContent);
    });
    return webCoffeeRequestBody;
  }


  private Schema getSchema(final ReferenceHandler referenceHandler,
      WebCoffee webCoffee) {

    WebCoffeeResources webCoffeeResource = webCoffee.getResources()
        .get(referenceHandler.getResourceKey());
    if (webCoffeeResource == null) {
      throw new WebCoffeeValidationExcepton(
          "invalid reference" + referenceHandler.getReference());
    }
    return webCoffeeResource.getOpenAPI().getComponents().getSchemas()
        .get(referenceHandler.getSchemasName());
  }

  private Set<Parameter> getParameters(ArrayNode parameters) {
    Set<Parameter> webCoffeeSpecsParametersSet = new HashSet<>();
    if (parameters == null) {
      return webCoffeeSpecsParametersSet;
    }
    parameters.forEach(jsonNode -> {
      webCoffeeSpecsParametersSet.add(getParameter(jsonNode));
    });
    return webCoffeeSpecsParametersSet;
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
    parameter.setName(getNodeString((ObjectNode) item, "name", false));
    parameter.setIn(value);
    parameter.setValue(getNodeString((ObjectNode) item, "value", false));
    parameter.setArgument(getNodeString((ObjectNode) item, "argument", false));
    return parameter;
  }

  //get path endpoint
  private Operation getPathOperation(ReferenceHandler referenceHandler, WebCoffee webCoffee) {
    WebCoffeeResources webCoffeeResource = webCoffee.getResources()
        .get(referenceHandler.getResourceKey());
    if (webCoffeeResource == null) {
      throw new WebCoffeeException("invalid reference" + referenceHandler.getReference());
    }
    io.swagger.v3.oas.models.PathItem pathItem = webCoffeeResource.getOpenAPI().getPaths()
        .get(referenceHandler.getPath());
    if (pathItem == null) {
      throw new WebCoffeeValidationExcepton("path operation is null");
    }
    String httpMethod = referenceHandler.getHttpMethod().toLowerCase();
    try {
      Method method = pathItem.getClass().getMethod(
          "get" + httpMethod.substring(0, 1).toUpperCase() + httpMethod.substring(1));
      Operation operation = (Operation) method.invoke(pathItem);

      if (operation == null) {
        throw new WebCoffeeException(
            "path operation is not defined " + referenceHandler.getReference());
      }
      return operation;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      throw new WebCoffeeValidationExcepton(e.getMessage());
    }

  }
}
