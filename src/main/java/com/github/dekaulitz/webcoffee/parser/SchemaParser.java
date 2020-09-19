package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
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

  public static WebCoffeeSchema createSchemaFromNode(ObjectNode node) throws WebCoffeeException {
    final String type = getNodeString(TYPE, true, node);
    final String format = getNodeString(FORMAT, false, node);
    final String value = getNodeString(VALUE, false, node);
    final String refValue = getNodeString(REFVALUE, false, node);
    return createSchema(createSchema(type, format, value, refValue), node);
  }

  private static WebCoffeeSchema createSchema(WebCoffeeSchema schema,
      ObjectNode node) throws WebCoffeeException {
    schema.setTitle(getNodeString("title", false, node));
    ObjectNode propertiesNode = getNodeObject("properties", false, node);
    if (propertiesNode != null) {
      Map<String, Schema> properties = new HashMap<>();
      propertiesNode.fields().forEachRemaining(stringJsonNodeEntry -> {
        final String key = stringJsonNodeEntry.getKey();
        final ObjectNode objectNode = (ObjectNode) stringJsonNodeEntry.getValue();
        try {
          Schema propertySchema = createSchemaFromNode(objectNode);
          properties.put(key, propertySchema);
        } catch (WebCoffeeException e) {
          e.printStackTrace();
          log.warn(e.getMessage());
        }
      });
      schema.setProperties(properties);
    }
    return schema;
  }

  public static WebCoffeeSchema createSchema(String type, String format, String value,
      String refValue) {
    if (STRING_TYPE.equals(type)) {
      if (StringUtils.isBlank(format)) {
        StringSchema stringSchema = new StringSchema();
        stringSchema.format(null);
        stringSchema.setValue(value);
        stringSchema.set$refValue(refValue);
        return stringSchema;
      } else {
        StringSchema stringSchema = new StringSchema();
        stringSchema.format(format);
        stringSchema.setValue(value);
        stringSchema.set$refValue(refValue);
        return stringSchema;
      }
    } else {
      return new WebCoffeeSchema();
    }
  }
}
