package shotmaniacs.group2.di.tests.applicationTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class administratorTest {
    private WebDriver driver;

    @Before
    public void setUp() {
        // Set up WebDriver configuration
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\.idea\\libraries\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        // Clean up after each test
        driver.quit();
    }

    @Test
    public void testLogin() {
        // Open the login page
        driver.get("http://localhost:8080/shotmaniacs2/");
        driver.findElement(By.id("login")).click();
        driver.findElement(By.id("email")).sendKeys("testaccountadmin@student.utwente.nl");
        driver.findElement(By.id("password")).sendKeys("yummybanana");
        driver.findElement(By.id("loginButton")).click();

        // Wait for the alert to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());

        // Switch to the alert
        Alert alert = driver.switchTo().alert();

        // Ignore the alert by accepting it
        alert.accept();

        // Switch back to the default content
        driver.switchTo().defaultContent();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Admin Dashboard"));

        assertTrue(driver.findElement(By.id("container1")).isDisplayed());
    }

    @Test
    public void testLogOut() {
    }
}
