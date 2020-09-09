package com.github.dekaulitz.webcoffee.models.parameters;

public class Parameter extends io.swagger.v3.oas.models.parameters.Parameter {

  protected String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
