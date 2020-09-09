package com.github.dekaulitz.webcoffee.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffeeEnvironmentInfo {

  private String url;
  private String description;
}
