package com.github.dekaulitz.webcoffee.modules.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentInfo {

  private String url;
  private String description;
}