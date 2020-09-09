package com.github.dekaulitz.webcoffee;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.rest.RestExecutor;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class WebCoffeeRunnerTest {

  private static final String path = "/Users/dekaulitz/projects/webcoffee/config/webcoffee.json";

  @Test
  void main() {
    log.info("preparing webcoffee ...");
    WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
    try {
      WebCoffee webCoffee = webCoffeeParser.getWebCoffeeResult();
      if (!webCoffeeParser.getMessage().isEmpty()) {
        log.warn("something wrong with you're configuration");
      } else {
        log.info("configuration loaded starting to coffee {}", webCoffee.getInfo().getTitle());
        log.info("load template using version {}", webCoffee.getWebCoffee());
        webCoffee.getSpecs().forEach((s, webCoffeeComponent) -> {
          log.info("starting to coffee scenario {} ...", s);
          RestExecutor restExecutor = new RestExecutor();
          try {
            restExecutor.setEnvironment(webCoffee.getEnvironment().get("development").getUrl());
            restExecutor.prepare(webCoffeeComponent);
            restExecutor.execute().validate();
            Thread.sleep(1000);
          } catch (WebCoffeeException | InterruptedException e) {
            e.printStackTrace();
          }
          log.info("coffee scenario {} finished...", s);
        });
        log.info("webcoffee total usecases {}", webCoffee.getSpecs().size());

      }
    } catch (WebCoffeeException e) {
      e.printStackTrace();
    }
  }
}
