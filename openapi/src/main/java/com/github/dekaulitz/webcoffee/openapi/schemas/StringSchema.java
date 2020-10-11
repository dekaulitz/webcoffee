package com.github.dekaulitz.webcoffee.openapi.schemas;

public class StringSchema extends WebCoffeeSchema<String> {

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
