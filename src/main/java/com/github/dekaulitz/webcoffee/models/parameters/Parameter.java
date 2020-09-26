package com.github.dekaulitz.webcoffee.models.parameters;

public class Parameter extends io.swagger.v3.oas.models.parameters.Parameter {

  protected String value;
  protected String argument;

  public String getArgument() {
    return argument;
  }

  public void setArgument(String argument) {
    this.argument = argument;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
