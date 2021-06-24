package options;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;


@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty", "html:target/cucumber-reports.html" },
		glue = {"stepdefs"},
		features = {"src/test/features"},
		monochrome = true)
public class CucumberTests {}