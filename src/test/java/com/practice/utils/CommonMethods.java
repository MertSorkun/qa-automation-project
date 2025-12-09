package com.practice.utils;

import org.openqa.selenium.WebElement;

import com.practice.base.BaseClass;

public class CommonMethods extends BaseClass {

    // send text to fields
    public void sendText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    // simple click wrapper
    public void click(WebElement element) {
        element.click();
    }

    // we can add waits, screenshots etc. later as we need them
}
