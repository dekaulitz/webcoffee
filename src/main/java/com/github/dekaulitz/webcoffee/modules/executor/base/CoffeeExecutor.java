package com.github.dekaulitz.webcoffee.modules.executor.base;


import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeRunnerEnv;

public interface CoffeeExecutor {

  CoffeeExecutor prepare(WebCoffeeRunnerEnv webCoffeeRunnerEnv);

  void execute();
}
