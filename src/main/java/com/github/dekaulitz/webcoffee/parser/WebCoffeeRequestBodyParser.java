package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.extractString;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeRequestBodyContent;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeRequestBodyParser {

  public Map<String, WebCoffeeRequestBodyContent> getWebCoffeeRequestBody(ObjectNode content,
      Map<String, WebCoffeeResources> resources,
      WebCoffeeParser webCoffeeParser) {
    Map<String, WebCoffeeRequestBodyContent> webCoffeeRequestBodyContentMap = new HashMap<>();
    content.fields().forEachRemaining(stringJsonNodeEntry -> {
      final String key = stringJsonNodeEntry.getKey();
      final JsonNode jsonNode = stringJsonNodeEntry.getValue();
      WebCoffeeRequestBodyContent webCoffeeRequestBodyContent = new WebCoffeeRequestBodyContent();
      try {
        webCoffeeRequestBodyContent.set$ref(getNodeString("$ref", true, jsonNode));
        webCoffeeRequestBodyContent
            .setSchema(getSchemaRequestBody(webCoffeeRequestBodyContent.get$ref(), resources));
        webCoffeeRequestBodyContent
            .setPayload(
                SchemaParser.createSchemaFromNode(getNodeObject("payload", true, jsonNode)));
        webCoffeeRequestBodyContentMap.put(key, webCoffeeRequestBodyContent);
      } catch (WebCoffeeException e) {
        e.printStackTrace();
        log.error(e);
        webCoffeeParser.setMessage(e.getMessage());
        System.exit(9);
      }
    });
    return webCoffeeRequestBodyContentMap;
  }

  private Schema<?> getSchemaRequestBody(String $ref, Map<String, WebCoffeeResources> resources)
      throws WebCoffeeException {
    String[] ref = extractString($ref);
    if (ref.length != 4) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    WebCoffeeResources webCoffeeResource = resources.get(ref[0]);
    if (webCoffeeResource == null) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    return webCoffeeResource.getOpenAPI().getComponents().getSchemas().get(ref[3]);
  }
}
