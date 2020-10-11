package com.github.dekaulitz.webcoffee.executor.base;


import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeRunnerEnv;

public interface CoffeeExecutor {

  CoffeeExecutor prepare(WebCoffeeRunnerEnv webCoffeeRunnerEnv);

  void execute();
}
