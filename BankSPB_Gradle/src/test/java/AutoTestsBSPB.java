import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static org.junit.Assert.*;

import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;

public class AutoTestsBSPB {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BSPB_URL = "https://www.bspb.ru";
    private JavascriptExecutor js;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String switchToNewTab(Set<String> oldWindowHandles) {
        wait.until(ExpectedConditions.numberOfWindowsToBe(oldWindowHandles.size() + 1));
        Set<String> newWindowHandles = driver.getWindowHandles();
        String newTabHandle = null;
        for (String handle : newWindowHandles) {
            if (!oldWindowHandles.contains(handle)) {
                newTabHandle = handle;
                break;
            }
        }
        if (newTabHandle != null) {
            driver.switchTo().window(newTabHandle);
            return newTabHandle;
        } else {
            fail("Ошибка: Новая вкладка не была найдена после переключения.");
            return null;
        }
    }

    @Test
    public void regionSelection_ChangeToKaliningrad_Test() {
        driver.get(BSPB_URL);

        // 1. Открываем меню выбора региона
        WebElement regionMenuButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[./span/p[text()='Вне региона']]")));
        regionMenuButton.click();

        // 2. Выбираем Калининград из списка
        WebElement kaliningradOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[starts-with(@id, 'menu-list-') and text()='Калининград']")));
        kaliningradOption.click();

        // 3. Проверяем, что регион изменился на "Калининград"
        WebElement regionText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[./span/p[text()='Калининград']]")));
        assertEquals("Регион должен измениться на Калининград", "Калининград", regionText.getText());
    }

    @Test
    public void officeAtmSearch_SearchByAddress_Test() {
        driver.get(BSPB_URL);

        // 1. Переходим в раздел "Офисы и банкоматы"
        WebElement officesAtmLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("Офисы и банкоматы")));
        officesAtmLink.click();
        wait.until(ExpectedConditions.urlContains("/map"));

        // 2. Выбираем отображение списком
        WebElement listModeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(@class, 'radio__label') and contains(text(), 'Списком')]")));
        listModeButton.click();

        // 3. Вводим адрес и выполняем поиск
        WebElement searchAddress = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[placeholder*='Поиск по адресу']")));
        searchAddress.sendKeys("Лесной пр-кт, 65 к. 1, Санкт-Петербург, Санкт-Петербург, Россия, 194100");
        searchAddress.sendKeys(Keys.ENTER);

        // 4. Проверяем результат поиска
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(), 'Лесной')]")));
        assertEquals("Заголовок найденного офиса должен быть 'ДО «Лесной»'", "ДО «Лесной»", header.getText());
    }

    @Test
    public void investors_NavigateToDetailedInformation_Test() {
        driver.get(BSPB_URL);

        // 1. Клик по ссылке "Инвесторам" и ожидание перехода
        WebElement investorsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("Инвесторам")));
        investorsLink.click();
        wait.until(ExpectedConditions.urlContains("/investors"));

        // 2. Клик по "Узнать подробнее"
        WebElement detailedInformation = wait.until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("Узнать подробнее")));
        detailedInformation.click();

        // 3. Ждем, пока URL изменится и проверяем его
        wait.until(ExpectedConditions.urlToBe("https://www.bspb.ru/investors/presentations"));
        assertEquals("URL должен соответствовать странице презентаций инвесторов", "https://www.bspb.ru/investors/presentations", driver.getCurrentUrl());
    }

    @Test
    public void privateBanking_NavigateToGold_ShouldReachGoldPage_Test() {
        driver.get(BSPB_URL);

        // 1. Клик по ссылке "Private Banking"
        WebElement privateBankingLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Private Banking")));
        privateBankingLink.click();

        // 2. Клик по стрелке для раскрытия меню
        WebElement arrowDiv = wait.until(ExpectedConditions.elementToBeClickable(By.className("t-cover__arrow_mobile")));
        arrowDiv.click();

        // 3. Клик по ссылке "Золото"
        WebElement goldLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[data-elem-id='1680204986064']")));
        goldLink.click();

        // 4. Ждем и проверяем URL
        wait.until(ExpectedConditions.urlToBe("https://pb.bspb.ru/gold_2"));
        assertEquals("URL должен соответствовать странице золота Private Banking", "https://pb.bspb.ru/gold_2", driver.getCurrentUrl());
    }

    @Test
    public void currencyExchange_DollarsToRubles_Test() {
        driver.get(BSPB_URL);

        WebElement financialMarketsLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Финансовые рынки")));
        financialMarketsLink.click();
        wait.until(ExpectedConditions.urlContains("/finance"));

        WebElement cashExchangeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[id^='tabs-'][id$='--tab-0']")));
        cashExchangeButton.click();

        WebElement detailedInformation = wait.until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("Узнать подробнее")));
        detailedInformation.click();
        wait.until(ExpectedConditions.urlContains("/exchange"));

        js.executeScript("window.scrollBy(0, 1350);");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='text' and @inputmode='decimal']")));

        inputField.sendKeys(Keys.CONTROL + "a");
        inputField.sendKeys(Keys.DELETE);
        inputField.sendKeys("10");

        assertTrue("URL должен содержать '/exchange'", driver.getCurrentUrl().contains("/exchange"));
        assertEquals("Значение в поле должно быть '100.00'", "100.00", inputField.getAttribute("value"));
    }

    @Test
    public void vedNavigation_ToMarketRiskManagement_Test() {
        driver.get(BSPB_URL);

        WebElement VEDLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("ВЭД")));
        VEDLink.click();
        wait.until(ExpectedConditions.urlContains("/foreign-trade"));

        // Прокрутка страницы
        js.executeScript("window.scrollBy(0, 500);");

        try {
            Thread.sleep(1000); // Оставлен Thread.sleep() по запросу
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement riskManagementLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='https://www.bspb.ru/foreign-trade/market-risk-management']")));
        riskManagementLink.click();

        wait.until(ExpectedConditions.urlToBe("https://www.bspb.ru/foreign-trade/market-risk-management"));
        assertEquals("URL должен соответствовать странице управления рыночными рисками", "https://www.bspb.ru/foreign-trade/market-risk-management", driver.getCurrentUrl());
    }

    @Test
    public void businessNavigation_ToCreditsSM_Test() {
        driver.get(BSPB_URL);

        WebElement BusinessLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Бизнесу")));
        BusinessLink.click();
        wait.until(ExpectedConditions.urlContains("/business"));

        // Прокрутка страницы
        js.executeScript("window.scrollBy(0, 500);");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement creditsDiv = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//p[text()='на любые цели для вашего бизнеса']")));
        creditsDiv.click();

        wait.until(ExpectedConditions.urlToBe("https://www.bspb.ru/business/credits-sm"));
        assertEquals("URL должен соответствовать странице кредитов для малого бизнеса", "https://www.bspb.ru/business/credits-sm", driver.getCurrentUrl());
    }

    @Test
    public void NavigationToAuthorizationIndividual_Test() {
        driver.get(BSPB_URL);
        WebElement LoginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("popover-trigger-:R3adt9jltmH1:")));
        LoginButton.click();

        WebElement internetBankSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='Интернет-банк ФЛ']")));
        internetBankSpan.click();

        String mainWindowHandle = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        WebElement header = driver.findElement(By.xpath("//h2[contains(., 'Вход в интернет-банк')]"));
        assertEquals("Текст заголовка не совпадает", "Вход в интернет-банк", header.getText());
    }

    @Test
    public void BecomeCustomer_Test() {
        driver.get(BSPB_URL);
        WebElement LoginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("popover-trigger-:R3adt9jltmH1:")));
        LoginButton.click();

        WebElement internetBankSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='Интернет-банк ФЛ']")));
        internetBankSpan.click();

        String mainWindowHandle = driver.getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        WebElement becomeClientLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Стать клиентом")));
        becomeClientLink.click();

        wait.until(ExpectedConditions.urlToBe("https://www.bspb.ru/retail/cards/debit"));
        assertEquals("Текущий URL должен соответствовать URL дебетовых карт", "https://www.bspb.ru/retail/cards/debit", driver.getCurrentUrl());
    }

    @Test
    public void searchFunctionality_EnterQuery_Test() {
        driver.get(BSPB_URL);

        WebElement searchIconLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='css-35z2bt']/a[@href='/search']")
        ));
        searchIconLink.click();

        WebElement searchInputField = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[placeholder='Введите поисковый запрос']")));
        searchInputField.sendKeys("Кредит \"Время деньги\"");

        assertEquals("Текст в поле ввода должен соответствовать введенному запросу", "Кредит \"Время деньги\"", searchInputField.getAttribute("value"));
    }
}