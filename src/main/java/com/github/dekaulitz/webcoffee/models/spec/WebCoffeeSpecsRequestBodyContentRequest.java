package com.github.dekaulitz.webcoffee.models.spec;

import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class WebCoffeeSpecsRequestBodyContentRequest {

  private String $ref;
  private Schema schema;
  private Schema payload;

  public String get$ref() {
    return $ref;
  }

  public void set$ref(String $ref) {
    this.$ref = $ref;
  }
}
