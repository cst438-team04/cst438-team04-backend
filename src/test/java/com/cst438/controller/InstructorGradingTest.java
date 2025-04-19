package com.cst438.controller;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstructorGradingTest {
    public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\chromedriver-win64\\chromedriver.exe";;
    public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 2000; // 2 seconds
    public static final int WAIT_TIMEOUT = 30; // 30 seconds
    public static final String SCREENSHOT_DIR = "test-output/screenshots/";

    WebDriver driver;
    WebDriverWait wait;
    boolean currentTestFailed = false;

    @BeforeEach
    public void setUpDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        ops.addArguments("--start-maximized");
        ops.addArguments("--no-sandbox");
        ops.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(ops);
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);
        
        new File(SCREENSHOT_DIR).mkdirs();
    }

    @AfterEach
    public void terminateDriver() throws IOException {
        if (driver != null) {
            if (currentTestFailed) {
                takeScreenshot("failure");
            }
            driver.quit();
        }
    }

    private void takeScreenshot(String prefix) throws IOException {
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileHandler.copy(screenshot, 
            new File(SCREENSHOT_DIR + prefix + "_" + System.currentTimeMillis() + ".png"));
    }

    @Test
    public void testCompleteGradingWorkflow() throws Exception {
        try {
            // 1. Navigate to Sections view
            System.out.println("STEP 1: Navigating to Sections view");
            WebElement sectionsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Sections') or contains(text(), 'View Sections')]")));
            sectionsLink.click();
            Thread.sleep(SLEEP_DURATION);

            

            // 2. Find any available section
            System.out.println("STEP 2: Finding available section");
            WebElement sectionRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("tbody tr")));
            
            // 3. Click View Enrollments
            System.out.println("STEP 3: Clicking View Enrollments");
            WebElement enrollmentsLink = sectionRow.findElement(
                By.xpath(".//a[contains(text(), 'Enrollments')]"));
            enrollmentsLink.click();
            Thread.sleep(SLEEP_DURATION);

            // 4. Verify enrollments page
            System.out.println("STEP 4: Verifying enrollments page");
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(., 'Enrollments')]")));

            // 5. Find grade inputs
            System.out.println("STEP 5: Finding grade inputs");
            List<WebElement> gradeInputs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("input[name='grade'], input[type='text']")));
            
            if (gradeInputs.isEmpty()) {
                takeScreenshot("no_grade_inputs");
                fail("No grade input fields found");
            }

            // 6. Update grades to "A"
            System.out.println("STEP 6: Updating grades to 'A'");
            for (WebElement input : gradeInputs) {
                input.clear();
                input.sendKeys("A");
            }

            // 7. Save grades
            System.out.println("STEP 7: Saving grades");
            WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Save Grades')]")));
            saveButton.click();

            // 8. Verify success
            System.out.println("STEP 8: Verifying success");
            try {
                // Check for success message
                boolean messageAppeared = false;
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 3000) { // 3 second
                    List<WebElement> messages = driver.findElements(
                        By.xpath("//*[contains(., 'Grade saved') or contains(., 'success')]"));
                    if (!messages.isEmpty() && messages.get(0).isDisplayed()) {
                        messageAppeared = true;
                        break;
                    }
                    Thread.sleep(100);
                }
                assertTrue(messageAppeared, "Success message did not appear");

                // Verify grades were saved
                wait.until(d -> {
                    List<WebElement> updatedGrades = d.findElements(
                        By.cssSelector("input[name='grade'], input[type='text']"));
                    return updatedGrades.stream().allMatch(input -> "A".equals(input.getAttribute("value")));
                });
            } catch (Exception e) {
                takeScreenshot("verification_failed");
                throw e;
            }

        } catch (Exception e) {
            currentTestFailed = true;
            System.err.println("TEST FAILED: " + e.getMessage());
            takeScreenshot("error");
            throw e;
        }
    }

    @Test
public void testNoEnrollmentsCase() throws Exception {
    try {
        // 1. Navigate to Sections view
        System.out.println("STEP 1: Navigating to Sections view");
        WebElement sectionsLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(text(), 'Sections') or contains(text(), 'View Sections')]")));
        sectionsLink.click();
        Thread.sleep(SLEEP_DURATION);

        
        // 2. Find all rows with cst363
        System.out.println("STEP 2: Finding all cst363 sections");
        List<WebElement> cst363Sections = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
            By.xpath("//tr[contains(., 'cst363')]")));
        
        
        // Select bottom cst363
        WebElement bottomCst363 = cst363Sections.get(cst363Sections.size() - 1);
        
        // 4. Click View Enrollments for the selected row
        System.out.println("STEP 3: Clicking View Enrollments");
        WebElement enrollmentsLink = bottomCst363.findElement(
            By.xpath(".//a[contains(., 'Enrollments') or contains(., 'View Enrollments')]"));
        enrollmentsLink.click();
        Thread.sleep(SLEEP_DURATION);

        // 5. Verify empty state
        System.out.println("STEP 5: Verifying no enrollments");
        boolean noEnrollmentsFound = false;
        
        // Check for message or empty table
        try {
            WebElement noEnrollmentsMessage = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(., 'No enrollments') or contains(., 'No students')]")));
            noEnrollmentsFound = noEnrollmentsMessage.isDisplayed();
        } catch (TimeoutException e) {
            List<WebElement> enrollmentRows = driver.findElements(By.cssSelector("tbody tr"));
            noEnrollmentsFound = enrollmentRows.isEmpty();
        }
        
        assertTrue(noEnrollmentsFound, "Expected no enrollments but found data");

        // 6. Verify Save button is disabled
        System.out.println("STEP 6: Verifying Save button disabled");
        WebElement saveButton = driver.findElement(
            By.xpath("//button[contains(., 'Save Grades')]"));
        assertFalse(saveButton.isEnabled(), 
            "Save button should be disabled when no enrollments");

    } catch (Exception e) {
        currentTestFailed = true;
        System.err.println("TEST FAILED: " + e.getMessage());
        takeScreenshot("error");
        throw e;
    }
}
}
