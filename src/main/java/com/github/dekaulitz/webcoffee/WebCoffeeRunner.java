package com.github.dekaulitz.webcoffee;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import java.util.Map;
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
      Map<String, WebCoffeeArgumentsRunner> arguments = webCoffee
          .getRunner().getArguments();
      arguments.forEach((s, webCoffeeArgumentsRunner) -> {
        System.out.println(s + " => " + webCoffeeArgumentsRunner.getValue());
      });
    } catch (WebCoffeeException | JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
