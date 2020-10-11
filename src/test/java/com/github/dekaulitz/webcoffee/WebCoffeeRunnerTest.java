package com.github.dekaulitz.webcoffee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.CoffeeFactory;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class WebCoffeeRunnerTest {

  private static final String path = "/Users/dekaulitz/projects/webcoffee/config/config.json";

  @Test
  void cobaString() {
    System.setProperty("nashorn.args", "--language=es6");
    log.info("preparing webcoffee ...");
    try {
      WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
      WebCoffee webCoffee = webCoffeeParser.getWebCoffee();
      CoffeeFactory coffeeFactory = new CoffeeFactory();
      long start = System.currentTimeMillis();
      coffeeFactory.executor(webCoffee.getRunner().getMode())
          .prepare(webCoffee.getRunner())
          .execute();
      System.out.println((System.currentTimeMillis() - start) / 1000);
    } catch (WebCoffeeException | JsonProcessingException e) {
      e.printStackTrace();
    }
  }

//  @Test
//  void main() {
//    log.info("preparing webcoffee ...");
//    WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
//    try {
//      WebCoffee webCoffee = webCoffeeParser.getWebCoffeeResult();
//      if (!webCoffeeParser.getMessage().isEmpty()) {
//        log.warn("something wrong with you're configuration");
//      } else {
//        log.info("configuration loaded starting to coffee {}", webCoffee.getInfo().getTitle());
//        log.info("load template using version {}", webCoffee.getWebCoffee());
//        webCoffee.getSpecs().forEach((usecase, webCoffeeComponent) -> {
//          log.info("starting to coffee scenario {} ...", usecase);
//          try {
//            if (!webCoffeeComponent.isSkip()) {
//              CoffeeFactory coffeeFactory = new CoffeeFactory();
//              coffeeFactory.executor(webCoffee.getRunner().getMode());
//              coffeeFactory.getCoffeeExecutor().setEnvironment(webCoffee.getRunner());
//              coffeeFactory.getCoffeeExecutor().prepare(usecase, webCoffeeComponent);
//              coffeeFactory.getCoffeeExecutor().execute().validate();
//            }
//          } catch (WebCoffeeException | JsonProcessingException e) {
//            e.printStackTrace();
//          }
//          log.info("coffee scenario {} finished...", usecase);
//        });
//        log.info("webcoffee total usecases {}",
//            webCoffee.getSpecs().values().stream().filter(webCoffeeSpecs ->
//                !webCoffeeSpecs.isSkip()).count());
//      }
//    } catch (WebCoffeeException e) {
//      e.printStackTrace();
//    }
//  }
}
