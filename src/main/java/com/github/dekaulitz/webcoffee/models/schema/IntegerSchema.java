package com.github.dekaulitz.webcoffee.models.schema;

public class IntegerSchema extends WebCoffeeSchema<Integer> {

  private Integer value;

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }
}
