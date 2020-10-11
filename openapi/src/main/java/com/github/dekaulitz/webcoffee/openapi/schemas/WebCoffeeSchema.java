package com.github.dekaulitz.webcoffee.openapi.schemas;


import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.List;

public class WebCoffeeSchema<T> extends Schema<T> {

  protected T value;
  protected T $refValue;
  protected String argument;
  protected WebCoffeeSchema<?> items = null;
  protected List<String> required = new ArrayList<>();

  @Override
  public List<String> getRequired() {
    return required;
  }

  @Override
  public void setRequired(List<String> required) {
    this.required = required;
  }

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

  public WebCoffeeSchema<?> getItems() {
    return items;
  }

  public void setItems(WebCoffeeSchema<?> items) {
    this.items = items;
  }

  public WebCoffeeSchema<?> items(WebCoffeeSchema<?> items) {
    this.items = items;
    return this;
  }
}
