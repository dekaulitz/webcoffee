package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.extractString;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSchemaValidation;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeSchemaValidationParser {

  public Map<String, WebCoffeeSchemaValidation> getWebCoffeeExpectContent(
      Map<String, WebCoffeeResources> resources, ObjectNode content) {
    Map<String, WebCoffeeSchemaValidation> stringWebCoffeeSchemaValidationMap = new HashMap<>();
    content.fields().forEachRemaining(stringJsonNodeEntry -> {
      try {
        final String key = stringJsonNodeEntry.getKey();
        final ObjectNode objectNode = getNodeObject("schemaValidation", true,
            stringJsonNodeEntry.getValue());
        WebCoffeeSchemaValidation webCoffeeSchemaValidation = new WebCoffeeSchemaValidation();
        webCoffeeSchemaValidation
            .set$ref(getNodeString("$ref", true, objectNode));
        webCoffeeSchemaValidation.setSchema(
            getSchemaValidation(webCoffeeSchemaValidation.get$ref(), resources));
        webCoffeeSchemaValidation
            .setExpectValue(SchemaParser.createSchemaFromNode(getNodeObject("expectValue", true, objectNode)));
        stringWebCoffeeSchemaValidationMap.put(key, webCoffeeSchemaValidation);
      } catch (WebCoffeeException e) {
        e.printStackTrace();
        log.error(e);
      }
    });
    return stringWebCoffeeSchemaValidationMap;
  }

  private Schema getSchemaValidation(String $ref,
      Map<String, WebCoffeeResources> resources)
      throws WebCoffeeException {
    String[] ref = extractString($ref);
    if (ref.length != 4) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    WebCoffeeResources webCoffeeResource = resources.get(ref[0]);
    if (webCoffeeResource == null) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    Schema schema = webCoffeeResource.getOpenAPI().getComponents().getSchemas().get(ref[3]);
    if (schema == null) {
      throw new WebCoffeeException("invalid schema reference" + $ref);
    }
    return schema;
  }
}
