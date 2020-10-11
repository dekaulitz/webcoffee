package com.github.dekaulitz.webcoffee.model.runner;


import com.github.dekaulitz.webcoffee.openapi.parameters.Parameter;
import com.github.dekaulitz.webcoffee.openapi.schemas.WebCoffeeSchema;
import java.util.HashSet;
import java.util.Set;
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
public class WebCoffeeThenRequest {

  private Expect expect;

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  @ToString
  @Builder
  public static class Expect {

    private Integer httpStatus;
    private Set<Parameter> parameters = new HashSet<>();
    private WebCoffeeSchema<?> response;
    private WebCoffeeDoRequest doRequest;
    private Boolean schemaValidation;
  }
}
