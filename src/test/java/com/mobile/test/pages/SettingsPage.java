package com.mobile.test.pages;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SettingsPage extends BasePage {
    private static final String TAG = "[SettingsPage] ";

    // Main settings options
    @AndroidFindBy(id = "com.android.settings:id/dashboard_tile")
    private List<WebElement> settingsTiles;
    
    // Using more flexible locators that work with different Android versions
    @AndroidFindBy(xpath = "//*[contains(@text, 'Network') or contains(@text, 'Ağ') or contains(@content-desc, 'Network')]")
    private WebElement networkAndInternetOption;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Connected devices') or contains(@text, 'Bağlı cihazlar') or contains(@content-desc, 'Connected devices')]")
    private WebElement connectedDevicesOption;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Apps') or contains(@text, 'Uygulamalar') or contains(@content-desc, 'Apps')]")
    private WebElement appsOption;

    @AndroidFindBy(xpath = "//*[contains(@text, 'Battery') or contains(@text, 'Pil') or contains(@content-desc, 'Battery')]")
    private WebElement batteryOption;
    
    @AndroidFindBy(id = "com.android.settings:id/homepage_title")
    private WebElement settingsTitle;

    public SettingsPage(AndroidDriver driver) {
        super(driver);
        System.out.println(TAG + "Initializing SettingsPage");
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(15)), this);
    }

    public boolean isSettingsPageDisplayed() {
        try {
            System.out.println(TAG + "Checking if settings page is displayed");
            
            // First, try to find the settings title
            boolean isDisplayed = false;
            String pageTitle = "";
            
            try {
                isDisplayed = settingsTitle.isDisplayed();
                pageTitle = settingsTitle.getText();
            } catch (Exception e) {
                System.out.println(TAG + "Could not find settings title by ID, trying alternative locators");
                // Try alternative ways to detect settings page
                isDisplayed = !driver.findElements(
                    By.xpath("//*[contains(@text, 'Settings') or contains(@text, 'Ayarlar')]")).isEmpty();
                if (isDisplayed) {
                    pageTitle = "Settings";
                }
            }
            
            System.out.println(TAG + "Settings page is displayed: " + isDisplayed);
            System.out.println(TAG + "Page title: " + pageTitle);
            
            // Log available settings options
            System.out.println(TAG + "Available settings options (first 5):");
            int count = 0;
            for (WebElement tile : settingsTiles) {
                try {
                    if (count >= 5) break; // Limit to first 5 for logging
                    String text = tile.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        System.out.println("- " + text);
                        count++;
                    }
                } catch (Exception e) {
                    // Ignore elements that are not visible or don't have text
                }
            }
            
            // Also log page source for debugging if needed
            if (!isDisplayed) {
                System.out.println(TAG + "Page source (first 500 chars): " + 
                    driver.getPageSource().substring(0, Math.min(500, driver.getPageSource().length())));
            }
            
            return isDisplayed;
        } catch (Exception e) {
            System.err.println(TAG + "Error checking if settings page is displayed: " + e.getMessage());
            return false;
        }
    }

    public void clickNetworkAndInternet() {
        System.out.println(TAG + "Clicking on Network & Internet option");
        clickElement(networkAndInternetOption, "Network & Internet");
    }

    public void clickConnectedDevices() {
        System.out.println(TAG + "Clicking on Connected devices option");
        clickElement(connectedDevicesOption, "Connected devices");
    }

    public void clickApps() {
        System.out.println(TAG + "Clicking on Apps option");
        clickElement(appsOption, "Apps");
    }

    public void clickBattery() {
        System.out.println(TAG + "Clicking on Battery option");
        clickElement(batteryOption, "Battery");
    }

    private void clickElement(WebElement element, String elementName) {
        final int maxAttempts = 3;
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxAttempts) {
            try {
                attempt++;
                System.out.println(TAG + String.format("Attempt %d/%d to click on: %s", attempt, maxAttempts, elementName));
                
                // Try to find the element with a short timeout first
                WebElement visibleElement = waitForElementToBeVisible(element, 5);
                
                // Scroll to the element if needed
                scrollToElement(visibleElement);
                
                // Wait a bit for any animations
                Thread.sleep(500);
                
                System.out.println(TAG + "Clicking on element: " + elementName);
                visibleElement.click();
                System.out.println(TAG + "Successfully clicked on: " + elementName);
                
                // If we get here, the click was successful
                return;
                
            } catch (Exception e) {
                lastException = e;
                System.err.println(TAG + String.format("Attempt %d failed to click on %s: %s", 
                    attempt, elementName, e.getMessage()));
                
                if (attempt < maxAttempts) {
                    System.out.println(TAG + "Retrying...");
                    try {
                        Thread.sleep(1000); // Wait a bit before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        // If we get here, all attempts failed
        System.err.println(TAG + "Failed to click on " + elementName + " after " + maxAttempts + " attempts");
        if (lastException != null) {
            throw new RuntimeException("Failed to click on " + elementName, lastException);
        } else {
            throw new RuntimeException("Failed to click on " + elementName);
        }
    }

    private WebElement waitForElementToBeVisible(WebElement element, int timeoutInSeconds) {
        System.out.println(TAG + "Waiting for element to be visible (timeout: " + timeoutInSeconds + "s)");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            System.err.println(TAG + "Element not visible after " + timeoutInSeconds + " seconds: " + e.getMessage());
            
            // Try to get more information about the current screen
            try {
                String pageSource = driver.getPageSource();
                System.out.println(TAG + "Current page source (first 500 chars): " + 
                    pageSource.substring(0, Math.min(500, pageSource.length())));
            } catch (Exception ex) {
                System.err.println(TAG + "Could not get page source: " + ex.getMessage());
            }
            
            throw e;
        }
    }
}
