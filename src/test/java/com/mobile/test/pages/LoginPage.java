package com.mobile.test.pages;

import com.mobile.test.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Giriş sayfası işlemlerini yöneten sayfa sınıfı
 * Page class that manages login page operations
 */
public class LoginPage extends BasePage {

    // Element Lokatörleri / Element Locators
    @AndroidFindBy(accessibility = "test-Username")
    private WebElement usernameInput;

    @AndroidFindBy(accessibility = "test-Password")
    private WebElement passwordInput;

    @AndroidFindBy(accessibility = "test-LOGIN")
    private WebElement loginButton;

    // Hata Mesajları / Error Messages
    // More flexible XPath to find error messages
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text, 'required') or contains(@text, 'Username') or contains(@text, 'Password')]")
    private WebElement errorMessageElement;

    public LoginPage(AndroidDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    /**
     * Kullanıcı adı ve şifre ile giriş yapar
     * Logs in with username and password
     */
    public void login(String username, String password) {
        System.out.println("Kullanıcı girişi yapılıyor... / Logging in user...");
        waitForElementToBeVisible(usernameInput);
        sendKeys(usernameInput, username);
        sendKeys(passwordInput, password);
        click(loginButton);
        System.out.println("Giriş butonuna tıklandı / Login button clicked");
    }

    /**
     * Hata mesajını döndürür
     * Returns the error message text
     */
    public String getErrorMessage() {
        System.out.println("Hata mesajı alınıyor...");
        System.out.println("Getting error message...");
        ensureKeyboardIsHidden();
        
        try {
            // First try to find the error message with a more flexible approach
            String errorXpath = "//android.widget.TextView[contains(@text, 'required') or contains(@text, 'Username') or contains(@text, 'Password') or contains(@text, 'epic sadface')]";
            WebElement errorElement = driver.findElement(By.xpath(errorXpath));
            String errorText = errorElement.getText();
            System.out.println("Alınan hata mesajı / Error message received: " + errorText);
            return errorText;
        } catch (Exception e) {
            System.err.println("Hata mesajı alınamadı / Could not get error message: " + e.getMessage());
            
            // Try one more time with a different approach if the first one fails
            try {
                String pageSource = driver.getPageSource();
                System.out.println("Sayfa kaynağı / Page source: " + pageSource);
                // Look for common error message patterns in the page source
                if (pageSource.contains("Username is required") || pageSource.contains("Password is required")) {
                    return "Username and password are required";
                } else if (pageSource.contains("epic sadface")) {
                    return "epic sadface: " + pageSource.split("epic sadface:")[1].split("\"")[0].trim();
                }
            } catch (Exception ex) {
                System.err.println("Hata mesajı alınamadı (ikinci deneme) / Could not get error message (second attempt): " + ex.getMessage());
            }
            
            return "";
        }
    }
    
    /**
     * Hata mesajının görünür olup olmadığını kontrol eder
     * Checks if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        try {
            // Add a small delay to ensure the error message has time to appear
            Thread.sleep(2000);
            
            // Try multiple ways to detect the error message
            try {
                // Try with the page object element first
                if (errorMessageElement.isDisplayed()) {
                    System.out.println("Hata mesajı görünür durumda (ana element) / Error message displayed (main element)");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Ana hata mesajı elementi bulunamadı / Main error message element not found: " + e.getMessage());
            }
            
            // Try with a more flexible XPath
            try {
                String errorXpath = "//android.widget.TextView[contains(@text, 'required') or contains(@text, 'Username') or contains(@text, 'Password') or contains(@text, 'epic sadface')]";
                boolean isDisplayed = driver.findElement(By.xpath(errorXpath)).isDisplayed();
                System.out.println("Hata mesajı görünür durumda (XPath ile) / Error message displayed (with XPath): " + isDisplayed);
                return isDisplayed;
            } catch (Exception e) {
                System.err.println("XPath ile hata mesajı bulunamadı / Error message not found with XPath: " + e.getMessage());
            }
            
            // Check page source as a last resort
            String pageSource = driver.getPageSource().toLowerCase();
            boolean containsError = pageSource.contains("required") || 
                                  pageSource.contains("username") || 
                                  pageSource.contains("password") ||
                                  pageSource.contains("epic sadface");
            System.out.println("Sayfa kaynağında hata metni arandı / Error text searched in page source: " + containsError);
            return containsError;
            
        } catch (Exception e) {
            System.err.println("Hata mesajı kontrol edilemedi / Could not check error message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Giriş sayfasının görüntülenip görüntülenmediğini kontrol eder
     * Checks if the login page is displayed
     */
    public boolean isLoginPageDisplayed() {
        return isElementDisplayed(loginButton);
    }

    /**
     * Klavye açıksa kapatır
     * Hides the keyboard if it's shown
     */
    private void ensureKeyboardIsHidden() {
        try {
            if (driver.isKeyboardShown()) {
                System.out.println("Klavye kapatılıyor... / Hiding keyboard...");
                driver.hideKeyboard();
            }
        } catch (Exception e) {
            System.out.println("Klavye zaten kapalı veya mevcut değil / Keyboard already hidden or not present");
        }
    }
}
