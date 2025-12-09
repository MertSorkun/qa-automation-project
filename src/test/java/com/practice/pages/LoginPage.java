package com.practice.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.practice.base.BaseClass;

public class LoginPage extends BaseClass{

	
	@FindBy(id = "user-name")
	WebElement username;
	
	@FindBy(id = "password")
	WebElement password;
	
	@FindBy(id = "login-button")
	WebElement login;
	
	public LoginPage() {
        PageFactory.initElements(BaseClass.getDriver(), this);
    }
}
