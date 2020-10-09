package com.github.dekaulitz.webcoffee.executor.rest.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeDoRequest;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeTestRequest;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeThenRequest.Expect;
import com.github.dekaulitz.webcoffee.models.schema.StringSchema;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Log4j2
public class RestExecutor implements CoffeeExecutor {

  private String host;
  private Map<String, WebCoffeeArgumentsRunner> arguments;
  private Map<String, WebCoffeeTestRequest> coffeeTest;
  private Map<String, Map<String, Object>> globalResponse = new HashMap<>();

  @Override
  public RestExecutor prepare(WebCoffeeRunnerEnv webCoffeeRunnerEnv) throws WebCoffeeException {
    this.host = webCoffeeRunnerEnv.getHost();
    this.arguments = webCoffeeRunnerEnv.getArguments();
    this.coffeeTest = webCoffeeRunnerEnv.getCoffeeTest();
    return this;
  }

  @Override
  public void execute() throws WebCoffeeException {
    coffeeTest.forEach((s, webCoffeeTestRequest) -> {
      log.info("starting coffee with use case {}", s);
      try {
        this.executeRequest(webCoffeeTestRequest, webCoffeeTestRequest.getDoRequest());
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        throw new WebCoffeeException(e.getMessage());
      }
      log.info("coffee with use case {} finished", s);
    });
  }

  @Test
  public void executeRequest(final WebCoffeeTestRequest webCoffeeTestRequest,
      WebCoffeeDoRequest webCoffeeDoRequest) throws JsonProcessingException {
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
    TypeReference<HashMap<String, Object>> typeRef
        = new TypeReference<HashMap<String, Object>>() {
    };
    Map<String, Object> responseMap = JsonMapper.mapper()
        .convertValue(response.getBody().as(JsonNode.class), typeRef);
    globalResponse.put(webCoffeeTestRequest.getDoRequest().get$ref(), responseMap);
    final Expect expect = webCoffeeDoRequest.getThen().getExpect();
    Assertions.assertEquals(response.getStatusCode(), expect.getHttpStatus(),
        "expect httpStatusCode " + expect.getHttpStatus());
    if (expect.getParameters() != null) {
      expect.getParameters().forEach(parameter -> {
        if (parameter.getIn().equals("header")) {
          if (parameter.getRequired()) {
            Assertions.assertNotNull(response.getHeader(parameter.getName()),
                "expecting " + parameter.getName() + " is not empty");
          }
          if (StringUtils.isNoneEmpty(parameter.getValue())) {
            Assertions.assertEquals(response.getHeader(parameter.getName()), parameter.getValue(),
                "expecting " + parameter.getName() + " is not empty");
          }
        }
      });
    }
    if (expect.getResponse() != null) {
      expect.getResponse().getProperties().forEach((field, property) -> {
        final String responseField = (String) field;
        if (property instanceof StringSchema) {
          Assertions
              .assertNotNull(responseMap.get(responseField), "expecting " + field + " is required");
        }
      });
    }
    log.info("finishing request {}", webCoffeeDoRequest.getReferenceSpec().getEndpoint());
    if (expect.getDoRequest() != null) {
      executeRequest(webCoffeeTestRequest, expect.getDoRequest());
    }
  }

}
