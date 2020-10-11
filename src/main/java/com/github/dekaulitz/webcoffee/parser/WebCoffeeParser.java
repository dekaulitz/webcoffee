package com.github.dekaulitz.webcoffee.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.FileLoader;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.models.EnvironmentInfo;
import com.github.dekaulitz.webcoffee.models.Info;
import com.github.dekaulitz.webcoffee.models.WebCoffeeResources;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebCoffeeParser {

  @Getter
  private WebCoffee webCoffee;

  public WebCoffeeParser loadContent(String path)
      throws WebCoffeeException, JsonProcessingException {
    String webCoffeeString = FileLoader.loadContent(Paths.get(path));
    JsonNode webCoffeeNode = JsonMapper.mapper().readTree(webCoffeeString);
    webCoffee = this.parseWebCoffee(webCoffeeNode);
    return this;
  }

  private WebCoffee parseWebCoffee(JsonNode webCoffeeNode) {
    WebCoffee webCoffee = new WebCoffee();
    webCoffee.setWebCoffee(
        NodeHelper.getNodeString((ObjectNode) webCoffeeNode, "webCoffee", false));
    webCoffee
        .setInfo(getWebCoffeeInfo(NodeHelper.getObjectNode(webCoffeeNode, "info", true)));
    webCoffee.setEnvironment(
        getEnvironment(NodeHelper.getObjectNode(webCoffeeNode, "environment", true)));
    webCoffee
        .setResources(getResources(NodeHelper.getObjectNode(webCoffeeNode, "resources", true)));
    webCoffee
        .setSpecs(new WebCoffeeSpecParser(NodeHelper.getObjectNode(webCoffeeNode, "specs", true))
            .getSpecs(webCoffee));
    webCoffee.setRunner(
        new WebCoffeeRunnerParser(NodeHelper.getObjectNode(webCoffeeNode, "runner", true),
            webCoffee)
            .getRunner(webCoffee.getEnvironment()));
    return webCoffee;
  }

  private Map<String, WebCoffeeResources> getResources(ObjectNode resources) {
    Map<String, WebCoffeeResources> webCoffeeResourcesMap = new HashMap<>();
    resources.fields().forEachRemaining(nodeEntry -> {
      String resourceKey = nodeEntry.getKey();
      webCoffeeResourcesMap
          .put(resourceKey, new WebCoffeeResourceParser(nodeEntry.getValue()).getResources());
    });
    return webCoffeeResourcesMap;
  }

  private Map<String, EnvironmentInfo> getEnvironment(ObjectNode environment) {
    Map<String, EnvironmentInfo> webCoffeeEnvironmentInfoMap = new HashMap<>();
    environment.fields().forEachRemaining(nodeEntry -> {
      String environmentString = nodeEntry.getKey();
      EnvironmentInfo environmentInfo = new EnvironmentInfo();
      environmentInfo
          .setUrl(NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "url", false));
      environmentInfo.setDescription(
          NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "description", false));
      webCoffeeEnvironmentInfoMap.put(environmentString, environmentInfo);
    });
    return webCoffeeEnvironmentInfoMap;
  }

  private Info getWebCoffeeInfo(ObjectNode info) {
    Info webCoffeeInfo = new Info();
    webCoffeeInfo.setVersion(NodeHelper.getNodeString(info, "version", true));
    webCoffeeInfo.setTitle(NodeHelper.getNodeString(info, "title", true));
    return webCoffeeInfo;
  }


}
