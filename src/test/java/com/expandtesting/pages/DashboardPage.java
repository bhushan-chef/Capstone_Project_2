package com.expandtesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private final By addNoteButton = By.xpath("//button[@data-testid='add-new-note' or contains(text(), 'Add Note')]");
    private final By categoryDropdown = By.name("category");
    private final By titleInput = By.name("title");
    private final By descriptionInput = By.name("description");
    private final By saveNoteButton = By.xpath("//button[@data-testid='note-submit' or contains(text(), 'Create')]");

    private By getDynamicNoteTitleLocator(String title) {
        // Bulletproof locator: Finds the text anywhere on the screen
        return By.xpath("//*[contains(text(), '" + title + "')]");
    }
    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public void createNote(String category, String title, String description) {
        click(addNoteButton);
        selectByText(categoryDropdown, category);
        type(titleInput, title);
        type(descriptionInput, description); // The intelligent engine will now handle this flawlessly!
        click(saveNoteButton);
    }

    public boolean isNoteDisplayed(String title) {
        try {
            return wait.until(d -> {
                try {
                    return d.findElement(getDynamicNoteTitleLocator(title)).isDisplayed();
                } catch(Exception e) {
                    return false; // Heals from stale elements while the grid loads
                }
            });
        } catch (Exception e) {
            return false;
        }
    }
}