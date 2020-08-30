package com.github.dekaulitz.webcoffee.model.webcoffee;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class WebCoffeeRunnerTest {

  private final OpenAPI openAPI;

  WebCoffeeRunnerTest() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    //using static swagger json for testing
    File file = new File(classLoader.getResource("cofee.dologin.json").getFile());
    String defaultTemplate = new String(Files.readAllBytes(file.toPath()));
    SwaggerParseResult result = new OpenAPIParser().readContents(defaultTemplate, null, null);
    this.openAPI = result.getOpenAPI();
  }

  @Test
  void getWebCoffee() {

  }
}
