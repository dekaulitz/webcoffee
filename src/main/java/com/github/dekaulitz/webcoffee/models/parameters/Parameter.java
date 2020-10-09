package com.github.dekaulitz.webcoffee.models.parameters;

public class Parameter extends io.swagger.v3.oas.models.parameters.Parameter {

  protected String value;
  protected String argument;
  protected ParameterFrom from;
  protected String prefix;
  protected String suffix;

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

  public ParameterFrom getFrom() {
    return from;
  }

  public void setFrom(ParameterFrom from) {
    this.from = from;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }
}
