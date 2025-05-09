package com.example.demo.testOfEachSections;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SectionsTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    }



    @Test
    public void login() {
        
        driver.get("http://localhost:8080/login");

        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        inputEmail.sendKeys("cc@cc");

        WebElement inputPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        inputPassword.sendKeys("cccc");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        loginButton.click();

    }

    @Test //一章
    public void FirstSectionSequence() {
        login();
        WebElement firstSection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/chapter/1']")));
        firstSection.click();

        for (int i = 0; i < 9; i++) {
            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
             WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decicdeAnswerButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decicdeAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }

    @Test //二章
    public void secondSectionSequence() {
        login();
        WebElement firstSection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/chapter/2']")));
        firstSection.click();

        for (int i = 0; i < 9; i++) {
            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
             WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decicdeAnswerButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decicdeAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }

    @Test //三章
    public void thirdSectionSequence() {
        login();
        WebElement firstSection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/chapter/3']")));
        firstSection.click();

        for (int i = 0; i < 9; i++) {
            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
             WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decicdeAnswerButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decicdeAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }

    @Test //四章
    public void forthSectionSequence() {
        login();
        WebElement firstSection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/chapter/4']")));
        firstSection.click();

        for (int i = 0; i < 9; i++) {
            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
             WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decicdeAnswerButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decicdeAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }
    @Test //五章
    public void fifthSectionSequence() {
        login();
        WebElement firstSection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/chapter/5']")));
        firstSection.click();

        for (int i = 0; i < 9; i++) {
            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
             WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decicdeAnswerButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decicdeAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }
    @Test //六章
    public void sixthSectionSequence() {
        login();
        WebElement firstSection = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/chapter/6']")));
        firstSection.click();

        for (int i = 0; i < 9; i++) {
            List<WebElement> choices = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
            Random random = new Random();
            int randomIndex = random.nextInt(choices.size());
             WebElement label = choices.get(randomIndex);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);

            WebElement decicdeAnswerButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("submit-button")));
            decicdeAnswerButton.click();

            WebElement nextQuestionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("next-question")));
            nextQuestionButton.click();
        }
    }
}
