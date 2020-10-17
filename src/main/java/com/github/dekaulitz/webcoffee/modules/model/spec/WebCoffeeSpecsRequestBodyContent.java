package com.github.dekaulitz.webcoffee.modules.model.spec;

import com.github.dekaulitz.webcoffee.modules.openapi.schemas.WebCoffeeSchema;
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
public class WebCoffeeSpecsRequestBodyContent {

  private String mediaType;
  private String $ref;
  private Schema schema;
  private WebCoffeeSchema payload;

  public String get$ref() {
    return $ref;
  }

  public void set$ref(String $ref) {
    this.$ref = $ref;
  }
}
