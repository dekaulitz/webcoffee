package com.github.dekaulitz.webcoffee.models.spec;

import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import io.swagger.v3.oas.models.Operation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffeeGiven {

  private String $ref;
  private String httpMethod;
  private String pathEndpoint;
  private Operation operation;
  private List<Parameter> parameters;
  private WebCoffeeRequestBody requestBody;

  public String get$ref() {
    return $ref;
  }

  public void set$ref(String $ref) {
    this.$ref = $ref;
  }
}
