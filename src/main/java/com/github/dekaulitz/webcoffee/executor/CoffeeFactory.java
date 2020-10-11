package com.github.dekaulitz.webcoffee.executor;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.executor.rest.RestExecutor;
import lombok.Getter;

public class CoffeeFactory {

  @Getter
  private CoffeeExecutor coffeeExecutor;

  public CoffeeExecutor executor(String coffeeMode) throws WebCoffeeException {
    switch (coffeeMode) {
      case "restApi":
        coffeeExecutor = new RestExecutor();
        break;
      default:
        throw new WebCoffeeException("invalid coffeeMode " + coffeeMode);
    }
    return coffeeExecutor;
  }

}
