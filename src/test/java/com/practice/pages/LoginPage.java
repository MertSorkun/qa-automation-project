package com.practice.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.practice.utils.CommonMethods;

public class LoginPage extends CommonMethods {

	@FindBy(id = "user-name")
	WebElement username;

	@FindBy(id = "password")
	WebElement password;

	@FindBy(id = "login-button")
	WebElement login;

	@FindBy(css = "div.error-message-container.error")
	WebElement errorContainer;

	// inventory container id on successful login (use By so CommonMethods can wait on it)
	private By inventoryContainer = By.id("inventory_container");
	

	public LoginPage() {
		PageFactory.initElements(getDriver(), this);
	}

	// convenience method to perform login in one call
	public void login(String user, String pass) {
		// use shared input helper to reliably set values
		setInputValue(username, user);
		setInputValue(password, pass);
		// click login using safeClick helper
		safeClick(login);

		// 3) Fallback: attempt to click in-page modal 'OK' buttons that reference
		// password change text
		try {
			// find elements that look like modals mentioning password change
			java.util.List<WebElement> msgs = getDriver().findElements(By.xpath(
					"//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'change your password') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'password')]"));
			if (!msgs.isEmpty()) {
				try {
					By okLocator = By.xpath(
							"//button[normalize-space()='OK' or normalize-space()='Ok' or normalize-space()='Okay' or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'ok') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close')]");
					safeClick(okLocator);
					System.out.println("[DEBUG] Clicked in-page OK button for password popup");
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ignored) {
		}
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
			// use shared visibility helper with a short timeout
			return isElementVisible(inventoryContainer, 3);
		} catch (Exception e) {
			return false;
		}
	}
}
