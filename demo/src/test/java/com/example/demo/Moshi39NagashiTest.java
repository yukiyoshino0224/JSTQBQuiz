package com.example.demo;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Moshi39NagashiTest {
    @Test
    public void SequenceOfMoshi() {

        // System.setProperty("webdriver.chrome.driver",
        // "C:\\selenium\\chromedriver-win64\\chromedriver.exe");
        // ↑これはpathを設定していない場合に用います
        // 環境構築していないとSelenium使えないので、実施する際は先にそちらをお願いします。

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // 模試を流す目的の為、Assertionは全画面省略します。

        driver.get("http://localhost:8080/login");

        /*
         * WebElement registName =
         * wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
         * registName.sendKeys("はまはま");
         * 
         * 
         * WebElement registMailaddress =
         * wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
         * registMailaddress.sendKeys("abc@defgh");
         * 
         * 
         * WebElement registPassword =
         * wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
         * registPassword.sendKeys("abcdefg");
         * 
         * WebElement registButton =
         * wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(
         * "button[type='submit']")));
         * registButton.click();
         */

        // 以下ログイン画面内処理
        WebElement inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        inputEmail.sendKeys("cc@cc");// 自身で登録しているものに変更

        WebElement inputPassword = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        inputPassword.sendKeys("cccc");// 自身で登録しているものに変更

        WebElement loginButton = wait
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        loginButton.click();

        // ログイン完了後、以下メニュー画面での捜査
        WebElement moshiLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText("模擬試験")));
        moshiLink.click();

        // さあ、ここから模試39問、一気にいきますよぉ！！！
        for (int i = 0; i < 39; i++) {
            List<WebElement> choices = wait
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("label")));
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
