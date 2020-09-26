package com.github.dekaulitz.webcoffee.parser;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dekaulitz.webcoffee.helper.NodeHelper;
import com.github.dekaulitz.webcoffee.models.WebCoffeeEnvironmentInfo;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import java.util.HashMap;
import java.util.Map;

public class WebCoffeeRunnerParser {

  private final ObjectNode runner;

  public WebCoffeeRunnerParser(ObjectNode runner) {
    this.runner = runner;
  }

  public WebCoffeeRunnerEnv getRunner(
      Map<String, WebCoffeeEnvironmentInfo> environment) {
    WebCoffeeRunnerEnv webCoffeeRunnerEnv = new WebCoffeeRunnerEnv();
    webCoffeeRunnerEnv.setMode(NodeHelper.getNodeString(runner, "mode", false));
    webCoffeeRunnerEnv.setEnvironment(NodeHelper.getNodeString(runner, "environment", true));
    WebCoffeeEnvironmentInfo webCoffeeEnvironmentInfo = environment
        .get(webCoffeeRunnerEnv.getEnvironment());
    if (webCoffeeEnvironmentInfo != null) {
      webCoffeeRunnerEnv.setHostname(environment.get(webCoffeeRunnerEnv.getEnvironment()).getUrl());
    }
    webCoffeeRunnerEnv
        .setArguments(getArguments(NodeHelper.getObjectNode(runner, "arguments", false)));
    webCoffeeRunnerEnv.set
    return webCoffeeRunnerEnv;
  }

  private Map<String, WebCoffeeArgumentsRunner> getArguments(ObjectNode arguments) {
    Map<String, WebCoffeeArgumentsRunner> webCoffeeArgumentsRunnerMap = new HashMap<>();
    if (arguments != null) {
      arguments.fields().forEachRemaining(nodeEntry -> {
        String argumentKey = nodeEntry.getKey();
        WebCoffeeArgumentsRunner webCoffeeArgumentsRunner = new WebCoffeeArgumentsRunner();
        webCoffeeArgumentsRunner
            .setLang(NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "lang", false));
        webCoffeeArgumentsRunner
            .setSrc(NodeHelper.getNodeString((ObjectNode) nodeEntry.getValue(), "src", false));
        webCoffeeArgumentsRunnerMap.put(argumentKey, webCoffeeArgumentsRunner);
      });
    }
    return webCoffeeArgumentsRunnerMap;
  }
}
