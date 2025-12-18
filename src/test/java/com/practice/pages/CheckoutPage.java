package com.practice.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.practice.utils.CommonMethods;

/**
 * Page object that encapsulates actions needed for adding items to cart and
 * completing checkout. Selectors are chosen to work with the Sauce Demo
 * inventory/cart/checkout pages used by the project.
 */
public class CheckoutPage extends CommonMethods {

    // Using PageFactory @FindBy fields (matching LoginPage style)
    @FindBy(xpath = "//button[normalize-space()='Add to cart']")
    private List<WebElement> addToCartButtons;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartLink;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(className = "complete-header")
    private WebElement completeHeader;

    @FindBy(className = "shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    public CheckoutPage() {
        PageFactory.initElements(getDriver(), this);
    }

    /**
     * Adds a number of random items (2 or 3 as requested) to the cart. If count is
     * larger than available items, it will add all items. Returns the list of
     * product names that were added.
     */
    public List<String> addRandomItems(int count) {
        List<String> added = new ArrayList<>();
        try {
            // wait for add buttons to be present
            getWait().until(driver -> addToCartButtons != null && !addToCartButtons.isEmpty());
            int available = addToCartButtons.size();
            if (available == 0)
                return added;

            // build index list and shuffle
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < available; i++)
                indices.add(i);
            Collections.shuffle(indices, new Random());

            int toPick = Math.min(count, available);
            // read initial badge in a mutable final container so lambdas can update/read it
            final int[] badgeBefore = new int[] { getCartBadgeCount() };
            for (int i = 0; i < toPick; i++) {
                int idx = indices.get(i);
                try {
                    // Find the button (proxy list will locate fresh elements) and wait until
                    // clickable
                    WebElement button = getWait().until(driver -> {
                        try {
                            WebElement el = addToCartButtons.get(idx);
                            if (el != null && el.isDisplayed() && el.isEnabled())
                                return el;
                        } catch (Exception ignored) {
                        }
                        return null;
                    });
                    // record associated product name if available (traverse up to inventory_item)
                    String productName = "";
                    try {
                        WebElement parent = button
                                .findElement(By.xpath("ancestor::div[contains(@class,'inventory_item')]"));
                        WebElement nameEl = parent.findElement(By.className("inventory_item_name"));
                        productName = nameEl.getText().trim();
                    } catch (Exception ignored) {
                    }

                    boolean clickedOk = false;
                    // try up to 2 times to click and confirm
                    for (int attempt = 0; attempt < 2 && !clickedOk; attempt++) {
                        try {
                            // Use shared safeClick to click reliably
                            safeClick(button);

                            // Wait briefly for either the button text to change to 'Remove' or header
                            // badge to increment
                            try {
                                int badgeAfter = badgeBefore[0];
                                try { // get latest badge quickly
                                    badgeAfter = getCartBadgeCount();
                                } catch (Exception ignored) {
                                }

                                WebDriverWait shortWait = getWait(3);
                                // predicate: badge increased or button text equals 'Remove'
                                WebElement finalButton = button;
                                shortWait.until(d -> {
                                    try {
                                        String txt = finalButton.getText();
                                        if (txt != null && txt.trim().equalsIgnoreCase("Remove"))
                                            return true;
                                    } catch (Exception ignored) {
                                    }
                                    try {
                                        int newBadge = getCartBadgeCount();
                                        return newBadge > badgeBefore[0];
                                    } catch (Exception ignored) {
                                    }
                                    return false;
                                });

                                clickedOk = true;
                                // update badgeBefore for next iteration
                                badgeBefore[0] = getCartBadgeCount();
                            } catch (Exception waitEx) {
                                // not confirmed - retry
                                try {
                                    Thread.sleep(120);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        } catch (Exception e) {
                            // swallow and retry
                        }
                    }

                    if (clickedOk) {
                        added.add(productName.isEmpty() ? "item_" + idx : productName);
                    } else {
                        // didn't confirm adding; skip this item
                    }
                } catch (IndexOutOfBoundsException e) {
                    // skip if index no longer valid
                }
            }
        } catch (Exception e) {
            // best-effort; return what we have
        }
        return added;
    }

    public boolean isOrderComplete() {
        try {
            WebElement h = getWait()
                    .until(driver -> completeHeader != null && completeHeader.isDisplayed() ? completeHeader : null);
            return h.isDisplayed() && h.getText().toUpperCase().contains("THANK YOU");
        } catch (Exception e) {
            return false;
        }
    }

    // Return the text shown in the order completion header (or empty string if not
    // present)
    public String getCompletionMessage() {
        try {
            WebElement h = getWait()
                    .until(driver -> completeHeader != null && completeHeader.isDisplayed() ? completeHeader : null);
            return h.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Quick check whether we are on checkout-step-two (used after clicking
     * Continue).
     */
    public boolean isOnCheckoutStepTwo() {
        try {
            return getDriver().getCurrentUrl().contains("checkout-step-two");
        } catch (Exception e) {
            return false;
        }
    }

    // Return number shown in header cart badge, or 0 if absent
    public int getCartBadgeCount() {
        try {
            WebElement b = cartBadge;
            String t = b.getText().trim();
            return Integer.parseInt(t);
        } catch (Exception e) {
            return 0;
        }
    }

    // Open cart and return number of items on the cart page
    public int getCartItemsCountFromCartPage() {
        try {
            // use generic safeClick to open cart
            safeClick(By.className("shopping_cart_link"));
            getWait().until(driver -> cartItems != null && !cartItems.isEmpty());
            return cartItems.size();
        } catch (Exception e) {
            return 0;
        }
    }
}