package com.github.dekaulitz.webcoffee.executor.rest;

import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import com.github.dekaulitz.webcoffee.models.schema.WebCoffeeSchema;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeRequestBody;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import okhttp3.MediaType;


public class RequestBuilder {

  public static final MediaType JSON
      = MediaType.get("application/json; charset=utf-8");
  private final WebCoffeeSpecs webCoffeeSpecs;
  public RequestSpecification requestSpecification = RestAssured.given();
  private List<Parameter> parameters;
  @Getter
  private String content = "application/json";
  @Getter
  private ValidatableResponse validateResponse;

  public RequestBuilder(WebCoffeeSpecs webCoffeeSpecs) {
    this.webCoffeeSpecs = webCoffeeSpecs;
    parameters = webCoffeeSpecs.getGiven().getStatements().getParameters();
  }

  public RequestBuilder given() {
    requestSpecification.given().log().all();
    if (parameters.size() != 0) {
      parameters.forEach(parameter -> {
        if (parameter.getIn().equals("headers")) {
          requestSpecification.header(parameter.getName(), parameter.getValue());
        }
        if (parameter.getIn().equals("query")) {
          requestSpecification.queryParam(parameter.getName(), parameter.getValue());
        }
        if (parameter.getIn().equals("path")) {
          requestSpecification.pathParam(parameter.getName(), parameter.getValue());
        }
      });
    }
    if (this.webCoffeeSpecs.getGiven().getStatements().getRequestBody() != null) {
      requestSpecification.body(getPayloadObject(
          this.webCoffeeSpecs.getGiven().getStatements().getRequestBody()));
    }

    return this;
  }

  public RequestBuilder execute(String method, String fullPath) {
    requestSpecification.when();
    switch (method) {
      case "get":
        this.validateResponse = requestSpecification.get(fullPath).then();
        break;
      case "post":
        this.validateResponse = requestSpecification.post(fullPath).then();
        break;
      case "put":
        this.validateResponse = requestSpecification.put(fullPath).then();
        break;
      case "options":
        this.validateResponse = requestSpecification.options(fullPath).then();
        break;
      case "patch":
        this.validateResponse = requestSpecification.patch(fullPath).then();
        break;
    }
    return this;
  }

  private Map<String, Object> getPayloadObject(WebCoffeeRequestBody requestBody) {
    Map<String, Object> body = new HashMap<>();
    Map<String, WebCoffeeSchema> payload = requestBody.getContent().get(content).getPayload()
        .getProperties();
    for (Map.Entry<String, WebCoffeeSchema> entry : payload.entrySet()) {
      body.put(entry.getKey(), entry.getValue().getValue());
    }
    return body;
  }
}
