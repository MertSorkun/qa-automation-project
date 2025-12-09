package com.practice.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.practice.pages.LoginPage;

public class BaseClass {

	protected static WebDriver driver;

	// page objects accessible everywhere

	public static LoginPage loginPage;

	public static void setUp() {
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(Config.get("baseUrl")); // landing page

		// initialize page objects AFTER driver is created
		loginPage = new LoginPage();
	}

	public static WebDriver getDriver() {
		return driver;
	}

	public static void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
