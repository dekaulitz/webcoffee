package com.github.dekaulitz.webcoffee;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.CoffeeFactory;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeRunner {

  private static final String path = "/Users/dekaulitz/projects/webcoffee/config/webcoffee4.json";

  public static void main(String[] args) {
    System.setProperty("nashorn.args", "--language=es6");
    log.info("preparing webcoffee ...");
    try {
      WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
      WebCoffee webCoffee = webCoffeeParser.getWebCoffee();
      CoffeeFactory coffeeFactory = new CoffeeFactory();
      long start = System.currentTimeMillis();

      for (int i = 0; i < 1000; i++) {
        coffeeFactory.executor(webCoffee.getRunner().getMode())
            .prepare(webCoffee.getRunner())
            .execute();
      }
      System.out.println((System.currentTimeMillis() - start)/1000);
    } catch (WebCoffeeException | JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
