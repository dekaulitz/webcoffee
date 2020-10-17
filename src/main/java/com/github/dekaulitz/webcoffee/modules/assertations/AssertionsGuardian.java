package com.github.dekaulitz.webcoffee.modules.assertations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeThenRequest.Expect;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.ArraySchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.BooleanSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.IntegerSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.StringSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.WebCoffeeSchema;
import io.restassured.response.Response;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

@Slf4j
public class AssertionsGuardian {

  private final Expect expect;
  private final Response response;
  private final String endpoint;

  public AssertionsGuardian(String endpoint, Expect expect, Response response) {
    this.expect = expect;
    this.response = response;
    this.endpoint = endpoint;
  }


  public void validate() {
    assertEquals(response.getStatusCode(), expect.getHttpStatus(),
        "expect httpStatusCode " + expect.getHttpStatus());
    this.validateHeaders();
    if (expect.getResponse() != null) {
      JsonNode responseNode = response.getBody().as(JsonNode.class);
      if (expect.getResponse().getSchema() != null) {
        if (!expect.getResponse().getSchema().getRequired().isEmpty()) {
          expect.getResponse().getSchema().getRequired().forEach(field -> {
            Assertions
                .assertNotNull(responseNode.get(field),
                    this.endpoint + " expecting " + field + " is required");
          });
        }
        validateResponseType(responseNode, expect.getResponse().getSchema());
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
      validateResponseProperties(s, webCoffeeSchema, expect, true);
    });
  }

  private void validateResponseType(JsonNode node, Schema expectSchema) {
    if (expectSchema instanceof StringSchema) {
      assertTrue(node.isTextual(), this.endpoint + " expecting "+node+" response is string");
    } else if (expectSchema instanceof IntegerSchema) {
      assertTrue(node.isIntegralNumber(), this.endpoint + " expecting "+node+" response is integer");
    } else if (expectSchema instanceof BooleanSchema) {
      assertTrue(node.asBoolean(), this.endpoint + " expecting response "+node+" is boolean");
    } else if (expectSchema instanceof ArraySchema) {
      assertTrue(node.isArray(), this.endpoint + " expecting response "+node+" is array");
      //taking sample from the first response assuming the rest of structure remain same
      validateResponseType(node.get(0), ((ArraySchema) expectSchema).getItems());
    } else {
      expectSchema.getProperties().forEach((s, schema) -> {
        assertNotNull(node.get(String.valueOf(s)),
            this.endpoint + " expecting " + s + " is not null");
        //taking the first array assuming the rest of structure remain same
        validateResponseProperties(String.valueOf(s), (Schema) schema, node, false);
      });
    }
  }

  private void validateResponseProperties(String key, Schema schema, JsonNode node,
      boolean isExpectedValueResponse) {
    if (schema instanceof StringSchema) {
      if (isExpectedValueResponse) {
        assertTrue(node.isTextual(), this.endpoint + " expecting " + key + " is string");
        if (((StringSchema) schema).getValue() != null) {
          assertEquals(((StringSchema) schema).getValue(), node.asText(),
              this.endpoint + "expecting " + key + " is " + ((StringSchema) schema).getValue());
        }
      } else {
        assertTrue(node.get(key).isTextual(), this.endpoint + " expecting " + key + " is string");
        if (((StringSchema) schema).getValue() != null) {
          assertEquals(node.get(key).asText(), ((StringSchema) schema).getValue(),
              this.endpoint + " expecting " + key + " is " + ((StringSchema) schema).getValue());
        }
      }
    } else if (schema instanceof IntegerSchema) {
      if (isExpectedValueResponse) {
        assertTrue(node.isIntegralNumber(), "expecting " + key + " is integer");
        if (((IntegerSchema) schema).getValue() != null) {
          assertEquals(((IntegerSchema) schema).getValue(), node.asInt(),
              this.endpoint + " expecting " + key + " is " + ((IntegerSchema) schema).getValue());
        }
      } else {
        assertTrue(node.get(key).isIntegralNumber(), "expecting " + key + " is integer");
        if (((IntegerSchema) schema).getValue() != null) {
          assertEquals(((IntegerSchema) schema).getValue(), node.get(key).asInt(),
              this.endpoint + " expecting " + key + " is " + ((IntegerSchema) schema).getValue());
        }
      }
    } else if (schema instanceof BooleanSchema) {
      if (isExpectedValueResponse) {
        assertTrue(node.asBoolean(), "expecting " + key + " is boolean");
        if (((BooleanSchema) schema).getValue() != null) {
          assertEquals(((BooleanSchema) schema).getValue(), node.asBoolean(),
              this.endpoint + " expecting " + key + " is " + ((BooleanSchema) schema).getValue());
        }
      } else {
        assertTrue(node.get(key).asBoolean(), "expecting " + key + " is boolean");
        if (((BooleanSchema) schema).getValue() != null) {
          assertEquals(((BooleanSchema) schema).getValue(), node.get(key).asBoolean(),
              this.endpoint + " expecting " + key + " is " + ((BooleanSchema) schema).getValue());
        }
      }
    } else if (schema instanceof ArraySchema) {
      assertTrue(node.get(key).isArray(), this.endpoint + " expecting " + key + " is array");
      if (node.get(key).has(0)) {
        validateResponseType(node.get(key).get(0), ((ArraySchema) schema).getItems());
      }
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
