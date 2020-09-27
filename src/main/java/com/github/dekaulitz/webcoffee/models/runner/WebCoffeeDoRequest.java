package com.github.dekaulitz.webcoffee.models.runner;

import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class WebCoffeeDoRequest {

  private String $ref;
  private WebCoffeeSpecs referenceSpec;
  private WebCoffeeThenRequest then;

  public String get$ref() {
    return $ref;
  }

  public void set$ref(String $ref) {
    this.$ref = $ref;
  }
}
