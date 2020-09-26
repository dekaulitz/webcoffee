package com.github.dekaulitz.webcoffee;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeRunner {

  private static final String path = "/Users/dekaulitz/projects/webcoffee/config/webcoffee3.json";

  public static void main(String[] args) {
    log.info("preparing webcoffee ...");
    try {
      WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
    } catch (WebCoffeeException | JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
