package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.FileLoader;
import com.github.dekaulitz.webcoffee.models.WebCoffeeExternalDocs;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import io.swagger.v3.oas.models.OpenAPI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@NoArgsConstructor
@Log4j2
public class WebCoffeeResourcesParser {

  public final static String RESOURCES = "resources";

  public Map<String, WebCoffeeResources> getWebCoffeeResources(JsonNode jsonNode,
      WebCoffeeParser webCoffeeParser) {
    Map<String, WebCoffeeResources> webCoffeeResourcesMap = new HashMap<>();
    jsonNode.fields().forEachRemaining(stringJsonNodeEntry -> {
      try {
        WebCoffeeResources webCoffeeResources = new WebCoffeeResources();
        String key = stringJsonNodeEntry.getKey();
        JsonNode node = stringJsonNodeEntry.getValue();
        String path = getNodeString("path", true, node);
        webCoffeeResources.setPath(path);
        webCoffeeResources.setPath(path);
        Path refPath = Paths.get(path);
        if (Files.notExists(refPath)) {
          throw new WebCoffeeException(
              "file " + webCoffeeResources.getPath() + " is not exists");
        }
        OpenAPI openAPI = FileLoader
            .parsingIntoOpenApi(FileLoader.loadContent(refPath))
            .getOpenAPI();
        webCoffeeResources.setOpenAPI(openAPI);
        webCoffeeResources.setExternalDocs(
            getExternalDocs(getNodeObject("externalDocs", true, node)));
        webCoffeeResourcesMap.put(key, webCoffeeResources);
      } catch (WebCoffeeException e) {
        e.printStackTrace();
        log.error(e);
        webCoffeeParser.setMessage(e.getMessage());
      }
    });

    return webCoffeeResourcesMap;
  }

  private WebCoffeeExternalDocs getExternalDocs(JsonNode externalDocs) throws WebCoffeeException {
    WebCoffeeExternalDocs webCoffeeExternalDocs = new WebCoffeeExternalDocs();
    webCoffeeExternalDocs.setUrl(getNodeString("url", false, externalDocs));
    webCoffeeExternalDocs.setDescription(getNodeString("description", false, externalDocs));
    return webCoffeeExternalDocs;
  }
}
