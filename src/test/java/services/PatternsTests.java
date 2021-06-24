package services;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import junitparams.JUnitParamsRunner;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.NodeChildren;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@RunWith(JUnitParamsRunner.class)
public class PatternsTests {
	private static String ENDPOINT_GET_PATTERNS = "patterns";
	private static RequestSpecification requestSpec;
	@Parameter(0)
	private static int numViewsMin;

	@BeforeClass
	public static void createRequestSpecification() throws IOException {

		requestSpec = new RequestSpecBuilder().setBaseUri("http://www.colourlovers.com/api/")
				.addHeader("User-Agent", "PostmanRuntime/7.6.0").build();
		try {
			numViewsMin = readNumViewsMin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetPatterns() {
		Response response = given().log().all().spec(requestSpec).when().get(ENDPOINT_GET_PATTERNS).then().extract()
				.response();
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
		XmlPath xmlPath = new XmlPath(response.asString());
		NodeChildren patternNodes = xmlPath.getNodeChildren("patterns.pattern");
		int patternsSize = patternNodes.size();
		System.out.println("Size:" + patternsSize);
		// xml get attributes
		for (int i = 0; i < patternsSize; i++) {
			int nViews = Integer.parseInt(xmlPath.get("patterns.pattern[" + i + "].numViews"));
			String pID = xmlPath.get("patterns.pattern[" + i + "].id");
			System.out.println(i + 1 + "- Pattern ID: " + pID + " with nViews: " + nViews);
			Assert.assertTrue("Pattern with id = " + pID + " has a number of views = " + nViews
					+ " less than inserted miniumum number {" + numViewsMin + "}", nViews > numViewsMin);
		}
	}

	@Parameters
	public static int readNumViewsMin() throws IOException {
		String path = new File("src/resources/DataSheet.xlsx").getAbsolutePath();
		// Path of the excel file
		FileInputStream fs = new FileInputStream(path);
		// Creating a workbook
		XSSFWorkbook workbook = new XSSFWorkbook(fs);
		XSSFSheet sheet = workbook.getSheetAt(0);
		System.out.println(sheet.getRow(0).getCell(0) + " : " + sheet.getRow(1).getCell(0));
		int nViewsMin = (int) sheet.getRow(1).getCell(0).getNumericCellValue();
		workbook.close();
		return nViewsMin;
	}
}
