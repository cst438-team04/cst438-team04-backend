package com.cst438.controller;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentControllerSystemTest {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver/chromedriver.exe";
    public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 1000;

    WebDriver driver;

    @BeforeEach
    public void setupDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);
    }

    @AfterEach
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void testAddAssignment() throws Exception {
        driver.findElement(By.linkText("View Sections")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.linkText("View Assignments")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.xpath("//button[contains(text(),'Add Assignment')]")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.xpath("//input[@label='Title' or @id='title']")).sendKeys("Selenium Test Assignment");
        driver.findElement(By.xpath("//input[@type='date']")).sendKeys("2025-05-15");

        driver.findElement(By.xpath("//button[contains(text(),'Save')]")).click();
        Thread.sleep(SLEEP_DURATION);

        // check the new assignment appears in the list
        WebElement row = driver.findElement(By.xpath("//tr[td[contains(text(),'Selenium Test Assignment')]]"));
        assertNotNull(row, "Assignment not found after adding.");

        // delete the test assignment
        WebElement deleteButton = row.findElement(By.xpath(".//button[@title='Delete']"));
        deleteButton.click();
        Thread.sleep(SLEEP_DURATION);

        // confirm deletion
        WebElement confirmYes = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"))
                .get(0);
        confirmYes.click();
        Thread.sleep(SLEEP_DURATION);

        // confirm assignment no longer exists
        assertThrows(NoSuchElementException.class, () ->
                driver.findElement(By.xpath("//tr[td[contains(text(),'Selenium Test Assignment')]]")),
                "Assignment was not deleted properly.");
    }

    @Test
    public void testGradeAssignment() throws Exception {
        driver.findElement(By.linkText("View Sections")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.linkText("View Assignments")).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement gradeIcon = driver.findElement(By.xpath("//button[@title='Grade']"));
        gradeIcon.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement scoreInput = driver.findElement(By.xpath("//input[@name='score']"));
        scoreInput.clear();
        scoreInput.sendKeys("95");

        driver.findElement(By.xpath("//button[contains(text(),'Save Grades')]")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.navigate().refresh();
        Thread.sleep(SLEEP_DURATION);

        // check that the new score was saved correctly
        WebElement updatedInput = driver.findElement(By.xpath("//input[@name='score']"));
        assertEquals("95", updatedInput.getAttribute("value"), "Grade was not updated correctly.");
    }
}
