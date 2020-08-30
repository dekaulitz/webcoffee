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
public class WebCoffeeSpecs {

  private Map<String, Object> def;
  private WebCoffeeGiven given;
  private WebCoffeeExpect expect;
}
