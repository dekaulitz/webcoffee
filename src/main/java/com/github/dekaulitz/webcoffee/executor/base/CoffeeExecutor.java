package com.github.dekaulitz.webcoffee.executor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;

public interface CoffeeExecutor {

  public void prepare(String usecase, WebCoffeeSpecs webCoffeeSpecs) throws WebCoffeeException;

  public CoffeeExecutor execute() throws WebCoffeeException;

  public void validate() throws WebCoffeeException, JsonProcessingException;

  public void setEnvironment(WebCoffeeRunnerEnv runner);
}
