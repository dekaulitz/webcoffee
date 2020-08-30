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
public class WebCoffeeRequestBody {

  private Map<String, WebCoffeeRequestBodyContent> content;
}
