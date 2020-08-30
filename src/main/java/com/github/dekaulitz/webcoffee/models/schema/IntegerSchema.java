package com.github.dekaulitz.webcoffee.models.schema;

public class IntegerSchema extends io.swagger.v3.oas.models.media.IntegerSchema {

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
