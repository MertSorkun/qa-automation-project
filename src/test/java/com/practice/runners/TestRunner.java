package com.practice.runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * TestRunner tells Cucumber: - where feature files are - where step definitions
 * (and Hooks) are - which reports to generate - which tagged scenarios to run
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", // folder for .feature files
		glue = { "com.practice.steps" }, // package for step defs + Hooks
		plugin = { "pretty", // readable console output
				"html:target/cucumber-report.html", // HTML report
				"json:target/cucumber-report.json" // JSON report
		}, monochrome = true, // cleaner console output
		dryRun = false,
		tags = "@checkout" // run only scenarios with @checkout while focusing on checkout
)
public class TestRunner {
	// empty on purpose - Cucumber + JUnit handle everything
}