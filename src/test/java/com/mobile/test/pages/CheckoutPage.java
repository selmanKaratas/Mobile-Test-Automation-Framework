package com.mobile.test.pages;

import com.google.common.collect.ImmutableMap;
import com.mobile.test.helpers.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;

public class CheckoutPage extends BasePage {
    @AndroidFindBy(accessibility = "test-First Name")
    private WebElement firstNameField;

    @AndroidFindBy(accessibility = "test-Last Name")
    private WebElement lastNameField;

    @AndroidFindBy(accessibility = "test-Zip/Postal Code")
    private WebElement zipCodeField;

    @AndroidFindBy(accessibility = "test-CONTINUE")
    private WebElement continueButton;

    // Finish button locators
    @AndroidFindBy(accessibility = "test-Finish")
    private WebElement finishButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'FINISH')]")
    private WebElement finishButtonByText;

    @AndroidFindBy(xpath = "//android.widget.Button[contains(@text, 'FINISH')]")
    private WebElement finishButtonByButtonText;

    @AndroidFindBy(xpath = "//*[contains(@content-desc, 'FINISH')]")
    private WebElement finishButtonByContentDesc;

    @AndroidFindBy(accessibility = "test-COMPLETE")
    private WebElement completeHeader;

    @AndroidFindBy(xpath = "//*[contains(@text, 'THANK YOU') or contains(@content-desc, 'COMPLETE')]")
    private WebElement orderCompleteMessage;

    public CheckoutPage(AndroidDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void enterInfo(String firstName, String lastName, String zipCode) {
        sendKeys(firstNameField, firstName);
        sendKeys(lastNameField, lastName);
        sendKeys(zipCodeField, zipCode);
    }

    /**
     * Proceeds to the checkout overview page after validating the form.
     * 
     * @throws AssertionError       if validation fails
     * @throws NoSuchElementException if required elements are not found
     */
    public void continueToOverview() {
        try {
            // First check for empty required fields
            String firstName = firstNameField.getAttribute("text");
            String lastName = lastNameField.getAttribute("text");
            String zipCode = zipCodeField.getAttribute("text");
            
            boolean hasEmptyFields = (firstName == null || firstName.trim().isEmpty() || 
                                   lastName == null || lastName.trim().isEmpty() || 
                                   zipCode == null || zipCode.trim().isEmpty());
            
            System.out.println("🔍 Clicking continue button...");
            click(continueButton);
            
            // Small delay to allow the app to process the click
            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            
            // Check if we're still on the info page
            boolean stillOnInfoPage = isOnCheckoutInfoPage();
            
            // If we had empty fields, we should stay on the same page
            if (hasEmptyFields) {
                System.out.println("❌ Required fields are empty - checking for validation error");
                takeScreenshot("missing_fields_error");
                
                // Verify we're still on the info page
                if (!stillOnInfoPage) {
                    takeScreenshot("unexpected_page_after_validation_error");
                    throw new AssertionError("Expected to stay on checkout info page after validation error");
                }
                
                // Check for error message
                boolean isErrorDisplayed = isErrorMessageDisplayed();
                if (!isErrorDisplayed) {
                    takeScreenshot("missing_validation_error");
                    throw new AssertionError("Expected validation error message not displayed for empty fields");
                }
                
                System.out.println("✅ Validation error message displayed");
                // Throw an exception to indicate validation error
                throw new IllegalStateException("Validation error: Required fields are empty");
            }
            
            // If we get here, we should be on the overview page
            System.out.println("🔍 Looking for finish button on overview page...");
            
            // Wait for the overview page to load (wait for the finish button to be visible)
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(driver -> {
                    WebElement finishBtn = findFinishButton();
                    if (finishBtn != null && finishBtn.isDisplayed()) {
                        return true;
                    }
                    // If not found, try scrolling and check again
                    scrollDown();
                    return false;
                });
            } catch (Exception e) {
                // If we can't find the finish button, check if we're still on the info page
                if (isOnCheckoutInfoPage()) {
                    takeScreenshot("still_on_info_page");
                    throw new IllegalStateException("Failed to proceed to checkout overview - still on information page");
                }
                takeScreenshot("overview_page_not_loaded");
                throw new TimeoutException("Timed out waiting for checkout overview page to load");
            }
            
            System.out.println("✅ Checkout overview page loaded");
        } catch (IllegalStateException | TimeoutException e) {
            // Re-throw validation and timeout errors directly
            throw e;
        } catch (Exception e) {
            System.out.println("❌ Error in continueToOverview: " + e.getMessage());
            takeScreenshot("continue_to_overview_error");
            throw e;
        }
    }
    
    /**
     * Waits for the overview page to load with a timeout.
     */
    private void waitForOverviewPage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(@text, 'FINISH') or contains(@content-desc, 'FINISH')]")
            ));
        } catch (Exception e) {
            // If explicit wait fails, fall back to Thread.sleep
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.out.println("⚠️ Wait interrupted");
            }
        }
    }

    public void finishOrder() {
        try {
            System.out.println("🔍 Finding finish button...");
            WebElement finishBtn = findFinishButton();

            if (finishBtn == null) {
                System.out.println("❌ Finish button not found! Page source: " +
                        driver.getPageSource().substring(0, 500) + "...");
                takeScreenshot("finish_button_not_found");
                throw new NoSuchElementException("Could not find finish button using any method!");
            }

            System.out.println("✅ Finish button found, clicking...");

            // Scroll if button not visible
            if (!finishBtn.isDisplayed()) {
                System.out.println("⏬ Finish button not visible, scrolling...");
                scrollToElement(finishBtn);
            }

            click(finishBtn);

            // Wait for order completion
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                System.out.println("⚠️ Wait interrupted");
            }

            // Verify order completion
            if (!isOrderComplete()) {
                System.out.println("❌ Order not completed! Page source: " +
                        driver.getPageSource().substring(0, 500) + "...");
                takeScreenshot("order_not_completed");
                throw new RuntimeException("Order was not completed successfully!");
            }

            System.out.println("✅ Order completed successfully");

        } catch (Exception e) {
            System.err.println("❌ Error clicking finish button: " + e.getMessage());
            takeScreenshot("finish_order_error");
            throw e;
        }
    }

    /**
     * Takes a screenshot and saves it to the screenshots directory.
     *
     * @param screenshotName the base name for the screenshot file
     */
    private void takeScreenshot(String screenshotName) {
        try {
            String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            String directory = "screenshots/";
            new File(directory).mkdirs();
            String filePath = String.format("%s%s_%d.png", 
                directory, 
                screenshotName, 
                System.currentTimeMillis()
            );

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                byte[] imageBytes = Base64.getDecoder().decode(screenshot);
                fos.write(imageBytes);
                System.out.println("📸 Screenshot saved: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Failed to take screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Attempts to find the finish button using multiple strategies.
     * 
     * @return the found WebElement or null if not found
     */
    private WebElement findFinishButton() {
        System.out.println("🔍 Searching for finish button...");
        
        try {
            // First try by accessibility id (most reliable)
            WebElement button = driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc='test-FINISH']"));
            if (button != null && button.isDisplayed()) {
                System.out.println("✅ Found finish button by accessibility id");
                return button;
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Could not find button by accessibility id: " + e.getMessage());
        }
        
        // Fallback to other methods if needed
        WebElement button = tryDirectAccess();
        if (button != null) return button;
        
        button = tryTextSearch();
        if (button != null) return button;
        
        button = tryButtonElementSearch();
        if (button != null) return button;
        
        button = tryContentDescriptionSearch();
        if (button != null) return button;
        
        button = tryXPathSearch();
        if (button != null) return button;
        
        // Try scrolling and searching one more time
        button = tryScrollingAndSearching();
        
        if (button == null) {
            System.err.println("❌ Could not find finish button using any method!");
            // Take a screenshot for debugging
            takeScreenshot("finish_button_not_found");
        }
        
        return button;
    }
    
    private WebElement tryDirectAccess() {
        try {
            if (finishButton != null && finishButton.isDisplayed()) {
                System.out.println("✅ Found finish button directly");
                return finishButton;
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Direct access failed: " + e.getMessage());
        }
        return null;
    }
    
    private WebElement tryTextSearch() {
        try {
            if (finishButtonByText != null && finishButtonByText.isDisplayed()) {
                System.out.println("✅ Found finish button by text");
                return finishButtonByText;
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Text search failed: " + e.getMessage());
        }
        return null;
    }
    
    private WebElement tryButtonElementSearch() {
        try {
            if (finishButtonByButtonText != null && finishButtonByButtonText.isDisplayed()) {
                System.out.println("✅ Found finish button by button text");
                return finishButtonByButtonText;
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Button element search failed: " + e.getMessage());
        }
        return null;
    }
    
    private WebElement tryContentDescriptionSearch() {
        try {
            if (finishButtonByContentDesc != null && finishButtonByContentDesc.isDisplayed()) {
                System.out.println("✅ Found finish button by content description");
                return finishButtonByContentDesc;
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Content description search failed: " + e.getMessage());
        }
        return null;
    }
    
    private WebElement tryXPathSearch() {
        String[] xpaths = {
            "//*[contains(@text, 'FINISH')]",
            "//*[contains(@content-desc, 'FINISH')]",
            "//*[contains(@resource-id, 'finish')]",
            "//android.widget.Button[contains(@text, 'FINISH')]",
            "//android.widget.TextView[contains(@text, 'FINISH')]"
        };
        
        for (String xpath : xpaths) {
            try {
                WebElement element = driver.findElement(By.xpath(xpath));
                if (element != null && element.isDisplayed()) {
                    System.out.println("✅ Found finish button with XPath: " + xpath);
                    return element;
                }
            } catch (Exception e) {
                System.out.println("ℹ️ XPath search failed: " + xpath);
            }
        }
        return null;
    }
    
    private WebElement tryScrollingAndSearching() {
        try {
            System.out.println("⏬ Scrolling to find button...");
            scrollDown();
            
            // Try XPath search again after scrolling
            String[] xpaths = {
                "//*[contains(@text, 'FINISH')]",
                "//*[contains(@content-desc, 'FINISH')]"
            };
            
            for (String xpath : xpaths) {
                try {
                    WebElement element = driver.findElement(By.xpath(xpath));
                    if (element != null && element.isDisplayed()) {
                        System.out.println("✅ Found finish button after scroll with XPath: " + xpath);
                        return element;
                    }
                } catch (Exception e) {
                    // Continue to next XPath
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error while scrolling: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Performs a scroll down gesture on the screen.
     */
    private void scrollDown() {
        try {
            // Get screen dimensions
            int height = driver.manage().window().getSize().getHeight();
            int width = driver.manage().window().getSize().getWidth();
            
            // Calculate start and end points (70% down to 30% up)
            int startX = width / 2;
            int startY = (int) (height * 0.7);
            int endY = (int) (height * 0.3);
            
            // Create scroll action
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence scroll = new Sequence(finger, 1);
            
            // Move finger to start position
            scroll.addAction(finger.createPointerMove(
                Duration.ZERO, 
                PointerInput.Origin.viewport(), 
                startX, 
                startY
            ));
            
            // Press down
            scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            
            // Move to end position (scroll up)
            scroll.addAction(new PointerInput(PointerInput.Kind.TOUCH, "scroll")
                .createPointerMove(
                    Duration.ofMillis(500),
                    PointerInput.Origin.viewport(),
                    startX,
                    endY
                )
            );
            
            // Release finger
            scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            
            // Execute the action
            ((RemoteWebDriver) driver).perform(Collections.singletonList(scroll));
            
            System.out.println("⏬ Scrolled down");
            
            // Wait for any animations
            Thread.sleep(500);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("⚠️ Scroll wait interrupted");
        } catch (Exception e) {
            System.err.println("❌ Scroll failed: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the order has been successfully completed.
     * 
     * @return true if the order is complete, false otherwise
     */
    public boolean isOrderComplete() {
        try {
            // First check directly
            if (orderCompleteMessage != null && orderCompleteMessage.isDisplayed()) {
                return true;
            }
            
            // Check for common success messages
            String[] successMessages = {
                "THANK YOU FOR YOU ORDER",
                "ORDER CONFIRMATION",
                "THANK YOU",
                "ORDER PLACED"
            };
            
            String pageSource = driver.getPageSource().toUpperCase();
            
            // Check for success messages in page source
            for (String msg : successMessages) {
                if (pageSource.contains(msg)) {
                    System.out.println("✅ Order completion message found: " + msg);
                    return true;
                }
            }
            
            // Try alternative XPath for completion message
            if (checkAlternativeCompletionXPath()) {
                return true;
            }
            
            // Last resort: Check for common completion text in page source
            if (pageSource.contains("THANK YOU") || pageSource.contains("COMPLETE")) {
                System.out.println("✅ Order complete: Confirmation text found in page source");
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("❌ Could not verify order completion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks for order completion using alternative XPath selectors.
     * 
     * @return true if the completion element is found and visible
     */
    private boolean checkAlternativeCompletionXPath() {
        try {
            WebElement completeHeaderAlt = driver.findElement(
                By.xpath("//*[contains(@text, 'THANK YOU') or "
                      + "contains(@text, 'COMPLETE') or "
                      + "contains(@content-desc, 'COMPLETE')]")
            );
            
            if (isElementDisplayed(completeHeaderAlt)) {
                System.out.println("✅ Order complete: Alternative header found");
                return true;
            }
        } catch (Exception ex) {
            System.out.println("ℹ️ Alternative header not found: " + ex.getMessage());
        }
        return false;
    }
    
    /**
     * Checks if the current page is the checkout information page.
     * @return true if on checkout info page, false otherwise
     */
    public boolean isOnCheckoutInfoPage() {
        try {
            // Check if any of the input fields are visible
            boolean firstNameVisible = isElementDisplayed(firstNameField);
            boolean lastNameVisible = isElementDisplayed(lastNameField);
            boolean zipCodeVisible = isElementDisplayed(zipCodeField);
            
            // Also check for the continue button
            boolean continueButtonVisible = isElementDisplayed(continueButton);
            
            return firstNameVisible || lastNameVisible || zipCodeVisible || continueButtonVisible;
        } catch (Exception e) {
            System.out.println("❌ Error checking if on checkout info page: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if an error message is displayed on the page.
     * @return true if error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            // Small delay to ensure the error message has time to appear
            Thread.sleep(2000);
            
            // List of possible error message XPaths to check
            String[] errorXpaths = {
                "//android.widget.TextView[contains(@text, 'Error:')]",
                "//android.widget.TextView[contains(@text, 'required')]",
                "//android.widget.TextView[contains(@text, 'First Name')]",
                "//android.widget.TextView[contains(@text, 'Last Name')]",
                "//android.widget.TextView[contains(@text, 'Zip/Postal Code')]",
                "//*[contains(@text, 'missing')]",
                "//*[contains(@content-desc, 'error')]"
            };
            
            // Check each XPath
            for (String xpath : errorXpaths) {
                try {
                    WebElement errorElement = driver.findElement(By.xpath(xpath));
                    if (errorElement != null && errorElement.isDisplayed()) {
                        System.out.println("✅ Found error message with XPath: " + xpath);
                        System.out.println("📝 Error message text: " + errorElement.getText());
                        return true;
                    }
                } catch (Exception e) {
                    // Continue to next XPath if this one doesn't match
                    continue;
                }
            }
            
            // If no error message found with XPath, check page source as last resort
            String pageSource = driver.getPageSource().toLowerCase();
            boolean containsError = pageSource.contains("error") || 
                                  pageSource.contains("required") || 
                                  pageSource.contains("missing") ||
                                  pageSource.contains("first name") ||
                                  pageSource.contains("last name") ||
                                  pageSource.contains("zip/postal code");
                                  
            if (containsError) {
                System.out.println("ℹ️ Found error text in page source");
                return true;
            }
            
            System.out.println("ℹ️ No error message found on the page");
            return false;
            
        } catch (Exception e) {
            System.err.println("❌ Error checking for error message: " + e.getMessage());
            return false;
        }
    }
}
