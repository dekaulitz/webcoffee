package com.github.dekaulitz.webcoffee.executor.web;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.executor.rest.RestExecutor;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;

public class WebExecutor implements CoffeeExecutor {

  @Override
  public void prepare(String usecase, WebCoffeeSpecs webCoffeeSpecs) throws WebCoffeeException {

  }

  @Override
  public void setEnvironment(WebCoffeeRunnerEnv runner) {

  }

  @Override
  public RestExecutor execute() throws WebCoffeeException {

    return null;
  }

  @Override
  public void validate() throws WebCoffeeException {

  }
}
