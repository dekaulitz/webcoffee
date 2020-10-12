package com.github.dekaulitz.webcoffee.helper;

import com.github.dekaulitz.webcoffee.model.runner.WebCoffeeArgumentsRunner;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RequestHelper {

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
}
