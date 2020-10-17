package com.github.dekaulitz.webcoffee.modules.model;


import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.modules.model.spec.WebCoffeeSpecs;
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
  private Info info;
  private Map<String, EnvironmentInfo> environment;
  private Map<String, WebCoffeeResources> resources;
  private Map<String, WebCoffeeSpecs> specs;
  private WebCoffeeRunnerEnv runner;

}
