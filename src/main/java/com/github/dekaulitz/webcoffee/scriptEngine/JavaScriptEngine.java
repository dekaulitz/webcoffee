package com.github.dekaulitz.webcoffee.scriptEngine;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScriptEngine implements ScriptEngine {

  private final WebCoffeeArgumentsRunner argument;

  public JavaScriptEngine(
      WebCoffeeArgumentsRunner argument) {
    this.argument = argument;
  }

  @Override
  public Object execute() {
    try {
      javax.script.ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");
      engine.eval(new FileReader(argument.getSrc()));
      Invocable invocable = (Invocable) engine;
      return invocable.invokeFunction(argument.getArgumentName());
    } catch (ScriptException | FileNotFoundException | NoSuchMethodException e) {
      e.printStackTrace();
      throw new WebCoffeeValidationExcepton(
          e.getMessage() + " when execute argument " + argument.toString());
    }
  }
}
