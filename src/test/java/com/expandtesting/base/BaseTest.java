package com.expandtesting.base;

import com.expandtesting.drivers.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        DriverManager.initDriver();
        driver = DriverManager.getDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        Allure.step("Browser launched and session started");
    }

    @AfterMethod
    public void tearDown() {
        Allure.step("Closing browser session");
        DriverManager.quitDriver();
    }
}