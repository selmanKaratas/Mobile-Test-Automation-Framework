package com.mobile.test.base;

import com.mobile.test.constants.Constants;
import com.mobile.test.pages.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Tüm test sınıflarının türeyeceği temel test sınıfı
 * Base test class that all test classes will extend
 */
public class BaseTest {
    protected static AndroidDriver driver;
    protected static WebDriverWait wait;
    protected LoginPage loginPage;
    protected ProductsPage productsPage;
    protected CartPage cartPage;
    protected CheckoutPage checkoutPage;
    private static final String APP_PACKAGE = "com.swaglabsmobileapp";
    private static final String MAIN_ACTIVITY = "com.swaglabsmobileapp.MainActivity";

    @BeforeSuite(alwaysRun = true)
    public static void globalSetup() throws MalformedURLException {
        if (driver == null) {
            // Appium sunucusu ayarları
            String appiumServerUrl = "http://127.0.0.1:4723";
            
            // Uygulama dosya yolu
            String appPath = System.getProperty("user.dir") + "/apps/sauce-demo-app.apk";
            
            // Cihaz özellikleri
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", "Android");
            capabilities.setCapability("deviceName", "Medium_Phone_API_35");
            capabilities.setCapability("app", appPath);
            capabilities.setCapability("automationName", "UiAutomator2");
            capabilities.setCapability("noReset", true);
            capabilities.setCapability("autoGrantPermissions", true);
            capabilities.setCapability("appWaitActivity", "com.swaglabsmobileapp.MainActivity");
            capabilities.setCapability("appPackage", "com.swaglabsmobileapp");
            
            // Uzun zaman aşımları
            capabilities.setCapability("uiautomator2ServerInstallTimeout", 120000);
            capabilities.setCapability("uiautomator2ServerLaunchTimeout", 120000);
            
            try {
                System.out.println("🔄 Appium sürücüsü başlatılıyor... / Starting Appium driver...");
                driver = new AndroidDriver(new URL(appiumServerUrl), capabilities);
                
                // WebDriverWait oluştur
                wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                
                // Uygulamayı temizle ve başlat
                driver.terminateApp("com.swaglabsmobileapp");
                driver.activateApp("com.swaglabsmobileapp");
                
                System.out.println("✅ Appium sürücüsü başarıyla başlatıldı. Uygulama açıldı.");
                System.out.println("✅ Appium driver started successfully. App launched.");
                
            } catch (Exception e) {
                System.err.println("❌ Appium sürücüsü başlatılamadı / Failed to start Appium driver: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Appium Sürücüsü başlatılamadı / Failed to initialize Appium Driver.", e);
            }
        }
    }

    /**
     * Ekran görüntüsü alır ve rapora ekler
     * Takes a screenshot and attaches it to the report
     * @param name Ekran görüntüsü için isim / Name for the screenshot
     * @return Ekran görüntüsü verisi / Screenshot data
     */
    @Attachment(value = "Screenshot - {0}", type = "image/png")
    public static byte[] takeScreenshot(String name) {
        try {
            System.out.println("📸 Taking screenshot: " + name);
            if (driver != null) {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to take screenshot: " + e.getMessage());
        }
        return new byte[0];
    }

    /**
     * Hata durumunda ekran görüntüsü alır
     * Takes a screenshot on test failure
     * @param testName Test adı / Test name
     * @param error Hata mesajı / Error message
     */
    protected void takeScreenshotOnFailure(String testName, String error) {
        try {
            String screenshotName = testName + "_FAILED_" + System.currentTimeMillis();
            takeScreenshot(screenshotName);
            System.out.println("❌ Test başarısız oldu: " + testName);
            System.out.println("❌ Hata: " + error);
            System.out.println("❌ Screenshot alındı: " + screenshotName);
            
            System.out.println("❌ Test failed: " + testName);
            System.out.println("❌ Error: " + error);
            System.out.println("❌ Screenshot taken: " + screenshotName);
        } catch (Exception e) {
            System.err.println("⚠️ Hata durumunda ekran görüntüsü alınamadı / Failed to take screenshot on failure: " + e.getMessage());
        }
    }

    protected void pageInit() {
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        
        // Initialize PageFactory elements
        PageFactory.initElements(new AppiumFieldDecorator(driver), loginPage);
        PageFactory.initElements(new AppiumFieldDecorator(driver), productsPage);
        PageFactory.initElements(new AppiumFieldDecorator(driver), cartPage);
        PageFactory.initElements(new AppiumFieldDecorator(driver), checkoutPage);
    }

    @AfterSuite(alwaysRun = true)
    public static void globalTearDown() {
        if (driver != null) {
            try {
                if (driver.getSessionId() != null) {
                    takeScreenshot("Test_Suite_End_" + System.currentTimeMillis());
                }
                
                driver.quit();
                System.out.println("❌ Appium oturumu sonlandırıldı.");
                System.out.println("❌ Appium session terminated.");
            } catch (Exception e) {
                System.err.println("⚠️ Oturum kapatılırken hata oluştu / Error while terminating session: " + e.getMessage());
            } finally {
                driver = null;
                wait = null;
            }
        }
    }
}
