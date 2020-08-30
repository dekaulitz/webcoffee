package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.extractString;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeInteger;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeExpect;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeGiven;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeRequestBody;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSchemaValidation;
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
        webCoffeeSpecs
            .setExpect(getWebCoffeeSpecExpect(resources, getNodeObject("expect", true, node)));
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
    webCoffeeExpect.setParameters(JsonMapper.mapper()
        .convertValue(getNodeObject("parameters", true, expect),
            new TypeReference<Map<String, Object>>() {
            }));
    webCoffeeExpect
        .setContent(new WebCoffeeSchemaValidationParser()
            .getWebCoffeeExpectContent(resources, getNodeObject("content", true, expect)));
    return webCoffeeExpect;
  }

  private Map<String, WebCoffeeSchemaValidation> getWebCoffeeExpectContent(
      Map<String, WebCoffeeResources> resources, ObjectNode content) {
    Map<String, WebCoffeeSchemaValidation> webCoffeeResourcesMap = new HashMap<>();
    content.fields().forEachRemaining(stringJsonNodeEntry -> {
      final String key = stringJsonNodeEntry.getKey();
      WebCoffeeSchemaValidation webCoffeeSchemaValidation = new WebCoffeeSchemaValidation();
      try {
        webCoffeeSchemaValidation.set$ref(getNodeString("$ref", true, content));
        webCoffeeResourcesMap.put(key, webCoffeeSchemaValidation);
      } catch (WebCoffeeException e) {
        e.printStackTrace();
        log.error(e);
      }
    });
    return webCoffeeResourcesMap;
  }


  private WebCoffeeGiven getWebCoffeeSpecGive(
      Map<String, WebCoffeeResources> resources,
      JsonNode given, WebCoffeeParser webCoffeeParser)
      throws WebCoffeeException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    WebCoffeeGiven webCoffeeGiven = new WebCoffeeGiven();
    webCoffeeGiven.set$ref(getNodeString("$ref", true, given));
    webCoffeeGiven.setOperation(getPathOperation(webCoffeeGiven.get$ref(), resources));
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
    webCoffeeStatements.setParameters(JsonMapper.mapper()
        .convertValue(getNodeObject("parameters", true, statements),
            new TypeReference<Map<String, Object>>() {
            }));
    webCoffeeStatements
        .setRequestBody(
            getRequestBody(getNodeObject("requestBody", true, statements), resources,
                webCoffeeParser));
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
      Map<String, WebCoffeeResources> resources)
      throws WebCoffeeException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    String[] ref = extractString($ref);
    if (ref.length != 4) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    WebCoffeeResources webCoffeeResource = resources.get(ref[0]);
    if (webCoffeeResource == null) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    PathItem pathItem = webCoffeeResource.getOpenAPI().getPaths().get(ref[2]);
    if (pathItem == null) {
      throw new WebCoffeeException("path operation is null");
    }
    String httpMethod = ref[3].toLowerCase();
    Method method = pathItem.getClass().getMethod(
        "get" + httpMethod.substring(0, 1).toUpperCase() + httpMethod.substring(1));
    Operation operation = (Operation) method.invoke(pathItem);
    if (operation == null) {
      throw new WebCoffeeException("path operation is not defined " + $ref);
    }
    return operation;
  }
}
