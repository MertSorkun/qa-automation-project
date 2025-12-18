package com.practice.steps;

import com.practice.pages.LoginPage;
import com.practice.utils.CommonMethods;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class LogoutSteps extends CommonMethods {

    private LoginPage loginPage = new LoginPage();

    @And("I logout")
    public void i_logout() {
        // click the menu and then the logout link using generic click helper
        click(org.openqa.selenium.By.id("react-burger-menu-btn"));
        click(org.openqa.selenium.By.id("logout_sidebar_link"));
    }

    @Then("I should see the login page")
    public void i_should_see_the_login_page() {
        Assert.assertTrue("Expected to be back on the login page after logout", isVisible(org.openqa.selenium.By.id("user-name")));
    }
}