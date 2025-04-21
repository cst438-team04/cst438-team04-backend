package com.cst438.controller;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentEnrollmentSystemTest {
    public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\chromedriver-win64\\chromedriver.exe";
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
    public void studentEnrollsInSection() throws Exception {
        driver.findElement(By.id("enroll")).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement enrollBtn = driver.findElement(By.cssSelector("button[id^='enrollBtn-']"));
        enrollBtn.click();
        Thread.sleep(SLEEP_DURATION);

        String message = driver.findElement(By.id("enrollMessage")).getText();
        assertTrue(message.contains("Successfully enrolled"), "Enrollment success message not found.");

        driver.findElement(By.id("schedule")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("year")).sendKeys("2025");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("searchSchedule")).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement row = driver.findElement(By.xpath("//tr[td[contains(text(),'cst363')]]"));
        assertNotNull(row);

        WebElement deleteBtn = driver.findElement(By.cssSelector("button[id^='deleteBtn-']"));
        deleteBtn.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement confirmYes = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"))
                .get(0); // first button is "Yes"
        confirmYes.click();
        Thread.sleep(SLEEP_DURATION);
    }
}
