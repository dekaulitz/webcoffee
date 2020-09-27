package com.github.dekaulitz.webcoffee.models.runner;

import java.util.Map;
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
public class WebCoffeeTestRequest {

  private String host;
  private Map<String, WebCoffeeArgumentsRunner> arguments;
  private WebCoffeeDoRequest doRequest;
}
