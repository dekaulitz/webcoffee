package com.github.dekaulitz.webcoffee.executor.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.assertations.AssertionsGuardian;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.helper.ExecutorHelper;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeDoRequest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeTestRequest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeTestRequest.StatusTest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeThenRequest.Expect;
import io.restassured.response.Response;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class RestExecutor implements CoffeeExecutor {

  private String host;
  private Map<String, WebCoffeeArgumentsRunner> arguments;
  private Map<String, WebCoffeeTestRequest> coffeeTest;
  private Map<String, JsonNode> globalResponse = new HashMap<>();

  @Override
  public RestExecutor prepare(WebCoffeeRunnerEnv webCoffeeRunnerEnv) {
    this.host = webCoffeeRunnerEnv.getHost();
    this.arguments = webCoffeeRunnerEnv.getArguments();
    this.coffeeTest = webCoffeeRunnerEnv.getCoffeeTest();
    return this;
  }

  @Override
  public void execute() {
    coffeeTest.entrySet().stream().sorted(Comparator.comparing(o -> o.getValue().getOrder()))
        .forEach(webCoffeeTestRequest -> {
          WebCoffeeTestRequest testRequest = webCoffeeTestRequest.getValue();
          testRequest.setStartTime(System.currentTimeMillis());
          log.info("starting coffee with use case {}", webCoffeeTestRequest.getKey());
          this.executeRequest(testRequest,
              testRequest.getDoRequest());
          testRequest.setStatus(StatusTest.SUCCESS);
          testRequest.setExecutionTime(
              ExecutorHelper.getEndTime(testRequest.getEndTime()));
          log.info("clean up response map ");
          globalResponse = new HashMap<>();
          log.info("coffee with use case {} finished", webCoffeeTestRequest.getKey());
        });
  }

  public void executeRequest(final WebCoffeeTestRequest webCoffeeTestRequest,
      WebCoffeeDoRequest webCoffeeDoRequest) {
    log.info("start request {}", webCoffeeDoRequest.getReferenceSpec().getEndpoint());
    final RequestBuilder requestBuilder = RequestBuilder
        .initRequest(webCoffeeDoRequest);
    if (StringUtils.isNoneEmpty(webCoffeeTestRequest.getHost())) {
      requestBuilder.setHost(webCoffeeTestRequest.getHost());
    } else if (StringUtils.isNoneEmpty(this.host)) {
      requestBuilder.setHost(this.host);
    }
    requestBuilder.setGlobalResponse(globalResponse);
    requestBuilder.setGlobalArguments(this.arguments);
    requestBuilder.setArguments(webCoffeeTestRequest.getArguments());
    requestBuilder.createRequest();
    requestBuilder.execute();
    Response response = requestBuilder
        .getValidateResponse()
        .log().all()
        .extract().response();
    JsonNode responseNode = response.getBody().as(JsonNode.class);
    globalResponse.put(webCoffeeDoRequest.get$ref(), responseNode);
    final Expect expect = webCoffeeDoRequest.getThen().getExpect();

    try {
      AssertionsGuardian assertionsGuardian = new AssertionsGuardian(
          webCoffeeDoRequest.getReferenceSpec().getHttpMethod() + "#" + webCoffeeDoRequest
              .getReferenceSpec()
              .getEndpoint(), expect, response);
      assertionsGuardian.validate();
    } catch (AssertionError assertionError) {
      webCoffeeTestRequest.setStatus(StatusTest.FAIL);
      webCoffeeTestRequest
          .setExecutionTime(ExecutorHelper.getEndTime(webCoffeeTestRequest.getEndTime()));
      throw assertionError;
    }
    log.info("finishing request {}", webCoffeeDoRequest.getReferenceSpec().getEndpoint());
    if (expect.getDoRequest() != null) {
      executeRequest(webCoffeeTestRequest, expect.getDoRequest());
    }
  }

}
