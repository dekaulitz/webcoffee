package com.github.dekaulitz.webcoffee.executor.rest;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;

@Log4j2
public class RestExecutor implements CoffeeExecutor {

  protected String mediaType = "application/json";
  protected RequestBuilder requestBuilder;
  protected String endpoint;
  protected String host;
  protected String method;
  protected WebCoffeeSpecs webCoffeeSpecs;

  public void prepare(WebCoffeeSpecs webCoffeeSpecs) throws WebCoffeeException {
    this.webCoffeeSpecs = webCoffeeSpecs;
    this.endpoint = webCoffeeSpecs.getGiven().getPathEndpoint();
    this.method = webCoffeeSpecs.getGiven().getHttpMethod();
  }

  @Override
  public void setEnvironment(String host) {
    this.host = host;
  }

  /**
   * @return
   * @throws WebCoffeeException
   */
  @Override
  public RestExecutor execute() throws WebCoffeeException {
    this.requestBuilder = new RequestBuilder(this.webCoffeeSpecs);
    this.requestBuilder.given()
        .execute(this.method, this.host + endpoint);
    return this;
  }

  @Override
  public void validate() throws WebCoffeeException {
    Response assertations = this.requestBuilder
        .getValidateResponse().log().all()
        .extract().response();
    Assert.assertEquals("statusCode is failing on " + this.endpoint,
        this.webCoffeeSpecs.getExpect().getHttpCode(), assertations.statusCode());
    Assert.assertTrue("schema response failing", SchemaValidator
        .validateSchema(this.webCoffeeSpecs.getExpect().getContent().get("application/json")
            .getRawSchema(), assertations.body().asString()));
  }

}
