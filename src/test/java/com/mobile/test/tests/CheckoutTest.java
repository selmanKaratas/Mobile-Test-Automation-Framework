package com.mobile.test.tests;

import com.mobile.test.base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Sauce Demo Tests")
@Feature("Checkout Process")
public class CheckoutTest extends BaseTest {

    @BeforeMethod
    public void setup() throws InterruptedException {
        try {
            if (driver != null) {
                driver.terminateApp("com.swaglabsmobileapp");
                driver.activateApp("com.swaglabsmobileapp");
                Thread.sleep(2000);
            }
            pageInit();
            // Login and add product to cart before checkout tests
            loginPage.login("standard_user", "secret_sauce");
            productsPage.addFirstProductToCart();
            productsPage.goToCart();
            System.out.println("✅ Checkout testi başlatıldı");
        } catch (Exception e) {
            System.err.println("❌ Test başlatılamadı: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5, description = "Sipariş tamamlama testi")
    @Severity(SeverityLevel.CRITICAL)
    public void completeOrderTest() {
        try {
            // Sepet sayfasından checkout'a tıkla
            cartPage.checkout();
            
            // Adres bilgilerini doldur
            checkoutPage.enterInfo("Selman", "Karatas", "34000");
            checkoutPage.continueToOverview();
            
            // Siparişi tamamla
            checkoutPage.finishOrder();
            
            // Siparişin tamamlandığını doğrula
            Assert.assertTrue(checkoutPage.isOrderComplete(), "Sipariş tamamlanamadı");
            takeScreenshot("order_complete");
        } catch (Exception e) {
            takeScreenshot("order_error");
            throw e;
        }
    }

    @Test(priority = 6, description = "Eksik bilgi ile ödeme testi")
    @Severity(SeverityLevel.NORMAL)
    public void checkoutWithMissingInfoTest() {
        try {
            System.out.println("🚀 Starting checkout with missing info test");
            cartPage.checkout();
            
            // Eksik bilgi gönder
            checkoutPage.enterInfo("", "", "");
            
            try {
                System.out.println("🔍 Attempting to continue with empty fields...");
                checkoutPage.continueToOverview();
                Assert.fail("Expected IllegalStateException was not thrown");
            } catch (IllegalStateException e) {
                // Beklenen istisna
                System.out.println("✅ Got expected exception: " + e.getMessage());
                takeScreenshot("expected_validation_error");
                
                // Hata mesajının görüntülendiğini doğrula
                boolean isErrorDisplayed = checkoutPage.isErrorMessageDisplayed();
                if (!isErrorDisplayed) {
                    takeScreenshot("error_message_not_displayed");
                }
                Assert.assertTrue(isErrorDisplayed, "Hata mesajı görüntülenmedi");
                
                // Hala checkout bilgi sayfasında olduğumuzu doğrula
                boolean isOnInfoPage = checkoutPage.isOnCheckoutInfoPage();
                if (!isOnInfoPage) {
                    takeScreenshot("not_on_checkout_info_page");
                }
                Assert.assertTrue(isOnInfoPage, 
                    "Hata durumunda hala checkout bilgi sayfasında olmalıyız");
                
                System.out.println("✅ Test passed: Validation error handled correctly");
                return; // Test başarılı
            } catch (Exception e) {
                takeScreenshot("unexpected_exception");
                System.err.println("❌ Unexpected exception: " + e.getMessage());
                throw new AssertionError("Beklenmeyen istisna: " + e.getMessage(), e);
            }
            
        } catch (Exception e) {
            takeScreenshot("checkout_test_error");
            System.err.println("❌ Test failed: " + e.getMessage());
            throw e;
        }
    }
}
