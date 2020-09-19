package com.github.dekaulitz.webcoffee.models.spec;


import com.github.dekaulitz.webcoffee.models.schema.WebCoffeeSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffeeSchemaValidation {

  private String $ref;
  private Schema schema;
  private WebCoffeeSchema expectValue;
  private String rawSchema;

  public String get$ref() {
    return $ref;
  }

  public void set$ref(String $ref) {
    this.$ref = $ref;
  }
}
