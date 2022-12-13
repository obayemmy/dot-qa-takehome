package com.dot.qa;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.print.PrintService;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class verifies elements on the wikipedia homepage.
 */
public class WikipediaTest {

    private Logger log = LoggerFactory.getLogger(WikipediaTest.class);

    private WebDriver driver;
    private ElementFinder finder;

    @BeforeClass
    public void setup() {

        /*
            WebDriverManager.chromedriver().setup() should automatically use the right
             driver for your Chrome version.  If it does not, you can choose a version manually
            see https://sites.google.com/a/chromium.org/chromedriver/downloads
            and update it as needed.

            WebDriverManager.chromedriver().version("74.0.3729.6").setup();
        */

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        finder = new ElementFinder(driver);

        // adjust timeout as needed
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get("https://www.wikipedia.org/");
    }

    @Test
    public void sloganPresent() {

        String sloganClass = "localized-slogan";
        WebElement slogan = finder.findElement(By.className(sloganClass));
        Assert.assertNotNull(slogan, String.format("Unable to find slogan div by class: %s", sloganClass));
        log.info("Slogan text is {}", slogan.getText());
        Assert.assertEquals(slogan.getText(), "The Free Encyclopedia");
    }



    @Test (dataProvider = "getPageObjects")
// To validate that languages are visible on the page
    public void assertingLanguage( String element, String verifyLang){

        WebElement LanguageText = finder.findElement(By.xpath(element));
        Assert.assertNotNull(LanguageText, String.format("Unable to find Language div by Xpath: %s", element));
        log.info("Language text is {}", LanguageText.getText());
        Assert.assertEquals(LanguageText.getText(), verifyLang);
    }

    @DataProvider (name="getPageObjects")
    public Object[][] PageObjects(){
        return new Object[][]

                {
                        {"//strong[contains(text(),'Français')]", "Français"},
                        {"//strong[contains(text(),'Español')]", "Español"},
                        {"//strong[contains(text(), 'Deutsch')]", "Deutsch"},
                        {"//strong[contains(text(), 'English')]", "English"},
                        {"//strong[contains(text(), 'Русский')]", "Русский"},
                        {"//strong[contains(text(), '日本語')]", "日本語"},
                        {"//*[@id=\"js-link-box-fa\"]/strong/bdi", "فارسی"},
                        {"//strong[contains(text(), 'Português')]", "Português"},
                        {"//strong[contains(text(), '中文')]", "中文"},
                        {"//strong[contains(text(), 'Italiano')]", "Italiano"}


                };

    };


    @Test (dataProvider = "pageLinks")
    public void links(String langLinks){
        WebElement weblink = finder.findElement(By.xpath(langLinks));
        String httpLinks = weblink.getAttribute("href");
        System.out.println("Testing Link " + httpLinks);

        try {
            HttpURLConnection huc = (HttpURLConnection) (new URL(httpLinks).openConnection());

            huc.setRequestMethod("HEAD");

            huc.connect();

            int respCode = huc.getResponseCode();

            if(respCode >= 400){
                System.out.println(httpLinks+" is a broken link");
                log.info("The Link for " + httpLinks + " Is broken");
            }
            else if (respCode == 200){
                System.out.println(httpLinks+" is a valid link, Status code = " + respCode);
                log.info( httpLinks + " Status Code = " + respCode);
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    };

    @DataProvider (name="pageLinks")
    public Object[][] PageLinks(){
        return new Object[][]

                {
                        {"//*[@id=\"js-link-box-en\"]"},
                        {"//*[@id=\"js-link-box-ru\"]"},
                        {"//*[@id=\"js-link-box-de\"]"},
                        {"//*[@id=\"js-link-box-it\"]"},
                        {"//*[@id=\"js-link-box-fa\"]"},
                        {"//*[@id=\"js-link-box-ja\"]"},
                        {"//*[@id=\"js-link-box-fr\"]"},
                        {"//*[@id=\"js-link-box-es\"]"},
                        {"//*[@id=\"js-link-box-zh\"]"},
                        {"//*[@id=\"js-link-box-pt\"]"},


                };

    };

    @AfterClass
    public void closeBrowser() {

        if(driver!=null) {
            driver.close();
        }
    }


}
