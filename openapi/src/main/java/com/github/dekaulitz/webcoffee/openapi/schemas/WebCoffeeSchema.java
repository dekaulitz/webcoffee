package com.github.dekaulitz.webcoffee.openapi.schemas;


import io.swagger.v3.oas.models.media.Schema;

public class WebCoffeeSchema<T> extends Schema<T> {

  protected T value;
  protected T $refValue;
  protected String argument;

  public String getArgument() {
    return argument;
  }

  public void setArgument(String argument) {
    this.argument = argument;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public T get$refValue() {
    return $refValue;
  }

  public void set$refValue(T $refValue) {
    this.$refValue = $refValue;
  }
}
