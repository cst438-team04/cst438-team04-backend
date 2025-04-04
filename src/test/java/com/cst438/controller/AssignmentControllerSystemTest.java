package com.cst438.controller;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

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

        List<WebElement> links = driver.findElements(By.xpath("//a[contains(@id, 'viewAssignments-')]"));
        for (WebElement link : links) {
            System.out.println("Found assignment link: " + link.getAttribute("id"));
        }
        links.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.xpath("//button[contains(text(),'Add Assignment')]")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("atitle")).sendKeys("Selenium TestAddAssignment");
        driver.findElement(By.id("adueDate")).sendKeys("05-15-2025");

        driver.findElement(By.id("saveAssignment")).click();
        Thread.sleep(SLEEP_DURATION);

        // check the new assignment appears in the list
        WebElement row = driver.findElement(By.xpath("//tr[td[contains(text(),'Selenium TestAddAssignment')]]"));
        assertNotNull(row, "Assignment not found after adding.");

        // delete the test assignment
        WebElement deleteButton = row.findElement(By.xpath(".//button[contains(@id, 'delete-assignment-')]"));
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
                driver.findElement(By.xpath("//tr[td[contains(text(),'Selenium TestAddAssignment')]]")),
                "Assignment was not deleted properly.");
    }

    @Test
    public void testGradeAssignment() throws Exception {
        driver.findElement(By.linkText("View Sections")).click();
        Thread.sleep(SLEEP_DURATION);

        List<WebElement> links = driver.findElements(By.xpath("//a[contains(@id, 'viewAssignments-')]"));
        assertFalse(links.isEmpty(), "No assignment links found.");
        links.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement gradeButton = driver.findElement(By.id("grade-assignment-2"));
        gradeButton.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement scoreInput = driver.findElement(By.name("score"));
        scoreInput.clear();
        scoreInput.sendKeys("100");

        driver.findElement(By.xpath("//button[contains(text(),'Save Grades')]")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.navigate().refresh();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.linkText("View Sections")).click();
        Thread.sleep(SLEEP_DURATION);

        links = driver.findElements(By.xpath("//a[contains(@id, 'viewAssignments-')]"));
        links.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        gradeButton = driver.findElement(By.id("grade-assignment-2"));
        gradeButton.click();
        Thread.sleep(SLEEP_DURATION);

        // check that the new score was saved correctly
        WebElement updatedInput = driver.findElement(By.name("score"));
        assertEquals("100", updatedInput.getAttribute("value"), "Grade was not updated correctly.");
    }
}
