package com.github.dekaulitz.webcoffee;

import java.lang.annotation.Annotation;

public class WebCoffeeApplication {


  public static void run(Class<?> primarySources, String[] args) {
    Annotation[] test = primarySources.getAnnotations();
    System.out.println(test);
    System.out.println(args);
  }
}
