package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.schema.StringSchema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ByteArraySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.EmailSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.PasswordSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.UUIDSchema;
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

  public static Schema createSchemaFromNode(ObjectNode node) throws WebCoffeeException {
    final String type = getNodeString(TYPE, true, node);
    final String format = getNodeString(FORMAT, false, node);
    final String value = getNodeString(VALUE, false, node);
    return createSchema(createSchema(type, format, value), node);
  }

  private static Schema createSchema(Schema schema,
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

  public static Schema createSchema(String type, String format, String value) {

    if (INTEGER_TYPE.equals(type)) {
      if (StringUtils.isBlank(format)) {
        return new IntegerSchema().format(null);
      } else {
        return new IntegerSchema().format(format);
      }
    } else if (NUMBER_TYPE.equals(type)) {
      if (StringUtils.isBlank(format)) {
        return new NumberSchema();
      } else {
        return new NumberSchema().format(format);
      }
    } else if (BOOLEAN_TYPE.equals(type)) {
      if (StringUtils.isBlank(format)) {
        return new BooleanSchema();
      } else {
        return new BooleanSchema().format(format);
      }
    } else if (STRING_TYPE.equals(type)) {
      if (BYTE_FORMAT.equals(format)) {
        return new ByteArraySchema();
      } else if (BINARY_FORMAT.equals(format)) {
        return new BinarySchema();
      } else if (DATE_FORMAT.equals(format)) {
        return new DateSchema();
      } else if (DATE_TIME_FORMAT.equals(format)) {
        return new DateTimeSchema();
      } else if (PASSWORD_FORMAT.equals(format)) {
        return new PasswordSchema();
      } else if (EMAIL_FORMAT.equals(format)) {
        return new EmailSchema();
      } else if (UUID_FORMAT.equals(format)) {
        return new UUIDSchema();
      } else {
        if (StringUtils.isBlank(format)) {
          StringSchema stringSchema = new StringSchema();
          stringSchema.format(null);
          stringSchema.setValue(value);
          return stringSchema;
        } else {
          StringSchema stringSchema = new StringSchema();
          stringSchema.format(format);
          stringSchema.setValue(value);
          return stringSchema;
        }
      }
    } else if (OBJECT_TYPE.equals(type)) {
      return new ObjectSchema();
    } else {
      return new Schema();
    }
  }
}
