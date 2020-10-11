package com.github.dekaulitz.webcoffee.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.helper.ReferenceHandler;
import com.github.dekaulitz.webcoffee.model.EnvironmentInfo;
import com.github.dekaulitz.webcoffee.model.WebCoffee;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeDoRequest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeTestRequest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeThenRequest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeThenRequest.Expect;
import com.github.dekaulitz.webcoffee.model.spec.WebCoffeeSpecs;
import com.github.dekaulitz.webcoffee.scriptengine.ScriptFactory.Lang;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class WebCoffeeRunnerParser {

  private final ObjectNode runner;
  private final WebCoffee webCoffee;

  public WebCoffeeRunnerParser(ObjectNode runner,
      WebCoffee webCoffee) {
    this.runner = runner;
    this.webCoffee = webCoffee;
  }

  public WebCoffeeRunnerEnv getRunner(
      Map<String, EnvironmentInfo> environment) {
    WebCoffeeRunnerEnv webCoffeeRunnerEnv = new WebCoffeeRunnerEnv();
    webCoffeeRunnerEnv.setMode(NodeHelper.getNodeString(runner, "mode", true));
    webCoffeeRunnerEnv.setEnvironment(NodeHelper.getNodeString(runner, "environment", true));
    EnvironmentInfo environmentInfo = environment
        .get(webCoffeeRunnerEnv.getEnvironment());
    if (environmentInfo != null) {
      webCoffeeRunnerEnv.setHost(environment.get(webCoffeeRunnerEnv.getEnvironment()).getUrl());
    }
    webCoffeeRunnerEnv
        .setArguments(getArguments(NodeHelper.getObjectNode(runner, "arguments", false)));
    webCoffeeRunnerEnv
        .setCoffeeTest(getCoffeeTest(NodeHelper.getObjectNode(this.runner, "coffeeTest", true)));
    return webCoffeeRunnerEnv;
  }

  private Map<String, WebCoffeeTestRequest> getCoffeeTest(ObjectNode coffeeTest) {
    Map<String, WebCoffeeTestRequest> webCoffeeTestRequestMap = new HashMap<>();
    coffeeTest.fields().forEachRemaining(nodeEntry -> {
      final String nodeKey = nodeEntry.getKey();
      WebCoffeeTestRequest webCoffeeTestRequest = new WebCoffeeTestRequest();
      webCoffeeTestRequest
          .setHost(NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "host", false));
      webCoffeeTestRequest.setArguments(
          getArguments(NodeHelper.getObjectNode(nodeEntry.getValue(), "arguments", false)));
      webCoffeeTestRequestMap.put(nodeKey, webCoffeeTestRequest);
      webCoffeeTestRequest
          .setDoRequest(getDoRequest(NodeHelper.getObjectNode(nodeEntry.getValue(), "do", true)));
    });
    return webCoffeeTestRequestMap;
  }

  private WebCoffeeDoRequest getDoRequest(ObjectNode aDo) {
    WebCoffeeDoRequest webCoffeeDoRequest = new WebCoffeeDoRequest();
    ReferenceHandler referenceHandler = ReferenceHandler
        .getReference(NodeHelper.getNodeString(aDo, "$ref", true));
    webCoffeeDoRequest.set$ref(referenceHandler.getReference());
    WebCoffeeSpecs webCoffeeSpec = webCoffee.getSpecs()
        .get(referenceHandler.getObjectKey());
    if (webCoffeeSpec == null) {
      throw new WebCoffeeValidationExcepton(referenceHandler.getReference() + "is not found");
    }
    webCoffeeDoRequest.setReferenceSpec(webCoffeeSpec);
    webCoffeeDoRequest.setThen(getThenRequest(NodeHelper.getObjectNode(aDo, "then", true)));
    return webCoffeeDoRequest;
  }

  private WebCoffeeThenRequest getThenRequest(ObjectNode then) {
    WebCoffeeThenRequest webCoffeeThenRequest = new WebCoffeeThenRequest();
    JsonNode expectNode = NodeHelper.getObjectNode(then, "expect", true);
    Expect expect = new Expect();
    expect.setHttpStatus(NodeHelper.getNodeInteger((ObjectNode) expectNode, "httpStatus", true));
    ArrayNode nodeParameters = NodeHelper
        .getArrayNode(expectNode, "parameters", false);
    if (nodeParameters != null) {
      expect.setParameters(WebCoffeeParameterParser
          .getParameters(nodeParameters));
    }
    ObjectNode responseNode = NodeHelper.getObjectNode(expectNode, "response", false);
    if (responseNode != null) {
      expect.setResponse(WebCoffeeSchemaParser
          .createSchemaFromNode(responseNode));
    }
    ObjectNode doRequest = NodeHelper.getObjectNode(expectNode, "do", false);
    if (doRequest != null) {
      WebCoffeeDoRequest webCoffeeDorequest = getDoRequest(doRequest);
      expect.setDoRequest(webCoffeeDorequest);
    }
    webCoffeeThenRequest.setExpect(expect);
    return webCoffeeThenRequest;
  }


  private Map<String, WebCoffeeArgumentsRunner> getArguments(ObjectNode arguments) {
    Map<String, WebCoffeeArgumentsRunner> webCoffeeArgumentsRunnerMap = new HashMap<>();
    if (arguments != null) {
      arguments.fields().forEachRemaining(nodeEntry -> {
        String argumentKey = nodeEntry.getKey();
        WebCoffeeArgumentsRunner webCoffeeArgumentsRunner = new WebCoffeeArgumentsRunner();
        String lang = NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "lang", false);
        if (StringUtils.isNoneEmpty(lang)) {
          webCoffeeArgumentsRunner
              .setLang(Lang.getLang(lang));
        }
        webCoffeeArgumentsRunner
            .setSrc(NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "src", false));
        webCoffeeArgumentsRunner.setArgumentName(argumentKey);
        webCoffeeArgumentsRunnerMap.put(argumentKey, webCoffeeArgumentsRunner);
      });
    }
    return webCoffeeArgumentsRunnerMap;
  }
}
