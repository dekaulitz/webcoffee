package com.github.dekaulitz.webcoffee.openapi.schemas;

import java.math.BigDecimal;

public class NumberSchema extends WebCoffeeSchema<BigDecimal> {

  private BigDecimal value;

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

}
