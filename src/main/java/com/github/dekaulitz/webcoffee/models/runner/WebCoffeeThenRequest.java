package com.github.dekaulitz.webcoffee.models.runner;

import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import com.github.dekaulitz.webcoffee.models.schema.WebCoffeeSchema;
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

    private Set<Parameter> parameters;
    private WebCoffeeSchema response;
    private WebCoffeeDoRequest doRequest;
  }
}
