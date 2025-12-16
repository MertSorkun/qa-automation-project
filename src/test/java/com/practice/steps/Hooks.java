package com.practice.steps;

import com.practice.base.BaseClass;
import com.practice.utils.CommonMethods;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.charset.StandardCharsets;

public class Hooks {

    @Before
    public void startScenario() {
        BaseClass.setUp();   // start browser + open URL
    }

    @After
    public void endScenario(Scenario scenario) {
        // Always take a screenshot at the end of each scenario (success or failure)
        CommonMethods helper = new CommonMethods();
        try {
            // save a file for later inspection
            String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
            String saved = helper.takeScreenshot(safeName);
            if (saved != null) {
                scenario.log("Saved screenshot: " + saved);
            }
            // attach bytes so reports can embed the image
            byte[] bytes = helper.takeScreenshotBytes();
            if (bytes != null) {
                scenario.attach(bytes, "image/png", safeName + ".png");
            }
        } catch (Exception e) {
            // best-effort; do not fail teardown
            scenario.log("Failed to capture screenshot: " + e.getMessage());
        }

        if (scenario.isFailed()) {
            // attach additional diagnostics to the Cucumber report (if possible)
            // attach current URL
            try {
                String currentUrl = BaseClass.getDriver().getCurrentUrl();
                scenario.log("Current URL: " + currentUrl);
                scenario.attach(currentUrl.getBytes(StandardCharsets.UTF_8), "text/plain", "current_url.txt");
            } catch (Exception e) {
                // ignore
            }
            // attach page source
            try {
                String pageSource = BaseClass.getDriver().getPageSource();
                scenario.attach(pageSource.getBytes(StandardCharsets.UTF_8), "text/html", "page_source.html");
            } catch (Exception e) {
                // ignore
            }
            // try to attach browser console logs (best-effort)
            try {
                LogEntries logs = BaseClass.getDriver().manage().logs().get(LogType.BROWSER);
                StringBuilder sb = new StringBuilder();
                for (LogEntry entry : logs) {
                    sb.append(new java.util.Date(entry.getTimestamp())).append(" ")
                            .append(entry.getLevel()).append(" ")
                            .append(entry.getMessage()).append("\n");
                }
                if (sb.length() > 0) {
                    scenario.attach(sb.toString().getBytes(StandardCharsets.UTF_8), "text/plain", "console_logs.txt");
                }
            } catch (Exception e) {
                // ignore - not all drivers support logs
            }
            // attach the scenario status
            try {
                if (scenario.getStatus() != null) {
                    scenario.log("Scenario status: " + scenario.getStatus().name());
                }
            } catch (Exception e) {
                // ignore; just best-effort logging
            }
        }
        BaseClass.tearDown(); // quit browser
    }
}