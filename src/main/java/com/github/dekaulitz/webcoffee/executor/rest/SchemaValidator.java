package com.github.dekaulitz.webcoffee.executor.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import java.io.IOException;

public class SchemaValidator {

  private JsonNode rawSchema;


  public static boolean validateSchema(String rawSchema, String s) throws WebCoffeeException {
    return new JsonSchemaValidatorFactory<JsonNode>() {
      @Override
      JsonNode createSchemaInstance(String input) throws IOException {
        return null;
      }

      @Override
      boolean validate(String rawSchema) {
        return false;
      }
    }.validate(rawSchema);
  }

  private static abstract class JsonSchemaValidatorFactory<T> {


    abstract T createSchemaInstance(String input) throws IOException;

    abstract boolean validate(String rawSchema);
  }

}
