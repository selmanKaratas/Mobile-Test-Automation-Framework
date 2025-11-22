package com.mobile.test.pages;

import com.mobile.test.helpers.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.time.Duration;

public class BasePage {
    protected final AndroidDriver driver;
    protected final WaitHelper waitHelper;

    public BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, 10);
        FieldDecorator decorator = new AppiumFieldDecorator(driver, Duration.ofSeconds(10));
        PageFactory.initElements(decorator, this);
    }

    // Helper Methods
    protected void click(WebElement element) {
        try {
            waitHelper.waitForElementToBeClickable(element);
            element.click();
            System.out.println("✅ Elemente tıklandı");
        } catch (Exception e) {
            System.err.println("❌ Elemente tıklanamadı: " + e.getMessage());
            throw e;
        }
    }

    protected void sendKeys(WebElement element, String text) {
        try {
            waitHelper.waitForElementToBeVisible(element);
            element.clear();
            element.sendKeys(text);
            System.out.println("✅ Metin yazıldı: " + text);
        } catch (Exception e) {
            System.err.println("❌ Metin yazılamadı: " + e.getMessage());
            throw e;
        }
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            boolean isDisplayed = element.isDisplayed();
            System.out.println("✅ Element görünür durumda: " + isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            System.err.println("❌ Element görüntülenemedi: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * WaitHelper nesnesine erişim sağlar
     * Provides access to the WaitHelper instance
     */
    protected WaitHelper getWaitHelper() {
        return waitHelper;
    }
    
    /**
     * Scrolls to the specified element using W3C Actions
     * @param element The WebElement to scroll to
     */
    protected void scrollToElement(WebElement element) {
        try {
            // Get the center coordinates of the element
            int centerX = element.getLocation().getX() + (element.getSize().getWidth() / 2);
            int centerY = element.getLocation().getY() + (element.getSize().getHeight() / 2);
            
            // Scroll to the element using W3C Actions
            new org.openqa.selenium.interactions.Actions(driver)
                .moveToElement(element)
                .perform();
                
            System.out.println("✅ Scrolled to element");
            
            // Add a small delay to ensure the scroll completes
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            System.err.println("❌ Could not scroll to element: " + e.getMessage());
            throw e;
        }
    }
}
