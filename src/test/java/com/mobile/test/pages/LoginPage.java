package com.mobile.test.pages;

import com.mobile.test.base.BasePage;
import com.mobile.test.constants.Constants;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
    private final By errorMessage = By.xpath("//android.view.ViewGroup[@content-desc='test-Error message']/android.widget.TextView");
    private static final String ERROR_TEXT_VIEW_ACCESSIBILITY = "test-Error message";

    public LoginPage(AndroidDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
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
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }
    
    /**
     * Hata mesajının görünür olup olmadığını kontrol eder
     * Checks if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Giriş sayfasının görüntülenip görüntülenmediğini kontrol eder
     * Checks if the login page is displayed
     */
    public boolean isLoginPageDisplayed() {
        try {
            System.out.println("Giriş sayfası kontrol ediliyor... / Checking if login page is displayed...");
            return wait.until(ExpectedConditions.visibilityOf(loginButton)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Giriş sayfası görüntülenemedi / Login page is not displayed");
            return false;
        }
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
