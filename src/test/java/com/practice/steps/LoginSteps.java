package com.practice.steps;

import com.practice.utils.CommonMethods;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;



public class LoginSteps extends CommonMethods{
	
	@Given("Navigate to the website")
	public void navigate_to_the_website() throws InterruptedException {
		
		
		System.out.println("Test 1");
	    Thread.sleep(3000);
	}

	@Given("Enter a valid username and password")
	public void enter_a_valid_username_and_password() {
		System.out.println("Test 2");
	}

	@Then("Click on the login button")
	public void click_on_the_login_button() {
		System.out.println("Test 3");
	}

}
