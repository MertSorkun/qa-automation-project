package com.practice.utils;

import org.openqa.selenium.WebElement;

import com.practice.base.BaseClass;
import com.practice.base.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    /**
     * Wait until the element located by the given locator is visible and return it.
     * Timeout is provided in seconds.
     */
    public WebElement waitForVisibility(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait using the default timeout from config.properties (key: timeout) or 10s if missing.
     */
    public WebElement waitForVisibility(By locator) {
        int timeout = 10;
        try {
            String t = Config.get("timeout");
            if (t != null && !t.isEmpty()) {
                timeout = Integer.parseInt(t);
            }
        } catch (NumberFormatException ignored) {
        }
        return waitForVisibility(locator, timeout);
    }

    /**
     * Returns true if element becomes visible within timeout, false otherwise.
     */
    public boolean isElementVisible(By locator, int timeoutSeconds) {
        try {
            waitForVisibility(locator, timeoutSeconds);
            return true;
        } catch (NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * Take a screenshot of the current browser window and save it under target/screenshots.
     * The provided name will be combined with a timestamp to avoid overwriting files.
     * Returns the absolute path to the saved screenshot file, or null on failure.
     */
    public String takeScreenshot(String name) {
        try {
            if (!(getDriver() instanceof TakesScreenshot)) {
                return null;
            }
            TakesScreenshot ts = (TakesScreenshot) getDriver();
            File src = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = name + "_" + timestamp + ".png";
            Path screenshotsDir = Paths.get("target", "screenshots");
            Files.createDirectories(screenshotsDir);
            Path dest = screenshotsDir.resolve(fileName);
            Files.copy(src.toPath(), dest);
            return dest.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Capture a screenshot and return the bytes (useful to attach directly to reports).
     */
    public byte[] takeScreenshotBytes() {
        if (!(getDriver() instanceof TakesScreenshot)) {
            return null;
        }
        try {
            TakesScreenshot ts = (TakesScreenshot) getDriver();
            return ts.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}