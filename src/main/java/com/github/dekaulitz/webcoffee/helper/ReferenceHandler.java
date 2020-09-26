package com.github.dekaulitz.webcoffee.helper;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReferenceHandler {

  private String reference;
  private String type;
  private String resourceKey;
  //its type of object like path or schema
  private String objectKey;
  private String path;
  private String httpMethod;
  private String componentKey;
  private String schemasName;
  private String coffeeTestKey;
  private String coffeeTestResponse;
  private String coffeeTestResponseField;


  public static ReferenceHandler getReference(String reference) {
    ReferenceHandler referenceHandler = new ReferenceHandler();
    referenceHandler.reference = reference;
    String[] $ref = extractString(reference);
    referenceHandler.type = $ref[0];
    if ($ref[0].equals(Reference.RESOURCES.reference)) {
      referenceHandler.resourceKey = StringUtils.isBlank($ref[1]) ? "" : $ref[1];
      referenceHandler.objectKey = $ref[2];
      if ($ref[2].equals("paths")) {
        //resources#auth#paths#/v1/auth/doLogin#post
        referenceHandler.path = $ref[3];
        referenceHandler.httpMethod = $ref[4];
      } else if ($ref[2].equals("components")) {
        //resources#auth#components#schemas#LOGIN_REQUEST
        referenceHandler.componentKey = $ref[3];
        referenceHandler.schemasName = $ref[4];
      }
    } else if ($ref[0].equals(Reference.SPECS.reference)) {
      if ($ref.length != 2) {
        throw new WebCoffeeValidationExcepton("invalid reference specs");
      }
      //specs#DO_LOGIN
      referenceHandler.objectKey = $ref[1];
    } else if ($ref[0].equals(Reference.RUNNER.reference)) {
      getReferenceRunner(referenceHandler, $ref);
    }
    return referenceHandler;
  }

  private static void getReferenceRunner(ReferenceHandler referenceHandler, String[] $ref) {
    if ($ref.length != 5) {
      throw new WebCoffeeValidationExcepton("invalid reference runner");
    }
    //runner#coffeTest#requestLogin#response#$refId"
    referenceHandler.objectKey = $ref[1];
    if ($ref[1].equals("coffeeTest")) {
      referenceHandler.coffeeTestKey = $ref[2];
      referenceHandler.coffeeTestResponse = $ref[3];
      if ($ref[3].equals("response")) {
        referenceHandler.coffeeTestResponseField = $ref[4];
      }
    }
  }

  private static String[] extractString(String value) {
    return value.split("#");
  }

  private enum Reference {
    RESOURCES("resources"),
    SPECS("specs"),
    RUNNER("runner");

    private final String reference;

    private Reference(String reference) {
      this.reference = reference;
    }

    public String getReference() {
      return reference;
    }
  }

  private enum OpenApiComponent {
    PATH("paths");

    private final String ok;

    private OpenApiComponent(String reference) {
      this.ok = reference;
    }

    public String getReference() {
      return ok;
    }
  }
}
