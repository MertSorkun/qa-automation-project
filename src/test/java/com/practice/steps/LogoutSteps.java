package com.practice.steps;

import com.practice.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class LogoutSteps {

    private LoginPage loginPage = new LoginPage();

    @And("I logout")
    public void i_logout() {
        loginPage.clickMenuAndLogout();
    }

    @Then("I should see the login page")
    public void i_should_see_the_login_page() {
        Assert.assertTrue("Expected to be back on the login page after logout", loginPage.isLoginFormVisible());
    }
}
