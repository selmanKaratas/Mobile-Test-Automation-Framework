package com.mobile.test.pages;

import com.mobile.test.helpers.WaitHelper;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.time.Duration;

public class BasePage {
    protected final AndroidDriver driver;
    protected final WaitHelper waitHelper;

    public BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver, 10);
        FieldDecorator decorator = new AppiumFieldDecorator(driver, Duration.ofSeconds(10));
        PageFactory.initElements(decorator, this);
    }
}
