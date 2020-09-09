package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getArrayObjectNode;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeInteger;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.WebCoffeeReference;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeExpect;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeGiven;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeRequestBody;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeStatements;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeSpecParser {

  public static final String SPECS = "specs";

  public Map<String, WebCoffeeSpecs> getWebCoffeeSpecs(
      Map<String, WebCoffeeResources> resources,
      JsonNode nodeObject,
      WebCoffeeParser webCoffeeParser) {
    Map<String, WebCoffeeSpecs> webCoffeeSpecsMap = new HashMap<>();
    nodeObject.fields().forEachRemaining(stringJsonNodeEntry -> {
      WebCoffeeSpecs webCoffeeSpecs = new WebCoffeeSpecs();
      String key = stringJsonNodeEntry.getKey();
      JsonNode node = stringJsonNodeEntry.getValue();
      try {
        webCoffeeSpecs
            .setGiven(getWebCoffeeSpecGive(resources, getNodeObject("given", true, node),
                webCoffeeParser));
        ObjectNode expect = getNodeObject("expect", false, node);
        if (!expect.isEmpty()) {
          webCoffeeSpecs.setExpect(getWebCoffeeSpecExpect(resources, expect));
        }
        webCoffeeSpecsMap.put(key, webCoffeeSpecs);
      } catch (WebCoffeeException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
        log.error(e);
        webCoffeeParser.setMessage(e.getMessage());
      }
    });
    return webCoffeeSpecsMap;
  }

  private WebCoffeeExpect getWebCoffeeSpecExpect(Map<String, WebCoffeeResources> resources,
      ObjectNode expect) throws WebCoffeeException {
    WebCoffeeExpect webCoffeeExpect = new WebCoffeeExpect();
    webCoffeeExpect.setHttpCode(getNodeInteger("httpCode", false, expect));
//    webCoffeeExpect.setParameters(JsonMapper.mapper()
//        .convertValue(getNodeObject("parameters", true, expect),
//            new TypeReference<Map<String, Object>>() {
//            }));
    webCoffeeExpect.setParameters(new WebCoffeeParameterParser()
        .getParameters(getArrayObjectNode("parameters", false, expect), resources));
    webCoffeeExpect
        .setContent(new WebCoffeeSchemaValidationParser()
            .getWebCoffeeExpectContent(resources, getNodeObject("content", true, expect)));
    return webCoffeeExpect;
  }

  private WebCoffeeGiven getWebCoffeeSpecGive(
      Map<String, WebCoffeeResources> resources,
      JsonNode given, WebCoffeeParser webCoffeeParser)
      throws WebCoffeeException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    WebCoffeeGiven webCoffeeGiven = new WebCoffeeGiven();
    webCoffeeGiven.set$ref(getNodeString("$ref", true, given));
    WebCoffeeReference webCoffeeReference = WebCoffeeReference
        .getReference(webCoffeeGiven.get$ref());
    webCoffeeGiven
        .setOperation(getPathOperation(webCoffeeGiven.get$ref(), webCoffeeReference, resources));
    webCoffeeGiven.setHttpMethod(webCoffeeReference.getHttpMethod());
    webCoffeeGiven.setPathEndpoint(webCoffeeReference.getReference());
    webCoffeeGiven.setStatements(
        getWebCoffeeStatements(getNodeObject("statements", true, given), resources,
            webCoffeeParser));
    return webCoffeeGiven;
  }

  private WebCoffeeStatements getWebCoffeeStatements(ObjectNode statements,
      Map<String, WebCoffeeResources> resources,
      WebCoffeeParser webCoffeeParser)
      throws WebCoffeeException {
    WebCoffeeStatements webCoffeeStatements = new WebCoffeeStatements();
//    webCoffeeStatements.setParameters(JsonMapper.mapper()
//        .convertValue(getNodeObject("parameters", true, statements),
//            new TypeReference<List<Object>>() {
//            }));
    webCoffeeStatements
        .setParameters(new WebCoffeeParameterParser()
            .getParameters(getArrayObjectNode("parameters", false, statements), resources));
    ObjectNode requestBody = getNodeObject("requestBody", true, statements);
    if (!requestBody.isEmpty()) {
      webCoffeeStatements.setRequestBody(getRequestBody(requestBody, resources,
          webCoffeeParser));
    }
    return webCoffeeStatements;
  }

  private WebCoffeeRequestBody getRequestBody(ObjectNode requestBody,
      Map<String, WebCoffeeResources> resources,
      WebCoffeeParser webCoffeeParser) throws WebCoffeeException {
    WebCoffeeRequestBody webCoffeeRequestBody = new WebCoffeeRequestBody();
    webCoffeeRequestBody.setContent(new WebCoffeeRequestBodyParser()
        .getWebCoffeeRequestBody(getNodeObject("content", true, requestBody), resources,
            webCoffeeParser));
    return webCoffeeRequestBody;
  }

  private Operation getPathOperation(String $ref,
      WebCoffeeReference webCoffeeReference,
      Map<String, WebCoffeeResources> resources)
      throws WebCoffeeException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    WebCoffeeResources webCoffeeResource = resources.get(webCoffeeReference.getReferenceKey());
    if (webCoffeeResource == null) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    PathItem pathItem = webCoffeeResource.getOpenAPI().getPaths()
        .get(webCoffeeReference.getReference());
    if (pathItem == null) {
      throw new WebCoffeeException("path operation is null");
    }
    String httpMethod = webCoffeeReference.getHttpMethod().toLowerCase();
    Method method = pathItem.getClass().getMethod(
        "get" + httpMethod.substring(0, 1).toUpperCase() + httpMethod.substring(1));
    Operation operation = (Operation) method.invoke(pathItem);
    if (operation == null) {
      throw new WebCoffeeException("path operation is not defined " + $ref);
    }
    return operation;
  }
}
