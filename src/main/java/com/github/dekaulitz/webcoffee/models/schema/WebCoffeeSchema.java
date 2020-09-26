package com.github.dekaulitz.webcoffee.models.schema;


import io.swagger.v3.oas.models.media.Schema;

public class WebCoffeeSchema<T> extends Schema<T> {

  protected T value;
  protected T $refValue;
  protected String arguments;

  public String getArguments() {
    return arguments;
  }

  public void setArguments(String arguments) {
    this.arguments = arguments;
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
