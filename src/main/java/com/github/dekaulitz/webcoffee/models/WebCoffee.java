package com.github.dekaulitz.webcoffee.models;

import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffee implements Serializable {

  private String webCoffee;
  private WebCoffeeInfo info;
  private Map<String, WebCoffeeEnvironmentInfo>  environment;
  private Map<String, WebCoffeeResources> resources;
  private Map<String, WebCoffeeSpecs> specs;
  private Map<String, WebCoffeeRunnerEnv> runner;

}
