package com.github.dekaulitz.webcoffee.executor.web;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.executor.rest.RestExecutor;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;

public class WebExecutor implements CoffeeExecutor {

  @Override
  public void prepare(WebCoffeeSpecs webCoffeeSpecs) throws WebCoffeeException {

  }

  @Override
  public void setEnvironment(String uri) {

  }

  @Override
  public RestExecutor execute() throws WebCoffeeException {

    return null;
  }

  @Override
  public void validate() throws WebCoffeeException {

  }
}
