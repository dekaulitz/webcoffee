package com.github.dekaulitz.webcoffee.models.spec;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffeeStatements {

  private Map<String, Object> parameters;
  private WebCoffeeRequestBody requestBody;

}
