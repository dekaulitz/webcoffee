package com.github.dekaulitz.webcoffee.assertations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeThenRequest.Expect;
import com.github.dekaulitz.webcoffee.openapi.schemas.ArraySchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.BooleanSchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.IntegerSchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.StringSchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.WebCoffeeSchema;
import io.restassured.response.Response;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

@Log4j2
public class AssertionsGuardian {

  private final Expect expect;
  private final Response response;

  public AssertionsGuardian(Expect expect, Response response) {
    this.expect = expect;
    this.response = response;
  }


  public void validate() {
    assertEquals(response.getStatusCode(), expect.getHttpStatus(),
        "expect httpStatusCode " + expect.getHttpStatus());
    this.validateHeaders();
    if (expect.getResponse() != null) {
      JsonNode responseNode = response.getBody().as(JsonNode.class);

      if (!responseNode.isArray()) {
        if (expect.getResponse().getSchema() != null) {
          if (!expect.getResponse().getSchema().getRequired().isEmpty()) {
            expect.getResponse().getSchema().getRequired().forEach(field -> {
              Assertions
                  .assertNotNull(responseNode.get(field), "expecting " + field + " is required");
            });
          }
          expect.getResponse().getSchema().getProperties().forEach((s, schema) -> {
            assertNotNull(responseNode.get(s), "expecting " + s + " is not null");
            if (!(schema instanceof ArraySchema)) {
              validateResponse(s, schema, responseNode, false);
            }
          });
        }
      } else {
        //@TODO handling array
      }
      if (expect.getResponse().getExpectedValue() != null) {
        expectedResponseValueValidation(responseNode, expect.getResponse().getExpectedValue());
      }
    }
  }

  private void expectedResponseValueValidation(JsonNode responseNode,
      Map<String, WebCoffeeSchema<?>> expectedValue) {
    expectedValue.forEach((s, webCoffeeSchema) -> {
      String[] fieldArray = s.split("\\.");
      int n = 0;
      JsonNode expect = responseNode;
      for (String key : fieldArray) {
        if (n < fieldArray.length) {
          if (StringUtils.isNumeric(key)) {
            expect = expect.get(Integer.parseInt(key));
          } else if (!key.equalsIgnoreCase("$")) {
            expect = expect.get(key);
          }
          n++;
        }
      }
      validateResponse(s, webCoffeeSchema, expect, true);
    });
  }

  private void validateResponse(String key, Schema schema, JsonNode node,
      boolean isExpectedValueResponse) {
    if (schema instanceof StringSchema) {
      if (isExpectedValueResponse) {
        assertTrue(node.isTextual(), "expecting " + key + " is string");
        if (((StringSchema) schema).getValue() != null) {
          assertEquals(node.asText(), ((StringSchema) schema).getValue(),
              "expecting " + key + " is" + ((StringSchema) schema).getValue());
        }
      } else {
        assertTrue(node.get(key).isTextual(), "expecting " + key + " is string");
        if (((StringSchema) schema).getValue() != null) {
          assertEquals(node.get(key).asText(), ((StringSchema) schema).getValue(),
              "expecting " + key + " is" + ((StringSchema) schema).getValue());
        }
      }
    } else if (schema instanceof IntegerSchema) {

      if (isExpectedValueResponse) {
        assertTrue(node.isIntegralNumber(), "expecting " + key + " is integer");
        if (((IntegerSchema) schema).getValue() != null) {
          assertEquals(((IntegerSchema) schema).getValue(), node.asInt(),
              "expecting " + key + " is " + ((IntegerSchema) schema).getValue());
        }
      } else {
        assertTrue(node.get(key).isIntegralNumber(), "expecting " + key + " is integer");
        if (((IntegerSchema) schema).getValue() != null) {
          assertEquals(((IntegerSchema) schema).getValue(), node.get(key).asInt(),
              "expecting " + key + " is " + ((IntegerSchema) schema).getValue());
        }
      }
    } else if (schema instanceof BooleanSchema) {
      if (isExpectedValueResponse) {
        assertTrue(node.asBoolean(), "expecting " + key + " is boolean");
        if (((BooleanSchema) schema).getValue() != null) {
          assertEquals(((BooleanSchema) schema).getValue(), node.asBoolean(),
              "expecting " + key + " is " + ((BooleanSchema) schema).getValue());
        }
      } else {
        assertTrue(node.get(key).asBoolean(), "expecting " + key + " is boolean");
        if (((BooleanSchema) schema).getValue() != null) {
          assertEquals(((BooleanSchema) schema).getValue(), node.get(key).asBoolean(),
              "expecting " + key + " is " + ((BooleanSchema) schema).getValue());
        }
      }
    } else if (schema instanceof ArraySchema) {
      assertTrue(node.get(key).isArray(), "expecting " + key + " is array");
      validateResponse(key, ((ArraySchema) schema).getItems(), node.get(key), false);
    }
  }

  private void validateHeaders() {
    if (!expect.getParameters().isEmpty()) {
      expect.getParameters().forEach(parameter -> {
        if (parameter.getIn().equals("header")) {
          if (parameter.getRequired()) {
            assertNotNull(response.getHeader(parameter.getName()),
                "expecting " + parameter.getName() + " is not empty");
          }
          if (StringUtils.isNoneEmpty(parameter.getValue())) {
            assertEquals(response.getHeader(parameter.getName()), parameter.getValue(),
                "expecting " + parameter.getName() + " is not empty");
          }
        }
      });
    }
  }
}
