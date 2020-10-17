package com.github.dekaulitz.webcoffee.modules.executor;


import com.github.dekaulitz.webcoffee.modules.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.modules.executor.rest.RestExecutor;
import lombok.Getter;

public class CoffeeFactory {

  @Getter
  private CoffeeExecutor coffeeExecutor;

  public CoffeeExecutor executor(String coffeeMode) {
    switch (coffeeMode) {
      case "restApi":
        coffeeExecutor = new RestExecutor();
        break;
      default:
        throw new RuntimeException("invalid coffeeMode " + coffeeMode);
    }
    return coffeeExecutor;
  }

}
