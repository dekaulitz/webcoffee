package com.github.dekaulitz.webcoffee.helper;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FileLoader {

  public static String loadContent(Path path) throws WebCoffeeException {
    try {
      return new String(Files.readAllBytes(path));
    } catch (IOException e) {
      throw new WebCoffeeException(path.getFileName() + " is not found");
    }
  }

  public static SwaggerParseResult parsingIntoOpenApi(String openApi) {
    return new OpenAPIParser().readContents(openApi, null, null);
  }

  public static Map<String, OpenAPI> loadFile(String location) throws IOException {
    Map<String, OpenAPI> openAPIMap = new HashMap<>();
    try (Stream<Path> paths = Files
        .walk(Paths.get(location))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(path -> {
            try {
              String contract = loadContent(path);
              OpenAPI openAPI = parsingIntoOpenApi(contract).getOpenAPI();
              openAPIMap.put(String.valueOf(path.getFileName()), openAPI);
            } catch (WebCoffeeException e) {
              log.error(e.getMessage());
            }
          });
    }
    return openAPIMap;
  }
}
