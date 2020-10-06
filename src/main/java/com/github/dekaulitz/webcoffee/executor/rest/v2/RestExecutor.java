package com.github.dekaulitz.webcoffee.executor.rest.v2;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeTestRequest;
import io.restassured.response.Response;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class RestExecutor implements CoffeeExecutor {

  private String host;
  private Map<String, WebCoffeeArgumentsRunner> arguments;
  private Map<String, WebCoffeeTestRequest> coffeeTest;

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
      RequestBuilder requestBuilder = RequestBuilder
          .initRequest(webCoffeeTestRequest.getDoRequest());
      if (StringUtils.isNoneEmpty(webCoffeeTestRequest.getHost())) {
        requestBuilder.setHost(webCoffeeTestRequest.getHost());
      } else if (StringUtils.isNoneEmpty(this.host)) {
        requestBuilder.setHost(this.host);
      }
      requestBuilder.setGlobalArguments(this.arguments);
      requestBuilder.setArguments(webCoffeeTestRequest.getArguments());
      requestBuilder.createRequest();
      requestBuilder.validate();
      requestBuilder.execute();
      Response response = requestBuilder
          .getValidateResponse().log().all()
          .extract().response();
      log.info(response.statusCode());
    });
  }
}
