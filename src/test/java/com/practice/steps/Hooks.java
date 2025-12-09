package com.practice.steps;

import com.practice.base.BaseClass;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void startScenario() {
        BaseClass.setUp();   // start browser + open URL
    }

    @After
    public void endScenario(Scenario scenario) {
        if (scenario.isFailed()) {
          //  CommonMethods.takeScreenshot(scenario.getName().replace(" ", "_"));
        }
        BaseClass.tearDown(); // quit browser
    }
}
