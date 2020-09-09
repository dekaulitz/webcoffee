package com.github.dekaulitz.webcoffee.executor.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import java.io.IOException;

public class SchemaValidator {

  private JsonNode rawSchema;


  public static boolean validateSchema(String rawSchema, String s) throws WebCoffeeException {
    return new JsonSchemaValidatorFactory() {
      @Override
      JsonNode createSchemaInstance(String input) throws IOException {
        return null;
      }

      @Override
      public boolean validate(String rawSchema) {
        return true;
      }
    }.validate(rawSchema);
  }

  private static abstract class JsonSchemaValidatorFactory {


    abstract JsonNode createSchemaInstance(String input) throws IOException;

    public abstract boolean validate(String rawSchema);
  }
}
