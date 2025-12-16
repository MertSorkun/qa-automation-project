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

    // Click the menu and then logout (best-effort; waits briefly)
    public void clickMenuAndLogout() {
        try {
            getDriver().findElement(menuButton).click();
            // small sleep to allow menu animation (more robust approach would use explicit wait)
            Thread.sleep(300);
            getDriver().findElement(logoutLink).click();
        } catch (Exception e) {
            // ignore - caller will verify resulting state
        }
    }

    // Returns true if the login form elements are visible (we assume this means we're on the login page)
    public boolean isLoginFormVisible() {
        try {
            return username.isDisplayed() && password.isDisplayed() && login.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}