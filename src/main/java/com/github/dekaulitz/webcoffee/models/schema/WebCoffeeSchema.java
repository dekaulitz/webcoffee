package com.github.dekaulitz.webcoffee.models.schema;


import io.swagger.v3.oas.models.media.Schema;

public class WebCoffeeSchema<T> extends Schema<T> {

  protected T value;

  public T getValue() {
    return value;
  }
}
