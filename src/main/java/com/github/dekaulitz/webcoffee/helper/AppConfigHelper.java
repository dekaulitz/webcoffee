package com.github.dekaulitz.webcoffee.helper;

import java.io.IOException;
import java.util.Properties;
import lombok.Getter;

public class AppConfigHelper {

  private static AppConfigHelper instance;
  @Getter
  private Properties properties;

  public static AppConfigHelper getInstance() throws IOException {
    if (instance == null) {
      final Properties properties = new Properties();
      properties.load(Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("application.properties"));
      AppConfigHelper appConfigHelper = new AppConfigHelper();
      appConfigHelper.properties = properties;
      instance = appConfigHelper;
    }
    return instance;
  }
}
