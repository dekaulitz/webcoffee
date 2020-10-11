package com.github.dekaulitz.webcoffee.model.spec;

import java.util.Map;
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
@ToString(callSuper = true)
public class WebCoffeeSpecsRequestBody {

  private Map<String, WebCoffeeSpecsRequestBodyContent> content;
}
