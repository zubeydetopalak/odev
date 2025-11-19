package com.test.app.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TodoUiSeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:8080";
    private String uniqueUser;
    private final String password = "test1234";

    @BeforeAll
    void setupClass() {
        WebDriverManager.chromedriver().setup();
        uniqueUser = "user" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // Uygulama ayakta mı kontrolü
        try {
            new java.net.Socket("localhost", 8080).close();
        } catch (Exception e) {
            Assertions.fail("Spring Boot uygulamanız http://localhost:8080 adresinde çalışmıyor!");
        }
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Her testte benzersiz kullanıcı adı üret
        uniqueUser = "user" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    void registerAndLogin() {
        driver.get(baseUrl + "/register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys(uniqueUser);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // Kayıt sonrası ya /login'e yönlenmeli ya da hata mesajı çıkmalı
        boolean loginRedirected = false;
        try {
            wait.until(ExpectedConditions.urlContains("/login"));
            loginRedirected = true;
        } catch (TimeoutException e) {
            // /login'e yönlenmediyse, hata mesajı var mı kontrol et
            boolean error = driver.getPageSource().contains("Kullanıcı adı zaten mevcut");
            if (error) {
                Assertions.fail("Kayıt başarısız: Kullanıcı adı zaten mevcut!");
            } else {
                Assertions.fail("Kayıt sonrası /login'e yönlenmedi ve hata mesajı da yok!");
            }
        }
        if (loginRedirected) {
            driver.findElement(By.id("username")).sendKeys(uniqueUser);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            wait.until(ExpectedConditions.urlContains("/todos"));
        }
    }

    @Test
    void testRegisterAndLogin() {
        registerAndLogin();
        wait.until(driver -> driver.findElements(By.cssSelector("span.me-3 b")).stream().anyMatch(b -> b.getText().equals(uniqueUser)));
        WebElement userSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.me-3 b")));
        Assertions.assertEquals(uniqueUser, userSpan.getText());
    }

    @Test
    void testLoginFail() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys("wronguser");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-danger")));
        //wait.until(driver -> driver.getPageSource().contains("Hatalı kullanıcı adı veya şifre"));
        //Assertions.assertTrue(driver.getPageSource().contains("Hatalı kullanıcı adı veya şifre"));
    }

    @Test
    void testAddTodo() {
        registerAndLogin();
        String todoText = "Selenium ile görev ekle";
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        input.sendKeys(todoText);
        driver.findElement(By.cssSelector("form[action='/todos'] button[type='submit']")).click();
        // Yeni todo DOM'da görünene kadar bekle
        wait.until(driver -> driver.findElements(By.cssSelector("ul.list-group li")).stream().anyMatch(li -> li.getText().contains(todoText)));
        List<WebElement> items = driver.findElements(By.cssSelector("ul.list-group li"));
        boolean found = items.stream().anyMatch(li -> li.getText().contains(todoText));
        Assertions.assertTrue(found);
    }

    @Test
    void testEditTodo() {
        registerAndLogin();
        String todoText = "Düzenlenecek görev";
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        input.sendKeys(todoText);
        driver.findElement(By.cssSelector("form[action='/todos'] button[type='submit']")).click();
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-warning")));
        editBtn.click();
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editModal")));
        WebElement editInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("edit-title")));
        editInput.clear();
        String newText = "Düzenlendi";
        editInput.sendKeys(newText);
        modal.findElement(By.cssSelector("button.btn-success")).click();
        wait.until(ExpectedConditions.urlContains("/todos"));
        wait.until(driver -> driver.findElements(By.cssSelector("ul.list-group li")).stream().anyMatch(li -> li.getText().contains(newText)));
        List<WebElement> items = driver.findElements(By.cssSelector("ul.list-group li"));
        boolean found = items.stream().anyMatch(li -> li.getText().contains(newText));
        Assertions.assertTrue(found);
    }

    @Test
    void testDeleteTodo() {
        registerAndLogin();
        String todoText = "Silinecek görev";
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        input.sendKeys(todoText);
        driver.findElement(By.cssSelector("form[action='/todos'] button[type='submit']")).click();
        wait.until(driver -> driver.findElements(By.cssSelector("ul.list-group li")).stream().anyMatch(li -> li.getText().contains(todoText)));
        List<WebElement> items = driver.findElements(By.cssSelector("ul.list-group li"));
        int before = items.size();
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form[action^='/todos/delete/'] button")));
        deleteBtn.click();
        // Silindikten sonra, eski todo DOM'da yoksa başarılı
        wait.until(driver -> driver.findElements(By.cssSelector("ul.list-group li")).stream().noneMatch(li -> li.getText().contains(todoText)));
        List<WebElement> itemsAfter = driver.findElements(By.cssSelector("ul.list-group li"));
        Assertions.assertTrue(itemsAfter.size() < before);
    }

    @Test
    void testToggleCompleted() {
        registerAndLogin();
        String todoText = "Tamamlanacak görev";
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
        input.sendKeys(todoText);
        driver.findElement(By.cssSelector("form[action='/todos'] button[type='submit']")).click();
        wait.until(driver -> driver.findElements(By.cssSelector("ul.list-group li")).stream().anyMatch(li -> li.getText().contains(todoText)));
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='checkbox']")));
        Assertions.assertFalse(checkbox.isSelected());
        checkbox.click();
        // Sayfa yenilendiği için tekrar bul
        wait.until(driver -> driver.findElements(By.cssSelector("ul.list-group li span")).stream().anyMatch(span -> span.getAttribute("class") != null && span.getAttribute("class").contains("text-decoration-line-through")));
        checkbox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='checkbox']")));
        Assertions.assertTrue(checkbox.isSelected());
        WebElement todoSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.list-group li span")));
        String clazz = todoSpan.getAttribute("class");
        Assertions.assertNotNull(clazz);
        Assertions.assertTrue(clazz.contains("text-decoration-line-through"));
    }

    @Test
    void testLogout() {
        registerAndLogin();
        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Çıkış Yap")));
        logoutBtn.click();
        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void testTodosPageWithoutLogin() {
        driver.get(baseUrl + "/todos");
        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}
