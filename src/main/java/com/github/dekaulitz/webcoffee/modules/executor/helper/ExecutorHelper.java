package com.github.dekaulitz.webcoffee.modules.executor.helper;

import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeArgumentsRunner;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ExecutorHelper {

  public static String getArgumentValue(final String argument,
      final Map<String, WebCoffeeArgumentsRunner> globalArgument,
      final Map<String, WebCoffeeArgumentsRunner> arguments) {
    String value = "";
    WebCoffeeArgumentsRunner localArgument = arguments.get(argument);
    if (localArgument != null) {
      value = localArgument.getValue().toString();
    } else {
      WebCoffeeArgumentsRunner globalArguments = globalArgument.get(argument);
      if (globalArguments != null) {
        value = globalArguments.getValue().toString();
      }

    }
    return value;
  }

  public static String getPreSuffix(String prefix, String suffix, String value) {
    if (!StringUtils.isNoneEmpty(prefix) && !StringUtils.isNoneEmpty(suffix)) {
      return value;
    }
    String result = "";
    if (StringUtils.isNoneEmpty(prefix)) {
      result = prefix + value;
    }
    if (StringUtils.isNoneEmpty(suffix)) {
      result = value + suffix;
    }
    return result;
  }


  public static String getEndTime(long endTIme) {

    long minutes = (endTIme / 1000) / 60;
    long seconds = (endTIme / 1000) % 60;
    return String.format("finished on %d minutes %d seconds",minutes,seconds);
  }
}
