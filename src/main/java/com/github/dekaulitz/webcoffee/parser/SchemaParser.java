package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.models.schema.StringSchema;
import com.github.dekaulitz.webcoffee.models.schema.WebCoffeeSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.util.SchemaTypeUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class SchemaParser extends SchemaTypeUtil {

  private static final String TYPE = "type";
  private static final String FORMAT = "format";
  private static final String VALUE = "value";
  private static final String REFVALUE = "$refValue";
  private static final String argument = "$refValue";

  public static WebCoffeeSchema createSchemaFromNode(ObjectNode node) throws WebCoffeeException {
    final String type = NodeHelper.getNodeString(node, TYPE, true);
    final String format = NodeHelper.getNodeString(node, FORMAT, false);
    final String value = NodeHelper.getNodeString(node, VALUE, false);
    final String refValue = NodeHelper.getNodeString(node, REFVALUE, false);
    final String argumentValue = NodeHelper.getNodeString(node, argument, false);
    return createSchema(createSchema(type, format, value, refValue, argumentValue), node);
  }

  private static WebCoffeeSchema createSchema(WebCoffeeSchema schema,
      ObjectNode node) throws WebCoffeeException {
    schema.setTitle(getNodeString("title", false, node));
    ObjectNode propertiesNode = NodeHelper.getObjectNode(node, "properties", false);
    if (propertiesNode != null) {
      Map<String, Schema> properties = new HashMap<>();
      propertiesNode.fields().forEachRemaining(stringJsonNodeEntry -> {
        final String key = stringJsonNodeEntry.getKey();
        final ObjectNode objectNode = (ObjectNode) stringJsonNodeEntry.getValue();
        Schema propertySchema = createSchemaFromNode(objectNode);
        properties.put(key, propertySchema);
      });
      schema.setProperties(properties);
    }
    return schema;
  }

  public static WebCoffeeSchema createSchema(String type, String format, String value,
      String refValue, String argumentValue) {
    WebCoffeeSchema webCoffeeSchema;
    if (STRING_TYPE.equals(type)) {
      if (StringUtils.isBlank(format)) {
        webCoffeeSchema = new StringSchema();
        webCoffeeSchema.setFormat(format);
      } else {
        webCoffeeSchema = new StringSchema();
      }
    } else {
      webCoffeeSchema = new WebCoffeeSchema();
    }
    webCoffeeSchema.setValue(value);
    webCoffeeSchema.set$refValue(refValue);
    webCoffeeSchema.set$refValue(refValue);
    webCoffeeSchema.setArguments(argumentValue);
    return webCoffeeSchema;
  }
}
