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
public class WebCoffeeRunnerEnv {

  private String mode;
  private String environment;
  private String applicationType;
  private String hostname;
  //global arguments
  private Map<String,WebCoffeeArgumentsRunner> arguments;
  private String coffeeTest;

}



