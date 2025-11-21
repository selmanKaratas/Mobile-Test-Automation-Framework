package com.mobile.test.pages;

import com.mobile.test.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Ürünler sayfası işlemlerini yöneten sayfa sınıfı
 * Page class that manages products page operations
 */
public class ProductsPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='PRODUCTS']")
    private WebElement productsTitle;
    
    @AndroidFindBy(accessibility = "test-Menu")
    private WebElement menuButton;
    
    @AndroidFindBy(accessibility = "test-LOGOUT")
    private WebElement logoutButton;

    public ProductsPage(AndroidDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    /**
     * Ürünler sayfası başlığını döndürür
     * Returns the products page title
     */
    public String getTitle() {
        System.out.println("Ürünler sayfası başlığı alınıyor...");
        System.out.println("Getting products page title...");
        return wait.until(ExpectedConditions.visibilityOf(productsTitle)).getText();
    }

    /**
     * Çıkış yapma işlemini gerçekleştirir
     * Performs the logout operation
     */
    public void logout() {
        System.out.println("Çıkış yapılıyor...");
        System.out.println("Logging out...");
        
        // Menüyü aç / Open menu
        click(menuButton);
        
        // Çıkış yap butonuna tıkla / Click logout button
        wait.until(ExpectedConditions.visibilityOf(logoutButton));
        click(logoutButton);
        
        System.out.println("Başarıyla çıkış yapıldı");
        System.out.println("Successfully logged out");
    }
    
    /**
     * Ürünler sayfasının görüntülenip görüntülenmediğini kontrol eder
     * Checks if the products page is displayed
     */
    public boolean isProductsPageDisplayed() {
        try {
            System.out.println("Ürünler sayfası kontrol ediliyor...");
            System.out.println("Checking if products page is displayed...");
            return wait.until(ExpectedConditions.visibilityOf(productsTitle)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Ürünler sayfası görüntülenemedi / Products page is not displayed");
            return false;
        }
    }
}
