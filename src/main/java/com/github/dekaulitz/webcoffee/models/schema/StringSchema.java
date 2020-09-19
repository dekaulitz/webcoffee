package com.github.dekaulitz.webcoffee.models.schema;

public class StringSchema extends WebCoffeeSchema<String> {

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
