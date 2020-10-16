package com.github.dekaulitz.webcoffee.runner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.CoffeeFactory;
import com.github.dekaulitz.webcoffee.model.WebCoffee;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeTestRequest;
import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeTestRequest.StatusTest;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class RunnerTest {

  private static final String path = "/Users/dekaulitz/projects/webcoffee/config/config.json";

  @Test
  void main() {
    System.setProperty("nashorn.args", "--language=es6");
    log.info("preparing webCoffee ...");
    WebCoffee webCoffee = new WebCoffee();
    try {
      WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
      webCoffee = webCoffeeParser.getWebCoffee();
      CoffeeFactory coffeeFactory = new CoffeeFactory();
      coffeeFactory.executor(webCoffee.getRunner().getMode())
          .prepare(webCoffee.getRunner())
          .execute();

    } catch (WebCoffeeException | JsonProcessingException | AssertionError e) {
      log.error(e);
    }
    boolean isSuccess = false;
    AtomicInteger totalUseCase = new AtomicInteger(webCoffee.getRunner().getCoffeeTest().size());
    log.info("==================================");
    log.info("WEB COFFEE STATUS");
    webCoffee.getRunner().getCoffeeTest().entrySet().stream()
        .sorted(Comparator.comparing(o -> o.getValue().getOrder()))
        .forEach(entry -> {
          WebCoffeeTestRequest webCoffeeTestRequest = entry
              .getValue();
          log.info("{} status {} time {}", entry.getKey(), webCoffeeTestRequest.getStatus(),
              webCoffeeTestRequest.getExecutionTime());
          if (webCoffeeTestRequest.getStatus().equals(StatusTest.SUCCESS)) {
            totalUseCase.decrementAndGet();
          }
        });
    if (totalUseCase.get() == 0) {
      isSuccess = true;
    }
    log.info("==================================");
    log.info("webCoffee {}", isSuccess);
  }
}
