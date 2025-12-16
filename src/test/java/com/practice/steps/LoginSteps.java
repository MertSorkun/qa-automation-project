package com.practice.steps;

import com.practice.utils.CommonMethods;
import com.practice.base.Config;
import com.practice.base.BaseClass;
import com.practice.pages.LoginPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.junit.Assert;


public class LoginSteps extends CommonMethods{

    private LoginPage loginPage = new LoginPage();

    @Given("I open the login page")
    public void i_open_the_login_page() {
        // BaseClass.setUp() is called in Hooks @Before, so driver should already be at baseUrl
        // Wait for the login username to be visible
        waitForVisibility(By.id("user-name"));
    }

    @When("I login with {string} and {string}")
    public void i_login_with_and(String username, String password) {
        // Support passing empty strings as null-like values — login helper accepts empty strings
        loginPage.login(username == null ? "" : username, password == null ? "" : password);
    }

    @Then("I should see {string}")
    public void i_should_see(String result) {
        if ("success".equalsIgnoreCase(result)) {
            Assert.assertTrue("Expected to be on inventory page after successful login", loginPage.isInventoryVisible());
        } else {
            Assert.assertFalse("Expected NOT to be on inventory page after failed login", loginPage.isInventoryVisible());
        }
    }

    @Then("the error message should be {string}")
    public void the_error_message_should_be(String expected) {
        String actual = loginPage.getErrorMessage();
        // Normalize spaces and compare
        Assert.assertEquals("Error message should match", expected.trim(), actual.trim());
    }

}