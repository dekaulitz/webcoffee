package com.github.dekaulitz.webcoffee.parser;

import com.github.dekaulitz.webcoffee.modules.model.WebCoffee;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;

public class OpenApiParserComponent {

  private static OpenApiParserComponent instance = null;
  private final WebCoffee openApi;

  public OpenApiParserComponent(WebCoffee openApi) {
    this.openApi = openApi;
  }

  public static OpenApiParserComponent getInstance(WebCoffee webCoffee) {
    if (instance == null) {
      instance = new OpenApiParserComponent(webCoffee);
    }
    return instance;
  }

  /**
   * get component name from reff schema
   *
   * @param ref reff schema
   * @return String
   */
  public static String getSimpleRef(String ref) {
    if (ref.startsWith("#/components/")) {
      ref = ref.substring(ref.lastIndexOf("/") + 1);
    }
    return ref;
  }

  /**
   * get schema object base on schema name
   *
   * @param name    schema name
   * @param openAPI openapi object
   * @return Schema
   */
  public static Schema getSchemaFromName(String name, OpenAPI openAPI) {
    if (openAPI.getComponents() == null) {
      return null;
    }
    final Map<String, Schema> mapSchema = openAPI.getComponents().getSchemas();
    if (mapSchema == null || mapSchema.isEmpty()) {
      return null;
    }
    return mapSchema.get(name);
  }
}
