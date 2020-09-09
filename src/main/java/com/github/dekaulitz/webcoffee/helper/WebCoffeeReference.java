package com.github.dekaulitz.webcoffee.helper;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WebCoffeeReference {

  private String httpMethod;
  private String schema;
  private String reference;
  private String referenceKey;
  private String referenceType;

  public static WebCoffeeReference getReference(String $ref) throws WebCoffeeException {
    //"$ref":"auth#paths#/v1/auth/dologin#post"
    //"$ref":"auth#components#schemas#GENERATE_OTP",
    WebCoffeeReference webCoffeeReference = new WebCoffeeReference();
    String[] ref = extractString($ref);
    if (ref.length != 4) {
      throw new WebCoffeeException("invalid reference");
    }
    webCoffeeReference.setReferenceKey(ref[0]);
    webCoffeeReference.setReference(ref[2]);
    if (ref[1].toUpperCase().equals(Reference.PATHS.toString())) {
      webCoffeeReference.setReferenceType("paths");
      webCoffeeReference.setHttpMethod(ref[3]);
    }
    if (ref[1].toUpperCase().equals(Reference.COMPONENTS.toString())) {
      webCoffeeReference.setReferenceType("components");
      webCoffeeReference.setSchema(ref[3]);
    }
    return webCoffeeReference;
  }

  private static String[] extractString(String value) {
    return value.split("#");
  }

  enum Reference {
    COMPONENTS, PATHS,
  }
}
