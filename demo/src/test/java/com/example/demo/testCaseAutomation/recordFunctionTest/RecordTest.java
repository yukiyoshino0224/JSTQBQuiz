package com.example.demo.testCaseAutomation.recordFunctionTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;

public class RecordTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if (testInfo.getTags().contains("screenshot")){
            takeScreenShot(testInfo.getDisplayName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    public void takeScreenShot(String testCaseName) {
        try {
            Path screenshotDir = createScreenshotDirectory();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testCaseName + "_" + timestamp + ".png";
            Path destination = screenshotDir.resolve(fileName);

            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            saveScreenshot(screenshotFile, destination);

            System.out.println("保存完了" + destination);
        } catch (IOException e) {
            System.out.println("保存エラー" + e.getMessage());
        }
    }

    public Path createScreenshotDirectory() throws IOException {
        Path dir = Paths.get("screenshots");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    public void saveScreenshot(File screenshotFile, Path destinationPath) throws IOException {
        Files.copy(screenshotFile.toPath(), destinationPath);
    }

    
    public void login() { //共通処理とする

        driver.get("http://localhost:8080/login");

        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")));
        inputEmail.sendKeys("first@time");

        WebElement inputPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='password']")));
        inputPassword.sendKeys("firsttime");

        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type='submit']")));
        loginButton.click();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_1() {

        login();

        WebElement privateHistory = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("履歴")));
        privateHistory.click();

    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_2() {

        login();

        WebElement chapter_1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("第1章")));
        chapter_1.click();

        for (int i = 0; i < 10 ; i++) {

            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
            WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decideAnswerButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decideAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }

        WebElement toMenu = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='button']")));
        toMenu.click();

        WebElement privateHistory = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("履歴")));
        privateHistory.click();

    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_3() {

        login();

        WebElement chapter_2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("第2章")));
        chapter_2.click();

        for (int i = 0; i < 10; i++) {
            
        }

        
    }
}
