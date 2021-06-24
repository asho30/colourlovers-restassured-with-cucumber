package stepdefs;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.Assert;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.NodeChildren;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class PatternsDefinitions {

	private Response response;
	private RequestSpecification request;
	private XmlPath xmlPath;
	private int patternsSize;

	private String ENDPOINT_GET_PATTERNS = "http://www.colourlovers.com/api/patterns";

	@Given("get patterns api")
	public void getPatterns() {
		request = given().log().all();
		request.header("User-Agent", "PostmanRuntime/7.6.0");

	}

	@When("a user retrieves the patterns")
	public void a_user_retrieves_the_patterns() {
		response = request.when().get(ENDPOINT_GET_PATTERNS);
		System.out.println("response: " + response.prettyPrint());
		xmlPath = new XmlPath(response.asString());
		NodeChildren patternNodes = xmlPath.getNodeChildren("patterns.pattern");
		patternsSize = patternNodes.size();
		System.out.println("Size:" + patternsSize);
	}

	@Then("verify successful request")
	public void verify_successful_request() {
		response.then().log().all().statusCode(HttpStatus.SC_OK);
	}

	@And("the number of views for each pattern in response should be greater than {int}")
	public void the_number_of_views_for_each_pattern_in_response_should_be_greater_than_numViewsMin(int numViewsMin) {
		// get attributes
		for (int i = 0; i < patternsSize; i++) {
			int nViews = Integer.parseInt(xmlPath.get("patterns.pattern[" + i + "].numViews"));
			String pID = xmlPath.get("patterns.pattern[" + i + "].id");
			System.out.println(i + 1 + "- Pattern ID: " + pID + " with nViews: " + nViews);
			Assert.assertTrue("Pattern with id = " + pID + " has a number of views = " + nViews
					+ " less than inserted miniumum number {" + numViewsMin + "}", nViews > numViewsMin);
		}
	}
}
