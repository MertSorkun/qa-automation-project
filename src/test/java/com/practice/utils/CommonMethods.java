package com.practice.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import com.practice.base.BaseClass;
import com.practice.base.Config;

public class CommonMethods extends BaseClass {

	// send text to fields
	public void sendText(WebElement element, String text) {
		element.clear();
		element.sendKeys(text);
	}

	// simple click wrapper
	public void click(WebElement element) {
		// minimal helper: delegate to safeClick which handles JS/Actions fallbacks
		safeClick(element);
	}

	// simple click wrapper by locator
	public void click(By locator) {
		safeClick(locator);
	}

	/**
	 * Click a locator, then wait for another locator to become visible within the
	 * given timeout. This implementation retries a few times, nudges focus off
	 * inputs, and polls for navigation/element.
	 */
	public boolean clickThenWait(By clickLocator, By waitForLocator, int timeoutSeconds) {
		try {
			final int attempts = 6;
			for (int i = 0; i < attempts; i++) {
				try {
					safeClick(clickLocator);
				} catch (Exception ignored) {
				}

				// short polling window to see if navigation/element appeared
				long end = System.currentTimeMillis() + 1200; // 1.2s polling
				while (System.currentTimeMillis() < end) {
					try {
						if (getDriver().getCurrentUrl().contains("checkout-step-two")
								|| !getDriver().findElements(waitForLocator).isEmpty()) {
							return true;
						}
					} catch (Exception ignored) {
					}
					try {
						Thread.sleep(150);
					} catch (InterruptedException ignored) {
					}
				}

				// if not progressed, try nudging focus (use postal-code if available) to
				// trigger client-side validation
				try {
					WebElement postal = getDriver().findElement(By.id("postal-code"));
					try {
						postal.sendKeys(org.openqa.selenium.Keys.TAB);
					} catch (Exception ignored) {
					}
				} catch (Exception ignored) {
				}

				// small pause before retry
				try {
					Thread.sleep(200);
				} catch (InterruptedException ignored) {
				}
			}

			// final attempt: click once and wait with the provided timeout
			try {
				safeClick(clickLocator);
			} catch (Exception ignored) {
			}
			return isVisible(waitForLocator, timeoutSeconds);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Click a locator, then wait for another locator with the default timeout.
	 */
	public boolean clickThenWait(By clickLocator, By waitForLocator) {
		return clickThenWait(clickLocator, waitForLocator, getTimeout());
	}

	// we can add waits, screenshots etc. later as we need them

	/**
	 * Read default timeout from config (key: timeout) or return 10 seconds if
	 * missing/invalid.
	 */
	public int getTimeout() {
		int timeout = 10;
		try {
			String t = Config.get("timeout");
			if (t != null && !t.isEmpty()) {
				timeout = Integer.parseInt(t);
			}
		} catch (Exception ignored) {
		}
		return timeout;
	}

	/**
	 * Return a WebDriverWait configured with the default timeout from config or
	 * 10s.
	 */
	public WebDriverWait getWait() {
		return new WebDriverWait(getDriver(), java.time.Duration.ofSeconds(getTimeout()));
	}

	/**
	 * Return a WebDriverWait configured with a custom timeout in seconds.
	 */
	public WebDriverWait getWait(int seconds) {
		return new WebDriverWait(getDriver(), java.time.Duration.ofSeconds(seconds));
	}

	/**
	 * Safe click on an element: wait until clickable, try JS click then normal
	 * click as fallback.
	 */
	public void safeClick(WebElement el) {
		if (el == null)
			return;
		try {
			getWait().until(driver -> el.isDisplayed() && el.isEnabled());
		} catch (Exception ignored) {
		}
		// try JS click first for robustness
		try {
			((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", el);
			return;
		} catch (Exception ignored) {
		}
		try {
			el.click();
		} catch (Exception e) {
			try {
				new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(el).click().perform();
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Safe click by locator - waits until element is clickable then delegates to
	 * safeClick(WebElement).
	 */
	public void safeClick(By locator) {
		try {
			WebElement el = getWait().until(ExpectedConditions.elementToBeClickable(locator));
			safeClick(el);
		} catch (Exception e) {
			// fallback: try to find element and click quickly
			try {
				WebElement el = getDriver().findElement(locator);
				safeClick(el);
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Minimal visibility check by locator using default timeout.
	 */
	public boolean isVisible(By locator) {
		return isVisible(locator, getTimeout());
	}

	/**
	 * Minimal visibility check by locator with custom timeout.
	 */
	public boolean isVisible(By locator, int timeoutSeconds) {
		try {
			getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Minimal visibility check for a WebElement using default timeout.
	 */
	public boolean isVisible(WebElement element) {
		return isVisible(element, getTimeout());
	}

	/**
	 * Minimal visibility check for a WebElement with custom timeout.
	 */
	public boolean isVisible(WebElement element, int timeoutSeconds) {
		try {
			getWait(timeoutSeconds).until(ExpectedConditions.visibilityOf(element));
			return element.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Robustly set an input's value: focus, clear via JS, sendKeys, dispatch
	 * input/change/blur events.
	 */
	public void setInputValue(WebElement el, String value) {
		if (el == null)
			return;
		try {
			try {
				((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript("arguments[0].focus();", el);
			} catch (Exception ignored) {
			}

			try {
				((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript(
						"arguments[0].value=''; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change'));",
						el);
			} catch (Exception ignored) {
			}

			try {
				Thread.sleep(80);
			} catch (InterruptedException ignored) {
			}

			try {
				el.sendKeys(value);
			} catch (Exception ex) {
				try {
					new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(el).click().sendKeys(value)
							.perform();
				} catch (Exception ignored) {
				}
			}

			try {
				((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript(
						"arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change')); arguments[0].blur();",
						el);
			} catch (Exception ignored) {
			}

			try {
				Thread.sleep(180);
			} catch (InterruptedException ignored) {
			}
		} catch (Exception e) {
			try {
				((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript(
						"arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('input')); arguments[0].dispatchEvent(new Event('change')); arguments[0].blur();",
						el, value);
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Helper to set input value by locator (waits for visibility first).
	 */
	public void setInputValue(By locator, String value) {
		try {
			WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
			setInputValue(el, value);
		} catch (Exception e) {
			try {
				WebElement el = getDriver().findElement(locator);
				setInputValue(el, value);
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Wait for list of elements to be present and return them.
	 */
	public List<WebElement> waitForList(By locator) {
		try {
			return getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
		} catch (Exception e) {
			try {
				return getDriver().findElements(locator);
			} catch (Exception ignored) {
				return java.util.Collections.emptyList();
			}
		}
	}

	/**
	 * Wait until a WebElement is clickable and return it.
	 */
	public WebElement waitForClickable(WebElement el, int timeoutSeconds) {
		try {
			return getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(el));
		} catch (Exception e) {
			return el;
		}
	}

	/**
	 * Return text of element found by locator, or empty string if absent.
	 */
	public String getText(By locator) {
		try {
			WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
			return el.getText().trim();
		} catch (Exception e) {
			try {
				return getDriver().findElement(locator).getText().trim();
			} catch (Exception ignored) {
				return "";
			}
		}
	}

	/**
	 * Return attribute value of element found by locator, or empty string if
	 * absent.
	 */
	public String getAttribute(By locator, String attribute) {
		try {
			WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
			return el.getAttribute(attribute);
		} catch (Exception e) {
			try {
				return getDriver().findElement(locator).getAttribute(attribute);
			} catch (Exception ignored) {
				return null;
			}
		}
	}

	/**
	 * Wait until the element located by the given locator is visible and return it.
	 * Timeout is provided in seconds.
	 */
	public WebElement waitForVisibility(By locator, int timeoutSeconds) {
		WebDriverWait wait = new WebDriverWait(getDriver(), java.time.Duration.ofSeconds(timeoutSeconds));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Wait using the default timeout from config.properties (key: timeout) or 10s
	 * if missing.
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
	 * Take a screenshot of the current browser window and save it under
	 * target/screenshots. The provided name will be combined with a timestamp to
	 * avoid overwriting files. Returns the absolute path to the saved screenshot
	 * file, or null on failure.
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
	 * Capture a screenshot and return the bytes (useful to attach directly to
	 * reports).
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
