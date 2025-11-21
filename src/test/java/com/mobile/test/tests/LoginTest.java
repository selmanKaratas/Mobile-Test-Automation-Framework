package com.mobile.test.tests;

import com.mobile.test.base.BaseTest;
import com.mobile.test.constants.Constants;
import com.mobile.test.pages.LoginPage;
import com.mobile.test.pages.ProductsPage;
import io.qameta.allure.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Sauce Demo Tests")
@Feature("Login Functionality")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private ProductsPage productsPage;

    @BeforeMethod
    public void setup() throws InterruptedException {
        try {
            // Reset app to ensure we start from login screen
            if (driver != null) {
                driver.terminateApp("com.swaglabsmobileapp");
                driver.activateApp("com.swaglabsmobileapp");
                Thread.sleep(2000); // Wait for app to restart
            }
            
            // Initialize pages
            loginPage = new LoginPage(driver);
            productsPage = new ProductsPage(driver);
            System.out.println("✅ Test initialization completed");
        } catch (Exception e) {
            System.err.println("❌ Test initialization failed: " + e.getMessage());
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            // Take a final screenshot before quitting if the driver is still active
            if (driver != null) {
                takeScreenshot("Test_End_" + this.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error during teardown: " + e.getMessage());
        }
    }


    @Test(priority = 1, description = "Standard kullanıcı adı ve parola ile başarılı giriş testi.")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: Geçerli kullanıcı adı ve şifre ile giriş yapılabilmeli")
    @Story("Kullanıcı girişi başarılı olmalı")
    public void successfulLoginTest() {
        String testName = "successfulLoginTest";
        System.out.println("\n--- 1. Başarılı Giriş Testi Başladı ---");
        
        try {
            // Giriş öncesi ekran görüntüsü
            takeScreenshot(testName + "_1_before_login");
            
            // Giriş yap
            System.out.println("🔑 Kullanıcı girişi yapılıyor...");
            loginPage.login("standard_user", "secret_sauce");
            takeScreenshot(testName + "_2_after_login_click");

            // Ürünler sayfasının yüklendiğini bekle
            System.out.println("🔄 Ürünler sayfası yükleniyor...");
            String title = productsPage.getTitle();
            takeScreenshot(testName + "_3_products_page_loaded");
            
            // Doğrulamalar
            Assert.assertEquals(title, "PRODUCTS", "Başarılı giriş sonrası Products sayfası başlığı doğru değil.");
            
            System.out.println("✅ Senaryo 1 Başarılı: Kullanıcı başarıyla giriş yaptı ve ürünler sayfası görüntülendi.");
            takeScreenshot(testName + "_4_test_completed");
            
        } catch (AssertionError e) {
            String errorScreenshot = testName + "_ASSERTION_ERROR_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Assertion Hatası: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorScreenshot = testName + "_EXCEPTION_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Beklenmeyen Hata: " + e.getMessage());
            throw new RuntimeException("Test başarısız oldu: " + e.getMessage(), e);
        }
    }

    @Test(priority = 2, description = "Kullanıcı adı boş bırakıldığında hata mesajı testi.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: Kullanıcı adı boş bırakıldığında hata mesajı gösterilmeli")
    public void negativeLoginTest_missingUsername() {
        String testName = "negativeLoginTest_missingUsername";
        System.out.println("\n--- 2. Başarısız Giriş Testi Başladı (Eksik Kullanıcı Adı) ---");
        
        try {
            // Giriş öncesi ekran görüntüsü
            takeScreenshot(testName + "_1_initial_state");

            // Boş kullanıcı adı ile giriş yapmayı dene
            System.out.println("🔑 Boş kullanıcı adı ile giriş yapılıyor...");
            loginPage.login("", "secret_sauce");
            takeScreenshot(testName + "_2_after_login_attempt");

            // Hata mesajının görüntülendiğini doğrula
            System.out.println("🔍 Hata mesajı kontrol ediliyor...");
            boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
            takeScreenshot(testName + "_3_error_message_check");
            
            Assert.assertTrue(isErrorDisplayed, "Hata mesajı görüntülenmedi.");
            
            // Hata mesajını al ve doğrula
            String errorMessage = loginPage.getErrorMessage();
            takeScreenshot(testName + "_4_error_message_displayed");
            
            Assert.assertTrue(errorMessage.contains("Username is required"), 
                "Beklenen hata mesajı alınamadı. Alınan mesaj: " + errorMessage);
                
            System.out.println("✅ Senaryo 2 Başarılı: Kullanıcı adı zorunlu hatası doğrulandı.");
            takeScreenshot(testName + "_5_test_completed");
            
        } catch (AssertionError e) {
            String errorScreenshot = testName + "_ASSERTION_ERROR_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Assertion Hatası: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorScreenshot = testName + "_EXCEPTION_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Beklenmeyen Hata: " + e.getMessage());
            throw new RuntimeException("Test başarısız oldu: " + e.getMessage(), e);
        }
    }

    @Test(priority = 3, description = "Parola boş bırakıldığında hata mesajı testi.")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: Şifre boş bırakıldığında hata mesajı gösterilmeli")
    public void negativeLoginTest_missingPassword() {
        String testName = "negativeLoginTest_missingPassword";
        System.out.println("\n--- 3. Başarısız Giriş Testi Başladı (Eksik Şifre) ---");
        
        try {
            // Giriş öncesi ekran görüntüsü
            takeScreenshot(testName + "_1_initial_state");

            // Boş şifre ile giriş yapmayı dene
            System.out.println("🔑 Boş şifre ile giriş yapılıyor...");
            loginPage.login("standard_user", "");
            takeScreenshot(testName + "_2_after_login_attempt");

            // Hata mesajının görüntülendiğini doğrula
            System.out.println("🔍 Hata mesajı kontrol ediliyor...");
            boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
            takeScreenshot(testName + "_3_error_message_check");
            
            Assert.assertTrue(isErrorDisplayed, "Hata mesajı görüntülenmedi.");
            
            // Hata mesajını al ve doğrula
            String errorMessage = loginPage.getErrorMessage();
            takeScreenshot(testName + "_4_error_message_displayed");
            
            Assert.assertTrue(errorMessage.contains("Password is required"), 
                "Beklenen hata mesajı alınamadı. Alınan mesaj: " + errorMessage);
                
            System.out.println("✅ Senaryo 3 Başarılı: Şifre zorunlu hatası doğrulandı.");
            takeScreenshot(testName + "_5_test_completed");
            
        } catch (AssertionError e) {
            String errorScreenshot = testName + "_ASSERTION_ERROR_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Assertion Hatası: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorScreenshot = testName + "_EXCEPTION_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Beklenmeyen Hata: " + e.getMessage());
            throw new RuntimeException("Test başarısız oldu: " + e.getMessage(), e);
        }
    }

    @Test(priority = 4, description = "Başarılı giriş sonrası çıkış işlemi testi.")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: Başarılı giriş sonrası çıkış yapılabilmeli")
    public void logoutTest() {
        String testName = "logoutTest";
        System.out.println("\n--- 4. Çıkış İşlemi Testi Başladı (Logout Test Started) ---");
        
        try {
            // Giriş yap
            System.out.println("🔑 Kullanıcı girişi yapılıyor...");
            loginPage.login("standard_user", "secret_sauce");
            takeScreenshot(testName + "_1_after_login");
            
            // Ürünler sayfasının yüklendiğini doğrula
            System.out.println("🔍 Ürünler sayfası kontrol ediliyor...");
            Assert.assertTrue(productsPage.isProductsPageDisplayed(), "Ürünler sayfası görüntülenemedi.");
            takeScreenshot(testName + "_2_products_page_visible");
            
            // Çıkış yap
            System.out.println("🚪 Çıkış yapılıyor...");
            productsPage.logout();
            takeScreenshot(testName + "_3_after_logout_click");
            
            // Giriş sayfasına dönüldüğünü doğrula
            System.out.println("🔍 Giriş sayfası kontrol ediliyor...");
            boolean isLoginPageDisplayed = loginPage.isLoginPageDisplayed();
            takeScreenshot(testName + "_4_login_page_visible");
            
            Assert.assertTrue(isLoginPageDisplayed, "Çıkış işleminden sonra giriş sayfası görüntülenemedi.");
            
            System.out.println("✅ Senaryo 4 Başarılı: Kullanıcı başarıyla çıkış yaptı.");
            takeScreenshot(testName + "_5_test_completed");
            
        } catch (AssertionError e) {
            String errorScreenshot = testName + "_ASSERTION_ERROR_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Assertion Hatası: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorScreenshot = testName + "_EXCEPTION_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Beklenmeyen Hata: " + e.getMessage());
            throw new RuntimeException("Test başarısız oldu: " + e.getMessage(), e);
        }
    }
}
