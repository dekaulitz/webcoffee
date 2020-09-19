package com.github.dekaulitz.webcoffee.models.schema;


import io.swagger.v3.oas.models.media.Schema;

public class WebCoffeeSchema<T> extends Schema<T> {

  protected T value;
  protected T $refValue;

  public T getValue() {
    return value;
  }

  public T get$refValue() {
    return $refValue;
  }

  public void set$refValue(T $refValue) {
    this.$refValue = $refValue;
  }
}
