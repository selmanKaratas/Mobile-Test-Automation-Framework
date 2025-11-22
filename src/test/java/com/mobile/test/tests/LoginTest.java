package com.mobile.test.tests;

import com.mobile.test.base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Sauce Demo Tests")
@Feature("Login Functionality")
public class LoginTest extends BaseTest {

    
    @Test(priority = 2, description = "Geçersiz giriş testi")
    @Severity(SeverityLevel.NORMAL)
    public void invalidLoginTest() {
        try {
            loginPage.login("invalid_user", "wrong_password");
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Hata mesajı görüntülenmedi");
            takeScreenshot("invalid_login");
        } catch (Exception e) {
            takeScreenshot("login_error");
            throw e;
        }
    }

    @BeforeMethod
    public void setup() throws InterruptedException {
        try {
            if (driver != null) {
                driver.terminateApp("com.swaglabsmobileapp");
                driver.activateApp("com.swaglabsmobileapp");
                Thread.sleep(2000);
            }
            pageInit();
            System.out.println("✅ Login testi başlatıldı");
        } catch (Exception e) {
            System.err.println("❌ Test başlatılamadı: " + e.getMessage());
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

            // Ürünler sayfasının yüklendiğini doğrula
            System.out.println("🔄 Ürünler sayfası yükleniyor...");
            boolean isOnProductsPage = productsPage.isOnProductsPage();
            takeScreenshot(testName + "_3_products_page_loaded");
            
            // Doğrulamalar
            Assert.assertTrue(isOnProductsPage, "Ürünler sayfasında değil");
            
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
            
            // Wait for error message to appear with a timeout
            long startTime = System.currentTimeMillis();
            boolean isErrorDisplayed = false;
            String errorMessage = "";
            
            while ((System.currentTimeMillis() - startTime) < 10000) { // 10 seconds timeout
                isErrorDisplayed = loginPage.isErrorMessageDisplayed();
                if (isErrorDisplayed) {
                    errorMessage = loginPage.getErrorMessage().toLowerCase();
                    if (!errorMessage.isEmpty()) {
                        break;
                    }
                }
                Thread.sleep(500); // Check every 500ms
            }
            
            takeScreenshot(testName + "_3_error_message_check");
            
            // More flexible assertion for error message
            Assert.assertTrue(isErrorDisplayed, "Hata mesajı görüntülenmedi.");
            
            // Take another screenshot after getting the error message
            takeScreenshot(testName + "_4_error_message_displayed");
            
            // Check for different possible error messages
            boolean isValidMessage = errorMessage.contains("username is required") || 
                                   errorMessage.contains("epic sadface: username") ||
                                   errorMessage.contains("username and password are required");
            
            Assert.assertTrue(isValidMessage, 
                "Beklenen hata mesajı alınamadı. Alınan mesaj: " + errorMessage);
                
            System.out.println("✅ Senaryo 2 Başarılı: Kullanıcı adı zorunlu hatası doğrulandı.");
            takeScreenshot(testName + "_5_test_completed");
            
        } catch (AssertionError e) {
            String errorScreenshot = testName + "_ASSERTION_ERROR_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Assertion Hatası: " + e.getMessage());
            // Take a final screenshot of the current state
            takeScreenshot(testName + "_FINAL_STATE_AFTER_FAILURE");
            // Print page source for debugging
            System.out.println("Hata anındaki sayfa kaynağı: " + driver.getPageSource());
            throw e;
        } catch (Exception e) {
            String errorScreenshot = testName + "_EXCEPTION_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Beklenmeyen Hata: " + e.getMessage());
            // Print stack trace for debugging
            e.printStackTrace();
            // Take a final screenshot of the current state
            takeScreenshot(testName + "_FINAL_STATE_AFTER_EXCEPTION");
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
            
            // Wait for error message to appear with a timeout
            long startTime = System.currentTimeMillis();
            boolean isErrorDisplayed = false;
            String errorMessage = "";
            
            while ((System.currentTimeMillis() - startTime) < 10000) { // 10 seconds timeout
                isErrorDisplayed = loginPage.isErrorMessageDisplayed();
                if (isErrorDisplayed) {
                    errorMessage = loginPage.getErrorMessage().toLowerCase();
                    if (!errorMessage.isEmpty()) {
                        break;
                    }
                }
                Thread.sleep(500); // Check every 500ms
            }
            
            takeScreenshot(testName + "_3_error_message_check");
            
            // More flexible assertion for error message
            Assert.assertTrue(isErrorDisplayed, "Hata mesajı görüntülenmedi.");
            
            // Take another screenshot after getting the error message
            takeScreenshot(testName + "_4_error_message_displayed");
            
            // Check for different possible error messages
            boolean isValidMessage = errorMessage.contains("password is required") || 
                                   errorMessage.contains("epic sadface: password") ||
                                   errorMessage.contains("username and password are required");
            
            Assert.assertTrue(isValidMessage, 
                "Beklenen hata mesajı alınamadı. Alınan mesaj: " + errorMessage);
                
            System.out.println("✅ Senaryo 3 Başarılı: Şifre zorunlu hatası doğrulandı.");
            takeScreenshot(testName + "_5_test_completed");
            
        } catch (AssertionError e) {
            String errorScreenshot = testName + "_ASSERTION_ERROR_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Assertion Hatası: " + e.getMessage());
            // Take a final screenshot of the current state
            takeScreenshot(testName + "_FINAL_STATE_AFTER_FAILURE");
            // Print page source for debugging
            System.out.println("Hata anındaki sayfa kaynağı: " + driver.getPageSource());
            throw e;
        } catch (Exception e) {
            String errorScreenshot = testName + "_EXCEPTION_" + System.currentTimeMillis();
            takeScreenshot(errorScreenshot);
            System.err.println("❌ Beklenmeyen Hata: " + e.getMessage());
            // Print stack trace for debugging
            e.printStackTrace();
            // Take a final screenshot of the current state
            takeScreenshot(testName + "_FINAL_STATE_AFTER_EXCEPTION");
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
