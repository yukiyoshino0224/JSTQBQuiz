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
import org.junit.jupiter.api.Assertions;
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
    private Path testDirPath;

    private String currentChapter = "default";

    @BeforeEach
    public void setUp(TestInfo testInfo) throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 初期値はdefaultでOK
        currentChapter = "default";
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {

        if (testInfo.getTags().contains("screenshot")) {
            takeFullPageScreenshots(testInfo.getDisplayName());
        }

        if (driver != null) {
            driver.quit();
        }
    }

    public void takeFullPageScreenshots(String testCaseName) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            long totalHeight = (long) js.executeScript("return document.body.scrollHeight");
            long viewportHeight = (long) js.executeScript("return window.innerHeight");
            long scrollY = 0;
            int count = 1;

            while (scrollY < totalHeight) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String fileName = testCaseName + "_part" + count + "_" + timestamp + ".png";
                Path destination = testDirPath.resolve(fileName);

                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                saveScreenshot(screenshotFile, destination);

                scrollY += viewportHeight;
                js.executeScript("window.scrollBy(0, arguments[0]);", viewportHeight);
                Thread.sleep(500);

                count++;
            }

            System.out.println("フルページスクリーンショット保存完了: " + count + "枚");

        } catch (Exception e) {
            System.out.println("フルページスクリーンショット保存エラー: " + e.getMessage());
        }
    }

    public void takeSingleScreenshotInCurrentChapter(String label) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = label + "_" + timestamp + ".png";
            Path destination = testDirPath.resolve(fileName);

            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            saveScreenshot(screenshotFile, destination);

            System.out.println("evaluate画面の1枚スクリーンショット保存完了");

        } catch (Exception e) {
            System.out.println("evaluate画面スクリーンショット保存エラー: " + e.getMessage());
        }
    }

    private Path createScreenshotDirectory(String testCaseName) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String year = now.format(DateTimeFormatter.ofPattern("yyyy"));
        String month = now.format(DateTimeFormatter.ofPattern("MM"));
        String day = now.format(DateTimeFormatter.ofPattern("dd"));
        String hourMinute = now.format(DateTimeFormatter.ofPattern("H時m分"));

        Path baseDir = Paths.get("screenshots", testCaseName, year, month, day, hourMinute);
        Files.createDirectories(baseDir);

        return baseDir;
    }

    public void saveScreenshot(File screenshotFile, Path destinationPath) throws IOException {
        Files.copy(screenshotFile.toPath(), destinationPath);
    }

    public void login(String mailAddress, String password) {
        driver.get("http://localhost:8080/login");

        WebElement inputEmail = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='email']")));
        inputEmail.sendKeys(mailAddress);

        WebElement inputPassword = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='password']")));
        inputPassword.sendKeys(password);

        WebElement loginButton = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type='submit']")));
        loginButton.click();
    }

    public void clickChapter(String chapterName, String testCaseName) {
        this.currentChapter = chapterName;
        WebElement chapter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(chapterName)));
        chapter.click();

        try {
            this.testDirPath = createScreenshotDirectory(testCaseName); // 変更点
        } catch (IOException e) {
            System.out.println("ディレクトリ作成エラー: " + e.getMessage());
        }
    }

    public void repeatInChapter(int NumberOfQuestions) {
        for (int i = 0; i < NumberOfQuestions; i++) {
            List<WebElement> choices = wait
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
            WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decideAnswerButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decideAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }

    public void clickPartialLinkText(String PLT) {
        WebElement test = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(PLT)));
        test.click();
    }

    public void questionRepetition(int NumberOfQuestions, String testCaseName) {
        repeatInChapter(NumberOfQuestions);

        // evaluateディレクトリに変更
        try {
            this.testDirPath = createScreenshotDirectory(testCaseName).resolve("evaluate");
            Files.createDirectories(testDirPath);
        } catch (IOException e) {
            System.out.println("evaluate用ディレクトリ作成エラー: " + e.getMessage());
        }

        takeSingleScreenshotInCurrentChapter("evaluate");

        // メニューに戻る
        WebElement toMenu = wait
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='button']")));
        toMenu.click();

        clickPartialLinkText("履歴");

        // 履歴ディレクトリに変更
        try {
            this.testDirPath = createScreenshotDirectory(testCaseName).resolve("履歴");
            Files.createDirectories(testDirPath);
        } catch (IOException e) {
            System.out.println("履歴用ディレクトリ作成エラー: " + e.getMessage());
        }

        takeFullPageScreenshots("履歴_" + testCaseName);
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_1() {
        login("first@time", "firsttime");
        currentChapter = "ログイン後";
        WebElement privateHistory = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("履歴")));
        privateHistory.click();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_2() {
        login("first@time", "firsttime");
        clickChapter("第1章", "RecordTestCaseNo_2");
        questionRepetition(10, "RecordTestCaseNo_2");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_3() {
        login("first@time", "firsttime");
        clickChapter("第2章", "RecordTestCaseNo_3");
        questionRepetition(10, "RecordTestCaseNo_3");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_4() {
        login("first@time", "firsttime");
        clickChapter("第3章", "RecordTestCaseNo_4");
        questionRepetition(10, "RecordTestCaseNo_4");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_5() {
        login("first@time", "firsttime");
        clickChapter("第4章", "RecordTestCaseNo_5");
        questionRepetition(10, "RecordTestCaseNo_5");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_6() {
        login("first@time", "firsttime");
        clickChapter("第5章", "RecordTestCaseNo_6");
        questionRepetition(10, "RecordTestCaseNo_6");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_7() {
        login("first@time", "firsttime");
        clickChapter("第6章", "RecordTestCaseNo_7");
        questionRepetition(10, "RecordTestCaseNo_7");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_8() {
        login("first@time", "firsttime");
        clickChapter("模擬試験", "RecordTestCaseNo_8");
        questionRepetition(40, "RecordTestCaseNo_8");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_9() {
        login("test@user1", "user1");
        clickChapter("第1章", "RecordTestCaseNo_2");
        questionRepetition(10, "RecordTestCaseNo_9");
        clickPartialLinkText("ログアウト");
        login("test@user2", "user2");
        clickChapter("第2章", "RecordTestCaseNo_2");
        questionRepetition(10, "RecordTestCaseNo_9");
        clickPartialLinkText("ログアウト");
    }

    public void elevenChapterRepeate(String chapterName, String testCaseName, int NumberOfQuestions) {
        login("test@user3", "user3");
        for (int i = 0; i <= 11; i++) {
            clickChapter(chapterName, testCaseName);
            repeatInChapter(NumberOfQuestions);
            clickPartialLinkText("メニューへ");
        }
        clickPartialLinkText("履歴");
    }

    public void recordAssertion() {
        List<WebElement> recordCards = driver.findElements(By.className("record-card"));

        long visibleCount = recordCards.stream()
                .filter(el -> el.isDisplayed())
                .count();

        Assertions.assertEquals(10, visibleCount, "表示されている record-card の数が10個ではありません");
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_11() {
        elevenChapterRepeate("第1章", "RecordTestCaseNo_11", 10);
        recordAssertion();
    }

    public void chapterSelectInRecord(String currentChapter) {
        WebElement chapters = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(currentChapter)));
        chapters.click();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_12() {
        elevenChapterRepeate("第2章", "RecordTestCaseNo_12", 10);
        chapterSelectInRecord("button[onclick='showChapter(2)']");
        recordAssertion();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_13() {
        elevenChapterRepeate("第3章", "RecordTestCaseNo_13", 10);
        chapterSelectInRecord("button[onclick='showChapter(3)']");
        recordAssertion();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_14() {
        elevenChapterRepeate("第4章", "RecordTestCaseNo_14", 10);
        chapterSelectInRecord("button[onclick='showChapter(4)']");
        recordAssertion();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_15() {
        elevenChapterRepeate("第5章", "RecordTestCaseNo_15", 10);
        chapterSelectInRecord("button[onclick='showChapter(5)']");
        recordAssertion();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_16() {
        elevenChapterRepeate("第6章", "RecordTestCaseNo_16", 10);
        chapterSelectInRecord("button[onclick='showChapter(6)']");
        recordAssertion();
    }

    @Test
    @Tag("screenshot")
    public void RecordTestCaseNo_17() {
        elevenChapterRepeate("模擬試験", "RecordTestCaseNo_17", 10);
        chapterSelectInRecord("button[onclick='showChapter(0)']");
        recordAssertion();
    }
}
