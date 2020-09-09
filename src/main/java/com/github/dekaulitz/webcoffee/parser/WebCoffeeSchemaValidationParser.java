package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.helper.WebCoffeeReference;
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
        ObjectNode expectValue = getNodeObject("expectValue", false, objectNode);
        if (!expectValue.isEmpty()) {
          webCoffeeSchemaValidation
              .setExpectValue(
                  SchemaParser.createSchemaFromNode(expectValue));
        }
        webCoffeeSchemaValidation
            .setRawSchema(getRawSchema(webCoffeeSchemaValidation.get$ref(), resources));

        stringWebCoffeeSchemaValidationMap.put(key, webCoffeeSchemaValidation);
      } catch (WebCoffeeException | JsonProcessingException e) {
        e.printStackTrace();
        log.error(e);
      }
    });
    return stringWebCoffeeSchemaValidationMap;
  }

  private Schema getSchemaValidation(String $ref,
      Map<String, WebCoffeeResources> resources)
      throws WebCoffeeException {
    WebCoffeeReference webCoffeeReference = WebCoffeeReference.getReference($ref);
    WebCoffeeResources webCoffeeResource = resources.get(webCoffeeReference.getReferenceKey());
    if (webCoffeeResource == null) {
      throw new WebCoffeeException("invalid reference" + $ref);
    }
    Schema schema = webCoffeeResource.getOpenAPI().getComponents().getSchemas()
        .get(webCoffeeReference.getSchema());
    if (schema == null) {
      throw new WebCoffeeException("invalid schema reference" + $ref);
    }
    return schema;
  }

  private String getRawSchema(String $ref,
      Map<String, WebCoffeeResources> resources)
      throws WebCoffeeException, JsonProcessingException {
    WebCoffeeReference webCoffeeReference = WebCoffeeReference.getReference($ref);
    WebCoffeeResources webCoffeeResource = resources.get(webCoffeeReference.getReferenceKey());
    JsonNode jsonNode = webCoffeeResource.getOpenAPINode()
        .get(webCoffeeReference.getReferenceType()).get(webCoffeeReference.getReference())
        .get(webCoffeeReference.getSchema());
    checkNodeReference(webCoffeeResource.getOpenAPINode().get(webCoffeeReference.getReferenceType())
        .get(webCoffeeReference.getReference()), jsonNode);
    return JsonMapper.mapper().writeValueAsString(jsonNode);
  }

  private void checkNodeReference(final JsonNode schemaNode,
      JsonNode jsonNode) {
    JsonNode properties = jsonNode.get("properties");
    if (properties != null) {
      properties.forEach(jsonNode1 -> {
        jsonNode.get("properties").forEach(jsonNode2 -> {
          JsonNode items = jsonNode2.get("items");
          if (items != null) {
            checkReffNode(schemaNode, items);
          }
          checkReffNode(schemaNode, jsonNode2);
        });
      });
    }
  }

  private void checkReffNode(JsonNode schemaNode, JsonNode jsonNode2) {
    JsonNode $ref = jsonNode2.get("$ref");
    if ($ref != null) {
      String schemaName = OpenAPITools.getSimpleRef($ref.asText());
      ObjectNode schemaObjectNode = (ObjectNode) schemaNode.get(schemaName);
      ((ObjectNode) jsonNode2).setAll(schemaObjectNode);
      ((ObjectNode) jsonNode2).remove("$ref");
      this.checkNodeReference(schemaNode,jsonNode2);
    }
  }
}
