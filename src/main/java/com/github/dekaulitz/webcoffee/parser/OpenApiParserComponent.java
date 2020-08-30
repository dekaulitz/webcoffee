package com.github.dekaulitz.webcoffee.parser;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

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

  /**
   * get schma base on reff schema
   *
   * @param refSchema reffschema link
   * @param openAPI   openapi object
   * @return Schema
   */
  public static Schema getSchemaFromRefSchema(Schema refSchema, OpenAPI openAPI) {
    if (StringUtils.isBlank(refSchema.get$ref())) {
      return null;
    }
    final String name = getSimpleRef(refSchema.get$ref());
    return getSchemaFromName(name, openAPI);
  }

//  public Operation loadPathOperation(String $ref)
//      throws WebCoffeeException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//    String[] ref = extractString($ref);
//    WebCoffeeResources resources = validateOpenAPiSpec($ref, ref);
//    PathItem pathItem = resources.getOpenAPI().getPaths().get(ref[2]);
//    if (pathItem == null) {
//      throw new WebCoffeeException("path operation is null");
//    }
//    String httpMethod = ref[3].toLowerCase();
//    Method method = pathItem.getClass().getMethod(
//        "get" + httpMethod.substring(0, 1).toUpperCase() + httpMethod.substring(1));
//    Operation operation = (Operation) method.invoke(pathItem);
//    if (operation == null) {
//      throw new WebCoffeeException("path operation is not defined " + $ref);
//    }
//    return operation;
//  }

  public Schema<?> loadSchema(String $ref) throws WebCoffeeException {
    String[] ref = extractString($ref);
    WebCoffeeResources resources;
    resources = validateOpenAPiSpec($ref, ref);
    return getSchemaFromName(ref[3], resources.getOpenAPI());
  }

  private String[] extractString(String value) {
    return value.split("#");
  }

  private WebCoffeeResources validateOpenAPiSpec(String $ref, String[] ref)
      throws WebCoffeeException {
//    if (ref.length != 4) {
//      throw new WebCoffeeException("invalid reference" + $ref);
//    }
//    WebCoffeeResources resources = this.webCoffee
//        .getResources().get(ref[0]);
//    if (resources == null || resources.getOpenAPI() == null) {
//      throw new WebCoffeeException("invalid resources map" + ref[0]);
//    }
    return null;
  }
}
