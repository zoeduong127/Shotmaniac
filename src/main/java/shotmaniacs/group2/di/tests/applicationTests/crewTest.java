package shotmaniacs.group2.di.tests.applicationTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class crewTest {

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
        driver.findElement(By.id("email")).sendKeys("testaccount@student.utwente.nl");
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
        wait.until(ExpectedConditions.titleIs("Dashboard"));

        assertTrue(driver.findElement(By.id("dashboard")).isDisplayed());
    }

    @Test
    public void testLogOut() {
        testLogin();

        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.id("drop_down"))).perform();
        driver.findElement(By.id("logout")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Shotmaniacs"));
    }

    @Test
    public void testNavigationDashboard() {
        testLogin();
        driver.findElement(By.id("News")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Announcement Scroll"));
        assertTrue(driver.findElement(By.id("filter_img")).isDisplayed());

        driver.findElement(By.id("dashboard")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Dashboard"));

        driver.findElement(By.id("calendar")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Calendar"));
        assertTrue(driver.findElement(By.id("calendar_container")).isDisplayed());

        driver.findElement(By.id("dashboard")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Dashboard"));

        driver.findElement(By.id("booking")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));
    }

    @Test
    public void testNavigationCalendar(){
        testLogin();
        driver.findElement(By.id("calendar")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Calendar"));
        assertTrue(driver.findElement(By.id("calendar_container")).isDisplayed());

        driver.findElement(By.id("dashboard")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Dashboard"));
        assertTrue(driver.findElement(By.id("dashboard")).isDisplayed());

        driver.findElement(By.id("calendar")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Calendar"));
        assertTrue(driver.findElement(By.id("calendar_container")).isDisplayed());

        driver.findElement(By.id("News")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Announcement Scroll"));
        assertTrue(driver.findElement(By.id("filter_img")).isDisplayed());

        driver.findElement(By.id("calendar")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Calendar"));
        assertTrue(driver.findElement(By.id("calendar_container")).isDisplayed());

        driver.findElement(By.id("booking")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));
    }

    @Test
    public void testNavigationNews(){
        testLogin();
        driver.findElement(By.id("News")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Announcement Scroll"));
        assertTrue(driver.findElement(By.id("filter_img")).isDisplayed());


        driver.findElement(By.id("dashboard")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Dashboard"));
        assertTrue(driver.findElement(By.id("dashboard")).isDisplayed());

        driver.findElement(By.id("News")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Announcement Scroll"));
        assertTrue(driver.findElement(By.id("filter_img")).isDisplayed());

        driver.findElement(By.id("calendar")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Calendar"));
        assertTrue(driver.findElement(By.id("calendar_container")).isDisplayed());

        driver.findElement(By.id("News")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Announcement Scroll"));
        assertTrue(driver.findElement(By.id("filter_img")).isDisplayed());

        driver.findElement(By.id("booking")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));
    }

    @Test
    public void testNavigationBooking(){
        testLogin();
        driver.findElement(By.id("booking")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));

        driver.findElement(By.id("dashboard")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Dashboard"));
        assertTrue(driver.findElement(By.id("dashboard")).isDisplayed());

        driver.findElement(By.id("booking")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));

        driver.findElement(By.id("News")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Announcement Scroll"));
        assertTrue(driver.findElement(By.id("filter_img")).isDisplayed());

        driver.findElement(By.id("booking")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));

        driver.findElement(By.id("calendar")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Calendar"));
        assertTrue(driver.findElement(By.id("calendar_container")).isDisplayed());

        driver.findElement(By.id("booking")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("bookings"));
    }

}