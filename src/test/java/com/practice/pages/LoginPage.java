package com.practice.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.practice.base.BaseClass;
import org.openqa.selenium.By;
import com.practice.base.Config;

public class LoginPage extends BaseClass{

    
    @FindBy(id = "user-name")
    WebElement username;
    
    @FindBy(id = "password")
    WebElement password;
    
    @FindBy(id = "login-button")
    WebElement login;
    
    @FindBy(css = "div.error-message-container.error")
    WebElement errorContainer;

    // inventory container id on successful login
    private By inventoryContainer = By.id("inventory_container");

    // menu button and logout link used on the inventory page
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");

    public LoginPage() {
        PageFactory.initElements(BaseClass.getDriver(), this);
    }
    // convenience method to perform login in one call
    public void login(String user, String pass) {
        username.clear();
        username.sendKeys(user);
        password.clear();
        password.sendKeys(pass);
        login.click();
    }

    public String getErrorMessage() {
        try {
            return errorContainer.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isInventoryVisible() {
        try {
            return getDriver().findElement(inventoryContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Click the menu and then logout using explicit waits for reliability
    public void clickMenuAndLogout() {
        try {
            int timeout = 10;
            try {
                String t = Config.get("timeout");
                if (t != null && !t.isEmpty()) timeout = Integer.parseInt(t);
            } catch (Exception ignored) {}

            org.openqa.selenium.support.ui.WebDriverWait wait =
                    new org.openqa.selenium.support.ui.WebDriverWait(getDriver(), java.time.Duration.ofSeconds(timeout));

            // ensure we're on the inventory page before opening the menu
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(inventoryContainer));

            // click menu using JS in case normal click is intercepted
            org.openqa.selenium.WebElement menu =
                    wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(menuButton));
            ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", menu);

            // wait for logout link and click via JS
            org.openqa.selenium.WebElement logout =
                    wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(logoutLink));
            ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", logout);

            // wait until username field is visible on the login page (logout should navigate back)
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        } catch (Exception e) {
            // ignore - caller will verify resulting state
        }
    }

    // Returns true if the login form elements are visible (we assume this means we're on the login page)
    public boolean isLoginFormVisible() {
        try {
            // quick explicit wait to allow page to settle
            org.openqa.selenium.support.ui.WebDriverWait wait =
                    new org.openqa.selenium.support.ui.WebDriverWait(getDriver(), java.time.Duration.ofSeconds(5));
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
            return username.isDisplayed() && password.isDisplayed() && login.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}