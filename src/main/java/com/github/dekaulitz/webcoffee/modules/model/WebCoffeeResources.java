package com.github.dekaulitz.webcoffee.modules.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WebCoffeeResources {

  private String path;
  private ExternalDocs externalDocs;
  private OpenAPI openAPI;
  private JsonNode openAPINode;
}