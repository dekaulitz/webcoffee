package com.github.dekaulitz.webcoffee.parser;

import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeObject;
import static com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper.getNodeString;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.helper.FileLoader;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.helper.WebCoffeeHelper;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.models.WebCoffeeInfo;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeParser {

  public final static String WEBCOFFEE = "webCoffee";
  public final static String INFO = "info";
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
    webCoffee.setResources(
        new WebCoffeeResourcesParser()
            .getWebCoffeeResources(
                getNodeObject("resources", true, jsonNode),
                this));
    webCoffee.setSpecs(new WebCoffeeSpecParser()
        .getWebCoffeeSpecs(webCoffee.getResources(),
            getNodeObject("specs", true, jsonNode),
            this));
    webCoffee.setRunner(getWebCoffeeRunner(webCoffee,jsonNode));
    return webCoffee;
  }

  private Map<String, Object> getWebCoffeeRunner(WebCoffee webCoffee, JsonNode jsonNode)
      throws WebCoffeeException {
    return JsonMapper.mapper()
        .convertValue(getNodeObject("runner", true, jsonNode),
            new TypeReference<Map<String, Object>>() {
            });
  }

  private WebCoffeeInfo getWebCoffeInfo(JsonNode jsonNode) throws WebCoffeeException {
    WebCoffeeInfo webCoffeeInfo = new WebCoffeeInfo();
    webCoffeeInfo.setTitle(WebCoffeeHelper.getNodeString("title", false, jsonNode));
    webCoffeeInfo.setVersion(WebCoffeeHelper.getNodeString("version", false, jsonNode));
    return webCoffeeInfo;
  }
}
