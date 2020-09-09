package com.github.dekaulitz.webcoffee.models.runner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WebCoffeeRunnerEnv {

  private String url;
  private String description;

}
