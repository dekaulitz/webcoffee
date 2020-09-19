package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.FileLoader;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.models.WebCoffeeEnvironmentInfo;
import com.github.dekaulitz.webcoffee.models.WebCoffeeInfo;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeParser {

  private JsonNode jsonNode;
  @Getter
  @Setter
  private String message = "";

  public WebCoffeeParser loadContent(String webCoffee) {
    log.info("load configuration from {}", webCoffee);
    try {
      String webCoffeeString = FileLoader.loadContent(Paths.get(webCoffee));
      this.jsonNode = JsonMapper.mapper().readTree(webCoffeeString);
    } catch (JsonProcessingException | WebCoffeeException e) {
      e.printStackTrace();
      log.error(e);
      this.message = e.getMessage();
    }
    return this;
  }

  public WebCoffee getWebCoffeeResult()
      throws WebCoffeeException {
    WebCoffee webCoffee = new WebCoffee();
    webCoffee.setWebCoffee(getNodeString("webCoffee", true, jsonNode));
    webCoffee.setInfo(getWebCoffeInfo(getNodeObject("info", true, jsonNode)));
    webCoffee.setEnvironment(getEnvironment(getNodeObject("environment", false, jsonNode)));
    webCoffee.setResources(
        new WebCoffeeResourcesParser()
            .getWebCoffeeResources(
                getNodeObject("resources", true, jsonNode),
                this));
    webCoffee.setSpecs(new WebCoffeeSpecParser()
        .getWebCoffeeSpecs(webCoffee.getResources(),
            getNodeObject("specs", true, jsonNode),
            this));
    webCoffee.setRunner(getWebCoffeeRunner(webCoffee, getNodeObject("runner", true, jsonNode)));
    return webCoffee;
  }

  private Map<String, WebCoffeeEnvironmentInfo> getEnvironment(ObjectNode environment) {
    Map<String, WebCoffeeEnvironmentInfo> webCoffeeEnvironment = new HashMap<>();
    environment.fields().forEachRemaining(value -> {
      try {
        final String key = value.getKey();
        WebCoffeeEnvironmentInfo webCoffeeEnvironmentInfo = new WebCoffeeEnvironmentInfo();
        webCoffeeEnvironmentInfo
            .setDescription(getNodeString("description", false, value.getValue()));
        webCoffeeEnvironmentInfo.setUrl(getNodeString("url", false, value.getValue()));
        webCoffeeEnvironment.put(key, webCoffeeEnvironmentInfo);
      } catch (WebCoffeeException e) {
        e.printStackTrace();
      }
    });
    return webCoffeeEnvironment;
  }

  private WebCoffeeRunnerEnv getWebCoffeeRunner(WebCoffee webCoffee,
      ObjectNode runner) {
    WebCoffeeRunnerEnv webCoffeeRunnerEnv = new WebCoffeeRunnerEnv();
    try {
      webCoffeeRunnerEnv.setEnvironment(getNodeString("environment", false, runner));
      webCoffeeRunnerEnv.setMode(getNodeString("mode", false, runner));
      String hostName = webCoffee.getEnvironment().get(webCoffeeRunnerEnv.getEnvironment())
          .getUrl();
      if (hostName.isEmpty()) {
        throw new WebCoffeeException(
            "environment " + webCoffeeRunnerEnv.getEnvironment() + " is not initialized");
      }
      webCoffeeRunnerEnv.setHostname(hostName);
    } catch (WebCoffeeException e) {
      e.printStackTrace();
    }
    return webCoffeeRunnerEnv;
  }


  private WebCoffeeInfo getWebCoffeInfo(JsonNode jsonNode) throws WebCoffeeException {
    WebCoffeeInfo webCoffeeInfo = new WebCoffeeInfo();
    webCoffeeInfo.setTitle(WebCoffeeHelper.getNodeString("title", false, jsonNode));
    webCoffeeInfo.setVersion(WebCoffeeHelper.getNodeString("version", false, jsonNode));
    return webCoffeeInfo;
  }
}
