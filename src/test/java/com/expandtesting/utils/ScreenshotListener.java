package com.expandtesting.utils;

import com.expandtesting.drivers.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "target/screenshots/";

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverManager.getDriver();
        if (driver == null) return;

        try {
            // 1. Save screenshot to disk under target/screenshots/
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = result.getName() + "_FAILED_" + timestamp + ".png";

            Path dir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(dir);

            byte[] screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);

            Files.write(dir.resolve(fileName), screenshot);
            System.out.println("Screenshot saved: " + SCREENSHOT_DIR + fileName);

            // 2. Also attach screenshot directly into Allure report
            Allure.addAttachment(
                    "Screenshot on Failure: " + result.getName(),
                    new ByteArrayInputStream(screenshot)
            );

        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("▶ Starting test: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ PASSED: " + result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭ SKIPPED: " + result.getName());
    }
}