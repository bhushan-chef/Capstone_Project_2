package com.expandtesting.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverManager {

    // ThreadLocal ensures thread safety for parallel execution
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver() {
        if (driver.get() == null) {
            WebDriverManager.chromedriver().setup();

            var options = new ChromeOptions();
            options.addArguments("--start-maximized");
            // options.addArguments("--headless=new"); // We will uncomment this later for the CI/CD pipeline requirement

            driver.set(new ChromeDriver(options));
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}