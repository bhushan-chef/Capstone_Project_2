package com.expandtesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // 🏆 AGENTIC COMPONENT: Intelligent Visibility Engine
    // Bypasses hidden React modal bugs by dynamically filtering for the explicitly displayed element
    protected WebElement getVisibleElement(By locator) {
        wait.until(d -> {
            try {
                // Self-healing: Scans all matching locators and verifies at least one is truly visible
                return d.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
            } catch (Exception e) {
                return false; // Heals from StaleElementReferenceExceptions during React DOM updates
            }
        });

        // Returns the exact visible element, ignoring all hidden DOM garbage
        return driver.findElements(locator).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agentic Wait Failed: No visible element found"));
    }

    protected void click(By locator) {
        WebElement element = getVisibleElement(locator);
        wait.until(d -> element.isEnabled());
        element.click();
    }

    protected void type(By locator, String text) {
        WebElement element = getVisibleElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void selectByText(By locator, String text) {
        WebElement element = getVisibleElement(locator);
        Select dropdown = new Select(element);
        dropdown.selectByVisibleText(text);
    }

    protected String getText(By locator) {
        return getVisibleElement(locator).getText();
    }
}