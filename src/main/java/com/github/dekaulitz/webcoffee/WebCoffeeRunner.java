package com.github.dekaulitz.webcoffee;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.WebCoffee;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebCoffeeRunner {

  private static final String path = "/Users/dekaulitz/projects/webcoffee/config/webcoffee.json";

  public static void main(String[] args) throws IOException {
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
          log.info("coffee scenario {} finished...", s);
        });
      }
    } catch (WebCoffeeException e) {
      e.printStackTrace();
    }

  }
}
