package com.practice.steps;

import com.practice.pages.CheckoutPage;
import com.practice.pages.LoginPage;
import com.practice.utils.CommonMethods;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.List;
import java.util.Random;

public class CheckoutSteps extends CommonMethods {

    private LoginPage loginPage = new LoginPage();
    private CheckoutPage checkoutPage = new CheckoutPage();

    @Given("I am logged in as {string}")
    public void i_am_logged_in_as(String username) {
        // Assuming hooks already navigated to baseUrl which is the login page
        waitForVisibility(org.openqa.selenium.By.id("user-name"));
        // password for standard_user is stored in config or known default
        String password = "secret_sauce";
        loginPage.login(username, password);
    }

    @When("I add a random set of 2 or 3 items to the cart")
    public void i_add_a_random_set_of_2_or_3_items_to_the_cart() {
        // Choose randomly between 2 and 3
        int pick = new Random().nextBoolean() ? 2 : 3;
        List<String> added = checkoutPage.addRandomItems(pick);
        // at least one should be added
        Assert.assertTrue("Expected to add at least one item", added.size() >= 1);
        // verify header badge shows count (best-effort)
        int badge = checkoutPage.getCartBadgeCount();
        Assert.assertTrue("Expected header cart badge to reflect added items", badge >= 1);
        // Store chosen count in system property so subsequent steps can be aware (not
        // ideal but fine for small test)
        System.setProperty("added.count", String.valueOf(added.size()));

        // Click the cart icon on the top-right to navigate to the cart page (checkout
        // flow starts from cart)
        safeClick(org.openqa.selenium.By.className("shopping_cart_link"));
    }

    @And("I proceed to checkout and complete the order")
    public void i_proceed_to_checkout_and_complete_the_order() {
        // Open the cart and verify items (getCartItemsCountFromCartPage opens the cart)
        int itemsOnCart = checkoutPage.getCartItemsCountFromCartPage();
        Assert.assertTrue("Expected cart to contain at least one item before checkout", itemsOnCart >= 1);

        // Click the Checkout button on the cart page to navigate to checkout-step-one
        safeClick(org.openqa.selenium.By.id("checkout"));

        // Fill the required checkout fields with the exact values requested using shared helper
        setInputValue(org.openqa.selenium.By.id("first-name"), "XXXXXXX");
        setInputValue(org.openqa.selenium.By.id("last-name"), "YYYYYY");
        setInputValue(org.openqa.selenium.By.id("postal-code"), "34343");

        // verify values stuck; retry once if needed
        boolean ok = true;
        try {
            String f = getAttribute(org.openqa.selenium.By.id("first-name"), "value");
            String l = getAttribute(org.openqa.selenium.By.id("last-name"), "value");
            String p = getAttribute(org.openqa.selenium.By.id("postal-code"), "value");
            if (!"XXXXXXX".equals(f) || !"YYYYYY".equals(l) || !"34343".equals(p)) ok = false;
        } catch (Exception ignored) { ok = false; }
        if (!ok) {
            // retry once
            setInputValue(org.openqa.selenium.By.id("first-name"), "XXXXXXX");
            setInputValue(org.openqa.selenium.By.id("last-name"), "YYYYYY");
            setInputValue(org.openqa.selenium.By.id("postal-code"), "34343");
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }

        // Explicitly click Continue on the checkout-step-one page to proceed using shared helper
        // use longer timeout to accommodate slow pages
        clickThenWait(org.openqa.selenium.By.id("continue"), org.openqa.selenium.By.id("finish"), 20);

        // After continue, we leave finishing and verification to the @Then step
    }

    @Then("the order should be completed successfully")
    public void the_order_should_be_completed_successfully() {
        // perform the final Finish click via shared helper and wait for completion header
        clickThenWait(org.openqa.selenium.By.id("finish"), org.openqa.selenium.By.className("complete-header"), 20);

        // allow small wait for completion notice and then assert the exact message
        // requested by the user
        String actual = checkoutPage.getCompletionMessage();
        // The expected text on the confirmation page is exactly: "Thank you for your
        // order!"
        Assert.assertEquals("Expected exact completion message didn't match", "Thank you for your order!", actual);
    }
}
