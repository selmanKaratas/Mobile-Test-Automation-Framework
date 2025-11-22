package com.mobile.test.pages;

import com.mobile.test.base.BasePage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class ProductsPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='PRODUCTS']")
    private WebElement productsTitle;
    
    @AndroidFindBy(accessibility = "test-Menu")
    private WebElement menuButton;
    
    @AndroidFindBy(accessibility = "test-LOGOUT")
    private WebElement logoutButton;
    
    @AndroidFindBy(accessibility = "test-ADD TO CART")
    private WebElement addToCartButton;
    
    @AndroidFindBy(accessibility = "test-Cart")
    private WebElement cartIcon;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'test-Item')]/..")
    private List<WebElement> productItems;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'test-Item')]")
    private List<WebElement> productTitles;
    
    @AndroidFindBy(xpath = "//android.view.ViewGroup[contains(@content-desc, 'test-ADD TO CART')]")
    private List<WebElement> addToCartButtons;

    public ProductsPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isOnProductsPage() {
        return isElementDisplayed(productsTitle);
    }
    
    public void addFirstProductToCart() {
        if (!addToCartButtons.isEmpty()) {
            click(addToCartButtons.get(0));
            System.out.println("✅ İlk ürün sepete eklendi / First product added to cart");
        } else {
            System.out.println("❌ Sepete eklenecek ürün bulunamadı / No products found to add to cart");
        }
    }
    
    public void goToCart() {
        click(cartIcon);
    }
    
    public void logout() {
        click(menuButton);
        click(logoutButton);
        System.out.println("✅ Başarıyla çıkış yapıldı / Successfully logged out");
    }
    
    /**
     * Ürünler sayfasının görüntülenip görüntülenmediğini kontrol eder
     * Checks if the products page is displayed
     */
    public boolean isProductsPageDisplayed() {
        try {
            System.out.println("Ürünler sayfası kontrol ediliyor...");
            System.out.println("Checking if products page is displayed...");
            boolean isDisplayed = productsTitle.isDisplayed();
            System.out.println("Ürünler sayfası görüntülenme durumu / Products page display status: " + isDisplayed);
            if (isDisplayed) {
                System.out.println("Mevcut ürün sayısı / Number of products found: " + productTitles.size());
                if (!productTitles.isEmpty()) {
                    System.out.println("İlk ürün başlığı / First product title: " + productTitles.get(0).getText());
                }
            }
            return isDisplayed;
        } catch (Exception e) {
            System.out.println("Ürünler sayfası görüntülenemedi / Products page is not displayed: " + e.getMessage());
            return false;
        }
    }
}
