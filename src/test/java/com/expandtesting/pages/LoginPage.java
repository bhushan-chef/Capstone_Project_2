package com.expandtesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    // Custom Locators (Strictly adhering to no-PageFactory rule)
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.xpath("//button[@type='submit']");
    private final By loginSuccessAlert = By.cssSelector("[data-testid='alert-message']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLogin() {
        driver.get("https://practice.expandtesting.com/notes/app/login");
    }

    public void performLogin(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(loginButton);
    }

    public String getAlertMessage() {
        return getText(loginSuccessAlert);
    }
}