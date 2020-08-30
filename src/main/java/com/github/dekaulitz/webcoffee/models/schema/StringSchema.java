package com.github.dekaulitz.webcoffee.models.schema;

public class StringSchema extends io.swagger.v3.oas.models.media.StringSchema {

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
