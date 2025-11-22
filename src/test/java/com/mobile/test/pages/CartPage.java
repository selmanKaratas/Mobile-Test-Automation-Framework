package com.mobile.test.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

public class CartPage extends BasePage {
    
    @AndroidFindBy(accessibility = "test-CHECKOUT")
    private WebElement checkoutButton;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'test-Item')]")
    private List<WebElement> cartItems;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@content-desc, 'test-Item')]")
    private List<WebElement> productTitles;
    
    @AndroidFindBy(accessibility = "test-REMOVE")
    private WebElement removeButton;
    
    public CartPage(AndroidDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }
    
    public void checkout() {
        click(checkoutButton);
    }
    
    public boolean isProductInCart() {
        // Refresh elements to get the latest state
        cartItems = driver.findElements(By.xpath("//android.widget.TextView[contains(@content-desc, 'test-Item')]"));
        boolean hasItems = !cartItems.isEmpty();
        System.out.println("Sepette ürün var mı? / Is there a product in the cart? " + hasItems);
        if (hasItems) {
            System.out.println("Sepetteki ürün sayısı / Number of items in cart: " + cartItems.size());
            // Safely get the first product name if available
            try {
                productTitles = driver.findElements(By.xpath("//android.widget.TextView[contains(@content-desc, 'test-Item')]"));
                if (!productTitles.isEmpty()) {
                    System.out.println("İlk ürün adı / First product name: " + productTitles.get(0).getText());
                }
            } catch (Exception e) {
                System.out.println("Ürün adı alınırken hata oluştu / Error getting product name: " + e.getMessage());
            }
        }
        return hasItems;
    }
    
    public void removeFirstProduct() {
        if (!cartItems.isEmpty()) {
            System.out.println("İlk ürün sepetten çıkarılıyor / Removing first product from cart");
            click(removeButton);
            System.out.println("Ürün çıkarma işlemi tamamlandı / Product removal completed");
        } else {
            System.out.println("Sepette çıkarılacak ürün bulunamadı / No products found to remove from cart");
        }
    }
}
