package com.expandtesting.base;

import com.expandtesting.drivers.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    // Declared at the class level so LoginTest and HybridSyncTest can inherit it
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        DriverManager.initDriver();

        // Assigning to the class variable instead of a local 'var'
        driver = DriverManager.getDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}