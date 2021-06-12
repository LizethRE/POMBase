package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.Property;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BasePage {
    private WebDriver webDriver;
    private Properties properties;
    private Property property;

    public BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebDriver initializeWebDriver(){
        property = new Property();
        properties = property.initializeProperties();
        String browserName = properties.getProperty("browser");
        String driversPath = properties.getProperty("driversPath");

        if (browserName.equalsIgnoreCase("Chrome")){
            System.setProperty("webdriver.chrome.driver", driversPath + "chromedriver.exe");
            webDriver = new ChromeDriver();
        }else if (browserName.equalsIgnoreCase("Opera")) {
            System.setProperty("webdriver.chrome.driver", driversPath + "operadriver.exe");
            webDriver = new OperaDriver();
        }else if (browserName.equalsIgnoreCase("Firefox")){
            System.setProperty("webdriver.chrome.driver", driversPath + "geckodriver.exe");
            webDriver = new FirefoxDriver();
        }else {
            System.out.println("¡¡¡ Navegador no definido !!!");
        }
        webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        return webDriver;
    }

    public void visit(String url) {
        webDriver.get(url);
    }

    public void maximize() {
        webDriver.manage().window().maximize();
    }

    public String getTitle() {
        return webDriver.getTitle();
    }

    public WebElement findElement(By locator) {
        return webDriver.findElement(locator);
    }

    public List<WebElement> findElements(By locator) {
        return webDriver.findElements(locator);
    }

    public Boolean waitElement(By locator, int timeout) {
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, timeout);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return true;
    }

    public String getText(By locator) {
        return webDriver.findElement(locator).getText();
    }

    public void click(By locator) {
        webDriver.findElement(locator).click();
    }

    public void clear(By locator) {
        webDriver.findElement(locator).clear();
    }

    public void type(By locator, String text) {
        webDriver.findElement(locator).sendKeys(text);
    }

    public void selectByIndex(By locator, int index) {
        Select select = new Select(webDriver.findElement(locator));
        select.selectByIndex(index);
    }

    public void selectByValue(By locator, String value) {
        Select select = new Select(webDriver.findElement(locator));
        select.selectByValue(value);
    }

    public void selectByVisibleText(By locator, String value) {
        Select select = new Select(webDriver.findElement(locator));
        select.selectByVisibleText(value);
    }

    public Boolean isDisplayed(By locator) {
        try {
            return webDriver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean isEnabled(By locator) {
        try {
            return webDriver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean checkLinks(By locator){
        List<WebElement> links = findElements(locator);
        List<String> linksOk = new ArrayList<String>();
        List<String> linksNok = new ArrayList<String>();
        String url = "";
        int responseCode;
        HttpURLConnection httpURLConnection = null;
        Iterator<WebElement> iterator = links.iterator();

        while (iterator.hasNext()){
            url = iterator.next().getAttribute("href");

            if (url == null || url.isEmpty()){
                System.out.println("¡¡¡La URL del elemento -" + iterator.next().getText() + "- no se ha configurado!!!");
            }else {
                try {
                    httpURLConnection = (HttpURLConnection) (new URL(url).openConnection());
                    httpURLConnection.setRequestMethod("HEAD");
                    httpURLConnection.connect();
                    responseCode = httpURLConnection.getResponseCode();

                    if (responseCode > 400){
                        System.out.println("########## Enlace NOK ##########");
                        System.out.println("Elemento: " + iterator.next().getText());
                        System.out.println("Codigo de respuesta: " + responseCode);
                        linksNok.add(url);
                    }else {
                        System.out.println("########## Enlace OK ##########");
                        System.out.println("Elemento: " + iterator.next().getText());
                        System.out.println("Codigo de respuesta: " + responseCode);
                        linksOk.add(url);
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("¡¡¡Enlaces OK: " + linksOk.size() + "!!!");
        System.out.println("¡¡¡Enlaces NOK: " + linksNok.size()  + "!!!");

        if (linksNok.size() > 0){
            System.out.println("########## Enlaces NOK ##########");
            for (int i = 0; i < linksNok.size(); i++){
                System.out.println(linksNok.get(i));
            }
            return false;
        }else {
            return true;
        }
    }
}
