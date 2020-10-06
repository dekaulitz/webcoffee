package com.github.dekaulitz.webcoffee.executor.base;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;

public interface CoffeeExecutor {

  CoffeeExecutor prepare(WebCoffeeRunnerEnv webCoffeeRunnerEnv) throws WebCoffeeException;

  void execute() throws WebCoffeeException;
}
