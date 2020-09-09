package com.github.dekaulitz.webcoffee.models.spec;


import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffeeStatements {

  private List<Parameter> parameters;
  private WebCoffeeRequestBody requestBody;

}
