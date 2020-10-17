package com.github.dekaulitz.webcoffee.modules.openapi.parser;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.ArraySchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.BooleanSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.IntegerSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.NumberSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.StringSchema;
import com.github.dekaulitz.webcoffee.modules.openapi.schemas.WebCoffeeSchema;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class WebCoffeeSchemaParser extends SchemaTypeUtil {

  public static final String ARRAY_TYPE = "array";
  private static final String TYPE = "type";
  private static final String FORMAT = "format";
  private static final String VALUE = "value";
  private static final String REF_VALUE = "$refValue";
  private static final String ARGUMENT = "argument";
  private static final String REQUIRED = "required";


  public static WebCoffeeSchema createSchemaFromNode(ObjectNode node) {

    return createSchema(createSchema(node), node);
  }

  private static WebCoffeeSchema createSchema(WebCoffeeSchema schema,
      ObjectNode node) {
    schema.setTitle(NodeHelper.getNodeString(node, "title", false));
    ObjectNode propertiesNode = NodeHelper.getObjectNode(node, "properties", false);
    if (propertiesNode != null) {
      Map<String, WebCoffeeSchema<?>> properties = new HashMap<>();
      propertiesNode.fields().forEachRemaining(stringJsonNodeEntry -> {
        final String key = stringJsonNodeEntry.getKey();
        final ObjectNode objectNode = (ObjectNode) stringJsonNodeEntry.getValue();
        WebCoffeeSchema propertySchema = createSchemaFromNode(objectNode);
        properties.put(key, propertySchema);
      });
      schema.setProperties(properties);
    }
    return schema;
  }

  public static WebCoffeeSchema createSchema(ObjectNode node) {
    final String type = NodeHelper.getNodeString(node, TYPE, true);
    final String format = NodeHelper.getNodeString(node, FORMAT, false);

    final String refValue = NodeHelper.getNodeString(node, REF_VALUE, false);
    final String argumentValue = NodeHelper.getNodeString(node, ARGUMENT, false);
    WebCoffeeSchema webCoffeeSchema;
    if (INTEGER_TYPE.equals(type)) {
      webCoffeeSchema = new IntegerSchema();
      final Integer value = NodeHelper.getNodeInteger(node, VALUE, false);
      webCoffeeSchema.setValue(value);
      if (!StringUtils.isBlank(format)) {
        webCoffeeSchema.setFormat(format);
        webCoffeeSchema.setValue(value);
      }
    } else if (NUMBER_TYPE.equals(type)) {
      webCoffeeSchema = new NumberSchema();
      final String value = NodeHelper.getNodeString(node, VALUE, false);
      webCoffeeSchema.setValue(value);
      if (!StringUtils.isBlank(format)) {
        webCoffeeSchema.setFormat(format);
      }
    } else if (STRING_TYPE.equals(type)) {
      final String value = NodeHelper.getNodeString(node, VALUE, false);
      webCoffeeSchema = new StringSchema();
      webCoffeeSchema.setValue(value);
      if (!StringUtils.isBlank(format)) {
        webCoffeeSchema.setFormat(format);
      }
    } else if (ARRAY_TYPE.equals(type)) {
      webCoffeeSchema = new ArraySchema();
      ObjectNode item = NodeHelper.getObjectNode(node, "items", false);
      if (item != null) {
        webCoffeeSchema.setItems(createSchemaFromNode(item));
      }
    } else if (BOOLEAN_TYPE.equals(type)) {
      final boolean value = NodeHelper.getBooleanNode(node, VALUE, false);
      webCoffeeSchema = new BooleanSchema();
      webCoffeeSchema.setValue(value);
      if (!StringUtils.isBlank(format)) {
        webCoffeeSchema.format(format);
      }
    } else {
      final String value = NodeHelper.getNodeString(node, VALUE, false);
      webCoffeeSchema = new WebCoffeeSchema();
      webCoffeeSchema.setValue(value);
    }
    ArrayNode requiredNode = NodeHelper.getArrayNode(node, "required", false);
    if (requiredNode != null && !requiredNode.isEmpty()) {
      List<String> requiredList = new ArrayList<>();
      requiredNode.forEach(jsonNode -> {
        requiredList.add(jsonNode.asText());
      });
      webCoffeeSchema.setRequired(requiredList);
    }
    if (node.get("minItems") != null) {
      webCoffeeSchema.setMinItems(NodeHelper.getNodeInteger(node, "minItems", false));
    }
    if (node.get("maxItems") != null) {
      webCoffeeSchema.setMaxItems(NodeHelper.getNodeInteger(node, "maxItems", false));
    }
    webCoffeeSchema.setType(type);
    webCoffeeSchema.set$refValue(refValue);
    webCoffeeSchema.set$refValue(refValue);
    webCoffeeSchema.setArgument(argumentValue);
    return webCoffeeSchema;
  }
}
