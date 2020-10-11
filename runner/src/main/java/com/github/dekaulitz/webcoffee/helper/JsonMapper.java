package com.github.dekaulitz.webcoffee.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.v3.parser.ObjectMapperFactory;

public class JsonMapper {

  private static ObjectMapper mapper;

  public static ObjectMapper mapper() {
    if (mapper == null) {
      mapper = ObjectMapperFactory.createJson();
    }
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    return mapper;
  }

  public static ObjectWriter pretty() {
    return mapper().writer(new DefaultPrettyPrinter());
  }
}

