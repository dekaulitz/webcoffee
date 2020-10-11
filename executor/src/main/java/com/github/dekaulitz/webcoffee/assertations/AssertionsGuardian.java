package com.github.dekaulitz.webcoffee.assertations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeThenRequest.Expect;
import com.github.dekaulitz.webcoffee.openapi.schemas.ArraySchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.BooleanSchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.IntegerSchema;
import com.github.dekaulitz.webcoffee.openapi.schemas.StringSchema;
import io.restassured.response.Response;
import io.swagger.v3.oas.models.media.Schema;
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
        expect.getResponse().getProperties().forEach((s, schema) -> {
          validateResponse(s, schema, responseNode);
        });
      }
    }
  }


  private void validateResponse(String key, Schema schema, JsonNode node) {
    if (!(schema instanceof ArraySchema)) {
      if (!expect.getResponse().getRequired().isEmpty()) {
        expect.getResponse().getRequired().forEach(field -> {
          Assertions
              .assertNotNull(node.get(field), "expecting " + field + " is required");
        });
      }
      if (schema instanceof StringSchema) {
        if (((StringSchema) schema).getValue() != null) {
          assertEquals(node.get(key).asText(), ((StringSchema) schema).getValue());
        }
      } else if (schema instanceof IntegerSchema) {
        if (((IntegerSchema) schema).getValue() != null) {
          assertEquals(((IntegerSchema) schema).getValue(), node.get(key).asInt(),
              "expecting " + key + " is " + ((IntegerSchema) schema).getValue());
        }
      } else if (schema instanceof BooleanSchema) {
        if (((BooleanSchema) schema).getValue() != null) {
          assertEquals(((BooleanSchema) schema).getValue(), node.get(key).asBoolean(),
              "expecting " + key + " is " + ((BooleanSchema) schema).getValue());
        }
      } else if (schema instanceof ArraySchema) {
        //@TODO please do handling for array schema on next level array
      }

    } else {
//      assertEquals(true, node.isArray(), "expecting " + key + "is array");
      // @TODO pelase do handling for array schema on first level
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
