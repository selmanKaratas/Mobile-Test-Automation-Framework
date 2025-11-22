package com.mobile.test.tests;

import com.mobile.test.base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Sauce Demo Tests")
@Feature("Cart Operations")
public class CartTest extends BaseTest {

    @BeforeMethod
    public void setup() throws InterruptedException {
        try {
            if (driver != null) {
                driver.terminateApp("com.swaglabsmobileapp");
                driver.activateApp("com.swaglabsmobileapp");
                Thread.sleep(2000);
            }
            pageInit();
            // Login before cart tests
            loginPage.login("standard_user", "secret_sauce");
            System.out.println("✅ Cart testi başlatıldı");
        } catch (Exception e) {
            System.err.println("❌ Test başlatılamadı: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, description = "Sepete ürün ekleme testi")
    @Severity(SeverityLevel.CRITICAL)
    public void addProductToCartTest() {
        try {
            productsPage.addFirstProductToCart();
            productsPage.goToCart();
            Assert.assertTrue(cartPage.isProductInCart(), "Sepette ürün bulunamadı");
            takeScreenshot("cart_added");
        } catch (Exception e) {
            takeScreenshot("cart_error");
            throw e;
        }
    }

    @Test(priority = 4, description = "Sepetten ürün çıkarma testi")
    @Severity(SeverityLevel.NORMAL)
    public void removeProductFromCartTest() {
        try {
            // Önce ürün ekle
            productsPage.addFirstProductToCart();
            productsPage.goToCart();
            
            // Sonra çıkar
            cartPage.removeFirstProduct();
            Assert.assertFalse(cartPage.isProductInCart(), "Sepetten ürün çıkarılamadı");
            takeScreenshot("cart_removed");
        } catch (Exception e) {
            takeScreenshot("cart_remove_error");
            throw e;
        }
    }
}
