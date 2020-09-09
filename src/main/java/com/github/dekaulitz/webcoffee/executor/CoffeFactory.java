package com.github.dekaulitz.webcoffee.executor;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.executor.rest.RestExecutor;
import com.github.dekaulitz.webcoffee.executor.web.WebExecutor;
import lombok.Getter;

public class CoffeFactory {

  @Getter
  private CoffeeExecutor coffeeExecutor;

  public CoffeeExecutor executor(CoffeeMode coffeeMode) throws WebCoffeeException {
    switch (coffeeMode) {
      case REST_API:
        coffeeExecutor = new RestExecutor();
        break;
      case WEB_BROWSER:
        coffeeExecutor = new WebExecutor();
        break;
      default:
        throw new WebCoffeeException("invalid coffeeMode " + coffeeMode);
    }
    return coffeeExecutor;
  }

  enum CoffeeMode {
    REST_API, WEB_BROWSER, ANDROID_APP, IOS_APP;
  }
}
