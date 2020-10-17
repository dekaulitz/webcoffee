package com.github.dekaulitz.webcoffee.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.FileLoader;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.modules.model.ExternalDocs;
import com.github.dekaulitz.webcoffee.modules.model.WebCoffeeResources;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebCoffeeResourceParser {

  private final JsonNode nodeEntry;

  public WebCoffeeResourceParser(JsonNode nodeEntry) {
    this.nodeEntry = nodeEntry;
  }

  public WebCoffeeResources getResources() {
    WebCoffeeResources webCoffeeResources = new WebCoffeeResources();
    try {
      webCoffeeResources.setPath(NodeHelper.getNodeString((ObjectNode) nodeEntry, "path", true));
      Path refPath = Paths.get(webCoffeeResources.getPath());
      if (Files.notExists(refPath)) {
        throw new WebCoffeeException(
            "file " + webCoffeeResources.getPath() + " is not exists");
      }
      String openApiContent = FileLoader.loadContent(refPath);
      webCoffeeResources.setOpenAPI(FileLoader
          .parsingIntoOpenApi(openApiContent)
          .getOpenAPI());
      webCoffeeResources.setOpenAPINode(JsonMapper.mapper().readTree(openApiContent));
      webCoffeeResources.setExternalDocs(
          getWebCoffeeExternalDocs(
              NodeHelper.getObjectNode((JsonNode) nodeEntry, "externalDocs", false)));
    } catch (JsonProcessingException e) {
      throw new WebCoffeeException(e.getMessage());
    }
    return webCoffeeResources;
  }

  private ExternalDocs getWebCoffeeExternalDocs(ObjectNode externalDocs) {
    ExternalDocs webCoffeeExternalDocs = new ExternalDocs();
    if (externalDocs != null) {
      webCoffeeExternalDocs
          .setDescription(NodeHelper.getNodeString(externalDocs, "description", false));
      webCoffeeExternalDocs.setUrl(NodeHelper.getNodeString(externalDocs, "url", false));
    }
    return webCoffeeExternalDocs;
  }
}
