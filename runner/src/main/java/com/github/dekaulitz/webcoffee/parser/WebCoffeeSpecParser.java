package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.NodeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.helper.ReferenceHandler;
import com.github.dekaulitz.webcoffee.model.WebCoffee;
import com.github.dekaulitz.webcoffee.model.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.model.spec.WebCoffeeSpecs;
import com.github.dekaulitz.webcoffee.model.spec.WebCoffeeSpecs.PathItem;
import com.github.dekaulitz.webcoffee.model.spec.WebCoffeeSpecsRequestBodyContent;
import com.github.dekaulitz.webcoffee.openapi.parser.WebCoffeeParameterParser;
import com.github.dekaulitz.webcoffee.openapi.parser.WebCoffeeSchemaParser;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class WebCoffeeSpecParser {


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
    ReferenceHandler referenceHandler = ReferenceHandler
        .getReference(webCoffeeSpecs.getPathItem().get$ref());
    webCoffeeSpecs.setEndpoint(referenceHandler.getPath());
    webCoffeeSpecs
        .setParameters(WebCoffeeParameterParser
            .getParameters(NodeHelper.getArrayNode(value, "parameters", false)));
    webCoffeeSpecs.setOperation(getPathOperation(referenceHandler, webCoffee));
    webCoffeeSpecs.setHttpMethod(referenceHandler.getHttpMethod());
    webCoffeeSpecs.setRequestBody(
        getRequestBody(NodeHelper.getObjectNode(value, "requestBody", false), webCoffee));
    return webCoffeeSpecs;
  }

  private WebCoffeeSpecsRequestBodyContent getRequestBody(ObjectNode content, WebCoffee webCoffee) {
    if (content == null) {
      return null;
    }
    WebCoffeeSpecsRequestBodyContent webCoffeeSpecsRequestBodyContent = new WebCoffeeSpecsRequestBodyContent();
    ReferenceHandler referenceHandler = ReferenceHandler
        .getReference(NodeHelper.getNodeString(content, "$ref", true));
    webCoffeeSpecsRequestBodyContent.set$ref(referenceHandler.getReference());
    webCoffeeSpecsRequestBodyContent
        .setMediaType(NodeHelper.getNodeString(content, "mediaType", true));
    webCoffeeSpecsRequestBodyContent.setSchema(getSchema(referenceHandler, webCoffee));
    webCoffeeSpecsRequestBodyContent.setPayload(
        WebCoffeeSchemaParser.createSchemaFromNode(
            NodeHelper.getObjectNode(content, "payload", true)));
    return webCoffeeSpecsRequestBodyContent;
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
      throw new WebCoffeeValidationExcepton(
          referenceHandler.getPath() + " its not found on " + referenceHandler.getResourceKey());
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
