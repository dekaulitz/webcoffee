package com.github.dekaulitz.webcoffee.executor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;

public interface CoffeeExecutor {

  public void prepare(WebCoffeeSpecs webCoffeeSpecs) throws WebCoffeeException;

  public void setEnvironment(String uri);

  public CoffeeExecutor execute() throws WebCoffeeException;

  public void validate() throws WebCoffeeException, JsonProcessingException;
}
