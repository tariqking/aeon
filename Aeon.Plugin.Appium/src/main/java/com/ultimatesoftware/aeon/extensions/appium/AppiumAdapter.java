package com.ultimatesoftware.aeon.extensions.appium;

import com.ultimatesoftware.aeon.core.common.exceptions.BrowserTypeNotRecognizedException;
import com.ultimatesoftware.aeon.core.common.exceptions.NoSuchElementException;
import com.ultimatesoftware.aeon.core.common.exceptions.NoSuchElementsException;
import com.ultimatesoftware.aeon.core.common.interfaces.IBy;
import com.ultimatesoftware.aeon.core.common.mobile.AppType;
import com.ultimatesoftware.aeon.core.common.mobile.interfaces.IByMobile;
import com.ultimatesoftware.aeon.core.common.mobile.interfaces.IByMobileXPath;
import com.ultimatesoftware.aeon.core.common.mobile.selectors.ByMobile;
import com.ultimatesoftware.aeon.core.common.mobile.selectors.ByMobileId;
import com.ultimatesoftware.aeon.core.common.mobile.selectors.MobileSelectOption;
import com.ultimatesoftware.aeon.core.common.web.BrowserSize;
import com.ultimatesoftware.aeon.core.common.web.BrowserType;
import com.ultimatesoftware.aeon.core.common.web.WebSelectOption;
import com.ultimatesoftware.aeon.core.common.web.interfaces.IByWeb;
import com.ultimatesoftware.aeon.core.framework.abstraction.adapters.IMobileAdapter;
import com.ultimatesoftware.aeon.core.framework.abstraction.controls.web.WebControl;
import com.ultimatesoftware.aeon.extensions.selenium.SeleniumAdapter;
import com.ultimatesoftware.aeon.extensions.selenium.SeleniumConfiguration;
import com.ultimatesoftware.aeon.extensions.selenium.SeleniumElement;
import com.ultimatesoftware.aeon.extensions.selenium.jquery.IJavaScriptFlowExecutor;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.logging.LoggingPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Mobile adapter for Appium.
 */
public class AppiumAdapter extends SeleniumAdapter implements IMobileAdapter {

    private static Logger log = LoggerFactory.getLogger(AppiumAdapter.class);

    private String context;

    private HashMap<Integer, Integer> mobileDeviceResolutions = new HashMap<>();

    private static final String FL1_FL1_VIEWGROUP1 = "android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.ViewGroup[1]/";
    private static final String TEXTVIEW1 = "android.widget.TextView[1]";
    private static final String FL1_FL1_LINEARLAYOUT1 = "android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/";
    private static final String H_FL1_FRAMELAYOUT2 = "/hierarchy/android.widget.FrameLayout[1]/android.widget.FrameLayout[2]/";
    private static final String H_FL1_FRAMELAYOUT1 = "/hierarchy/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/";
    private static final String FL1_FL2_FRAMELAYOUT1 = "android.widget.FrameLayout[1]/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/";
    private static final String FL1_VG1_TEXTVIEW1 = "android.widget.FrameLayout[1]/android.view.ViewGroup[1]/android.widget.TextView[1]";
    private static final String SV1_FL1_FRAMELAYOUT1 = "android.widget.ScrollView[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/";
    private static final String FL1_LL1_LINEARLAYOUT1 = "android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/";
    private static final String ANDROID_ID_DATE_PICKER_HEADER_YEAR = "android:id/date_picker_header_year";
    private static final String ANDROID_HYBRID_APP = "AndroidHybridApp";
    private static final String IOS_HYBRID_APP = "IOSHybridApp";
    private static final String NATIVE_APP = "NATIVE_APP";

    /**
     * Constructor for Selenium Adapter.
     *
     * @param seleniumWebDriver       The driver for the adapter.
     * @param javaScriptExecutor      The javaScript executor for the adapter.
     * @param asyncJavaScriptExecutor The asynchronous javaScript executor for the adapter.
     * @param configuration           The configuration object.
     * @param browserSize             The screen resolution for the machine
     * @param seleniumHubUrl          The used Selenium hub URL.
     * @param loggingPreferences      Preferences which contain which Selenium log types to enable
     */
    AppiumAdapter(
            WebDriver seleniumWebDriver,
            IJavaScriptFlowExecutor javaScriptExecutor,
            IJavaScriptFlowExecutor asyncJavaScriptExecutor,
            SeleniumConfiguration configuration,
            BrowserSize browserSize,
            URL seleniumHubUrl,
            LoggingPreferences loggingPreferences
    ) {
        super(seleniumWebDriver, javaScriptExecutor, asyncJavaScriptExecutor, configuration, browserSize, seleniumHubUrl, loggingPreferences);

        if (browserType == AppType.ANDROID_HYBRID_APP || browserType == AppType.IOS_HYBRID_APP) {
            context = getMobileWebDriver().getContext();
        }

        mobileDeviceResolutions.put(1125, 2436);
        mobileDeviceResolutions.put(1080, 1920);
        mobileDeviceResolutions.put(750, 1334);
        mobileDeviceResolutions.put(640, 1136);
        mobileDeviceResolutions.put(2048, 2732);
        mobileDeviceResolutions.put(1536, 2048);
        mobileDeviceResolutions.put(768, 1024);
        mobileDeviceResolutions.put(1440, 2560);
        mobileDeviceResolutions.put(1200, 1920);
        mobileDeviceResolutions.put(800, 1280);
    }

    /**
     * Gets the web driver.
     *
     * @return The web driver is returned.
     */
    public AppiumDriver getMobileWebDriver() {
        if (!(webDriver instanceof AppiumDriver)) {
            throw new AppiumException("This command is only supported by an AppiumDriver");
        }

        return (AppiumDriver) webDriver;
    }

    @Override
    public void mobileSetPortrait() {
        getMobileWebDriver().rotate(ScreenOrientation.PORTRAIT);
    }

    @Override
    public void mobileSetLandscape() {
        getMobileWebDriver().rotate(ScreenOrientation.LANDSCAPE);
    }

    @Override
    public void mobileHideKeyboard() {
        getMobileWebDriver().hideKeyboard();
    }

    @Override
    public void mobileLock() {
        mobileLock(0);
    }

    @Override
    public void mobileLock(int seconds) {
        switch (browserType.getKey()) {
            case ANDROID_HYBRID_APP:
                ((AndroidDriver) getMobileWebDriver()).lockDevice();
                break;
            case IOS_HYBRID_APP:
                ((IOSDriver) getMobileWebDriver()).lockDevice(Duration.ofSeconds(seconds));
                break;
            default:
                throw new UnsupportedCommandException();
        }
    }

    @Override
    public void mobileSetGeoLocation(double latitude, double longitude, double altitude) {
        getMobileWebDriver().setLocation(new Location(latitude, longitude, altitude));
    }

    @Override
    public void swipe(boolean horizontally, boolean leftOrDown) {
        switchToNativeAppContext();

        try {
            Dimension screenSize = getMobileWebDriver().manage().window().getSize();

            int width = screenSize.getWidth();
            int height = screenSize.getHeight();

            log.trace("Screen size: {}, {}", width, height);

            int startX = (int) (width * 0.78);
            int startY = height / 2;
            if (!leftOrDown) {
                startX = (int) (width * 0.22);
            }

            if (!horizontally) {
                startX = width / 2;
                startY = (int) (height * 0.25);

                if (!leftOrDown) {
                    startY = (int) (height * 0.75);
                }
            }

            log.trace("Swipe start point: {}, {}", startX, startY);

            TouchAction action = new TouchAction(getMobileWebDriver());
            action.press(PointOption.point(startX, startY))
                    .moveTo(PointOption.point(width - startX * 2, height - startY * 2))
                    .release()
                    .perform();
        } finally {
            switchToWebViewContext();
        }
    }

    @Override
    public void closeApp() {
        if (browserType == AppType.ANDROID_HYBRID_APP) {
            log.trace("ANDROID: Pressing home button");
            ((AndroidDriver) getMobileWebDriver()).pressKeyCode(AndroidKeyCode.HOME);
        } else if (browserType == AppType.IOS_HYBRID_APP) {
            throw new AppiumException("Automated pressing of home button currently not supported on IOS");
        } else {
            throw new BrowserTypeNotRecognizedException();
        }
    }

    @Override
    public void recentNotificationDescriptionIs(String expectedDescription) {
        log.trace("Notification description check entered");
        List<String> descriptionPaths = addDescriptionXPaths();
        checkForNotificationElement(descriptionPaths, expectedDescription);
        log.trace("Notification description check exited");
    }

    @Override
    public void recentNotificationIs(String expectedBanner) {
        log.trace("Notification banner check entered");
        List<String> bannerPaths = addBannerXPaths();
        checkForNotificationElement(bannerPaths, expectedBanner);
        log.trace("Notification banner check exited");
    }

    private void checkForNotificationElement(List<String> xpaths, String expectedString) {
        int i;
        String foundString;
        swipeForNotificationStack();

        for (i = 0; i < xpaths.size(); i++) {
            try {
                WebControl firstNotification = findElement(ByMobile.xpath(xpaths.get(i)));
                foundString = ((SeleniumElement) firstNotification).getUnderlyingWebElement().getText();
                if (expectedString.equals(foundString)) {
                    log.trace("Correct notification found");
                    return;
                } else {
                    throw new AppiumException("Correct element not found, exiting");
                }
            } catch (NoSuchElementException noElement) {
                log.trace("Element wasn't found, moving on to next path");
            }
        }
        if (i == xpaths.size()) {
            throw new AppiumException("Correct element was not found after all checks");
        }
    }

    private List<String> addBannerXPaths() {
        List<String> bannerPaths = new ArrayList<>();
        //Google Pixel (7.1)
        bannerPaths.add(H_FL1_FRAMELAYOUT1 +
                "android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.FrameLayout[1]/" +
                FL1_FL1_VIEWGROUP1 +
                TEXTVIEW1);

        //Galaxy S7 Edge (6.0.1)
        bannerPaths.add(H_FL1_FRAMELAYOUT1 +
                "android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/" +
                "android.widget.FrameLayout[1]/android.view.ViewGroup[1]/android.widget.FrameLayout[1]/" +
                FL1_FL1_LINEARLAYOUT1 +
                "android.widget.LinearLayout[1]/android.widget.TextView[1]");

        //Galaxy S7 (7.0)
        bannerPaths.add(H_FL1_FRAMELAYOUT2 +
                FL1_FL2_FRAMELAYOUT1 +
                "android.view.ViewGroup[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/" +
                FL1_VG1_TEXTVIEW1);

        //Galaxy Note5 (7.0)
        bannerPaths.add(H_FL1_FRAMELAYOUT2 +
                FL1_FL2_FRAMELAYOUT1 +
                SV1_FL1_FRAMELAYOUT1 +
                FL1_VG1_TEXTVIEW1);

        //Galaxy S8 (7.0)
        bannerPaths.add(H_FL1_FRAMELAYOUT2 +
                FL1_FL2_FRAMELAYOUT1 +
                SV1_FL1_FRAMELAYOUT1 +
                FL1_VG1_TEXTVIEW1);

        //Sony Xperia XA (6.0)
        bannerPaths.add(H_FL1_FRAMELAYOUT1 +
                FL1_FL1_VIEWGROUP1 +
                "android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/" +
                "android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.TextView[1]");
        return bannerPaths;
    }

    private List<String> addDescriptionXPaths() {
        List<String> descriptionPaths = new ArrayList<>();
        //Google Pixel (7.1)
        descriptionPaths.add(H_FL1_FRAMELAYOUT1 +
                "android.widget.FrameLayout[1]/android.widget.ScrollView[1]/android.widget.FrameLayout[1]/" +
                FL1_FL1_LINEARLAYOUT1 +
                "android.widget.LinearLayout[1]/android.widget.TextView[1]");

        //Galaxy S7 (7.0)
        descriptionPaths.add(H_FL1_FRAMELAYOUT2 +
                FL1_FL2_FRAMELAYOUT1 +
                "android.view.ViewGroup[1]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/" +
                FL1_LL1_LINEARLAYOUT1 +
                TEXTVIEW1);

        //Galaxy Note5 (7.0) && Galaxy S8
        descriptionPaths.add(H_FL1_FRAMELAYOUT2 +
                FL1_FL2_FRAMELAYOUT1 +
                SV1_FL1_FRAMELAYOUT1 +
                FL1_LL1_LINEARLAYOUT1 +
                TEXTVIEW1);

        //Galaxy S8 (7.0)
        descriptionPaths.add(H_FL1_FRAMELAYOUT2 +
                FL1_FL2_FRAMELAYOUT1 +
                SV1_FL1_FRAMELAYOUT1 +
                FL1_LL1_LINEARLAYOUT1 +
                TEXTVIEW1);

        //Sony Xperia XA (6.0)
        descriptionPaths.add(H_FL1_FRAMELAYOUT1 +
                FL1_FL1_VIEWGROUP1 +
                "android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/" +
                "android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.TextView[1]");
        return descriptionPaths;
    }

    private void swipeForNotificationStack() {
        log.trace("Swipe for notification entered");
        Dimension screenSize = getMobileWebDriver().manage().window().getSize();
        int width = screenSize.getWidth();
        int height = screenSize.getHeight();
        int xStart = width / 2;
        int yStart = 0;
        int yEnd = (int) (height * .06);
        TouchAction action = new TouchAction(getMobileWebDriver());
        action.longPress(PointOption.point(xStart, yStart))
                .moveTo(PointOption.point(xStart, yEnd))
                .release()
                .perform();
        log.trace("Swipe for notification exited");
    }

    private int getWidgetNumber(int currentYear, int desiredYear) {
        int widgetNumber = 3 - (currentYear - desiredYear);
        if (currentYear < desiredYear) {
            widgetNumber = 3 + (desiredYear - currentYear);
        }
        return widgetNumber;
    }

    private String checkXPathOfYears() {
        //XPath set to emulator default
        return "/hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/" +
                "android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/" +
                "android.widget.FrameLayout/android.widget.DatePicker/android.widget.LinearLayout/" +
                "android.widget.ScrollView/android.widget.ViewAnimator/android.widget.ListView/" +
                "android.widget.TextView[";
    }

    private boolean checkAndClickYearElement(int desiredYear) {
        String xPath = checkXPathOfYears();
        log.info("Found path, continuing with check");
        String desiredYearString = String.valueOf(desiredYear);
        for (int i = 1; i < 8; i++) {
            try {
                WebControl yearLabel = findElement(ByMobile.xpath(xPath + i + "]"));
                String currentYearChecking = ((SeleniumElement) yearLabel).getUnderlyingWebElement().getText();
                if (currentYearChecking.equals(desiredYearString)) {
                    click(yearLabel);
                    return true;
                }
            } catch (NoSuchElementException err) {
                WebControl yearLabel = findElement(ByMobile.id(ANDROID_ID_DATE_PICKER_HEADER_YEAR), false);
                click(yearLabel);
            }
        }
        return false;
    }

    private void swipeAndCheckForYear(int currentYear, int desiredYear) {
        Dimension screenSize = getMobileWebDriver().manage().window().getSize();
        int width = screenSize.getWidth();
        int height = screenSize.getHeight();
        int xStart = width / 2;
        int yStart = height / 2;
        while (!checkAndClickYearElement(desiredYear)) {
            int yEnd = (int) (height * 0.2);
            if (getWidgetNumber(currentYear, desiredYear) > 7) {
                yEnd = yEnd + yStart;
            }
            TouchAction action = new TouchAction(getMobileWebDriver());
            action.longPress(PointOption.point(xStart, yStart))
                    .moveTo(PointOption.point(xStart, yEnd))
                    .release()
                    .perform();
        }
    }

    private void setYearOnAndroidDatePicker(int desiredYear) {
        WebControl yearLabel = findElement(ByMobile.id(ANDROID_ID_DATE_PICKER_HEADER_YEAR), false);
        click(yearLabel);
        int currentYear = Integer.parseInt(((SeleniumElement) yearLabel).getUnderlyingWebElement().getText());
        swipeAndCheckForYear(currentYear, desiredYear);
    }

    private int getMonthNumberOnAndroidDatePicker() {
        List<String> monthList = Arrays.asList("", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        WebControl monthLabel = findElement(ByMobile.id("android:id/date_picker_header_date"), false);
        String monthCurrentlyShown = ((SeleniumElement) monthLabel).getUnderlyingWebElement().getText().substring(5, 8);
        if (monthList.contains(monthCurrentlyShown)) {
            return monthList.indexOf(monthCurrentlyShown);
        }
        return -1;
    }

    private void setMonthOnAndroidDatePicker(LocalDate date) {
        int currentMonth = getMonthNumberOnAndroidDatePicker();
        int desiredMonth = date.getMonthValue();
        WebControl yearLabel = findElement(ByMobile.id(ANDROID_ID_DATE_PICKER_HEADER_YEAR), false);
        if (date.getYear() != Integer.parseInt(((SeleniumElement) yearLabel).getUnderlyingWebElement().getText())) {
            setYearOnAndroidDatePicker(date.getYear());
        }
        if (currentMonth == desiredMonth) {
            return;
        }
        if (currentMonth > desiredMonth) {
            for (int i = 0; i < currentMonth - desiredMonth; i++) {
                WebControl previousMonth = findElement(ByMobile.accessibilityId("Previous month"), false);
                click(previousMonth);
            }
        } else {
            for (int i = 0; i < desiredMonth - currentMonth; i++) {
                WebControl nextMonth = findElement(ByMobile.accessibilityId("Next month"), false);
                click(nextMonth);
            }
        }
    }

    @Override
    public void setDate(LocalDate date) {

        switchToNativeAppContext();

        try {
            if (browserType == AppType.ANDROID_HYBRID_APP) {
                setMonthOnAndroidDatePicker(date);
                WebControl label = findElement(ByMobile.accessibilityId(date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))), false);
                click(label);
                WebControl label1 = findElement(ByMobile.id("android:id/button1"), false);
                click(label1);
            } else {
                WebControl month = findElement(ByMobile.xpath("//XCUIElementTypePickerWheel[1]"), false);
                ((SeleniumElement) month).getUnderlyingWebElement().sendKeys(date.format(DateTimeFormatter.ofPattern("MMMM")));
                WebControl day = findElement(ByMobile.xpath("//XCUIElementTypePickerWheel[2]"), false);
                ((SeleniumElement) day).getUnderlyingWebElement().sendKeys(date.format(DateTimeFormatter.ofPattern("d")));
                WebControl year = findElement(ByMobile.xpath("//XCUIElementTypePickerWheel[3]"), false);
                ((SeleniumElement) year).getUnderlyingWebElement().sendKeys(date.format(DateTimeFormatter.ofPattern("yyyy")));
            }
        } finally {
            switchToWebViewContext();
        }
    }

    @Override
    public void mobileSelect(MobileSelectOption selectOption, String value) {
        switchToNativeAppContext();

        try {
            if (browserType == AppType.ANDROID_HYBRID_APP) {
                IByMobile selector = ByMobile.xpath(String.format("//android.widget.CheckedTextView[@text='%s']", value));
                click(findElement(selector, false));
            } else {
                WebControl element = findElement(ByMobile.xpath("//XCUIElementTypePickerWheel[1]"), false);
                ((SeleniumElement) element).getUnderlyingWebElement().sendKeys(value);
                click(findElement(ByMobile.accessibilityId("Done"), false));
            }
        } finally {
            switchToWebViewContext();
        }
    }

    /**
     * Scrolls the element specified by the provided 'selector' into view.
     *
     * @param selector Element to scroll into view.
     */
    @Override
    protected void scrollElementIntoView(IByWeb selector) {
        if (selector instanceof IByMobile) {
            return;
        }

        super.scrollElementIntoView(selector);
    }

    @Override
    public void acceptAlert() {
        switch (browserType.getKey()) {
            case ANDROID_HYBRID_APP:
                // Break intentionally omitted
            case IOS_HYBRID_APP:
                switchToNativeAppContext();
                try {
                    super.acceptAlert();
                } finally {
                    switchToWebViewContext();
                }
                break;
            default:
                super.acceptAlert();
        }
    }

    @Override
    public void dismissAlert() {
        switch (browserType.getKey()) {
            case ANDROID_HYBRID_APP:
                // Break intentionally omitted
            case IOS_HYBRID_APP:
                switchToNativeAppContext();
                try {
                    super.dismissAlert();
                } finally {
                    switchToWebViewContext();
                }
                break;
            default:
                super.dismissAlert();
        }
    }

    @Override
    public void acceptOrDismissPermissionDialog(boolean accept) {
        if (accept) {
            if (browserType == AppType.ANDROID_HYBRID_APP) {
                SeleniumElement element = (SeleniumElement) findElement(ByMobile.id("com.android.packageinstaller:id/permission_allow_button"));
                element.click();
            } else {
                acceptAlert();
            }
        } else {
            if (browserType == AppType.ANDROID_HYBRID_APP) {
                SeleniumElement element = (SeleniumElement) findElement(ByMobile.id("com.android.packageinstaller:id/permission_deny_button"));
                element.click();
            } else {
                dismissAlert();
            }
        }
    }

    @Override
    public void mobileClick(WebControl control) {
        tapOnControl(control);
    }

    @Override
    public void switchToWebView(IByWeb selector) {

        if (selector == null) {
            switchToWebViewContext();

            return;
        }

        log.trace("switchToWebView({})", selector);
        Set<String> availableContexts = getMobileWebDriver().getContextHandles();
        String joinedString = String.join(", ", availableContexts);
        log.trace("Available contexts: {}", joinedString);
        if (availableContexts.size() > 1) {
            for (String availableContext : availableContexts) {
                if (!availableContext.contains(NATIVE_APP) && availableContext.startsWith("WEBVIEW")) {
                    log.trace("Switching to context {}", availableContext);
                    getMobileWebDriver().context(availableContext);
                    log.trace("Switched to context");

                    try {
                        findElement(selector);
                    } catch (NoSuchElementException | NoSuchElementsException e) {
                        log.trace(e.getMessage());

                        continue;
                    }

                    return;
                }
            }
        }

        throw new AppiumException("Unable to find matching web view");
    }

    /**
     * Finds the first element that matches the corresponding IBy.
     *
     * @param findBy Selector used to search with.
     * @return An IWebElementAdapter matching the findBy.
     */
    public WebControl findElement(IBy findBy) {
        return findElement(findBy, true);
    }

    /**
     * Finds the first element that matches the corresponding IBy.
     *
     * @param findBy Selector used to search with.
     * @return An IWebElementAdapter matching the findBy.
     */
    private WebControl findElement(IBy findBy, boolean switchContext) {
        if (!(findBy instanceof IByMobile)) {
            return super.findElement(findBy);
        }

        if (switchContext) {
            switchToNativeAppContext();
        }

        if (findBy instanceof IByMobileXPath) {
            log.trace("WebDriver.findElement(by.xpath({}));", findBy);
            try {
                return new SeleniumElement(webDriver.findElement(org.openqa.selenium.By.xpath(findBy.toString())));
            } catch (org.openqa.selenium.NoSuchElementException e) {
                throw new NoSuchElementException(e, findBy);
            } finally {
                if (switchContext) {
                    switchToWebViewContext();
                }
            }
        }

        if (findBy instanceof ByMobileId) {
            String formattedByID = String.format("WebDriver.findElement(by.id(%1$s));", findBy);
            log.trace(formattedByID);
            try {
                return new SeleniumElement(webDriver.findElement(org.openqa.selenium.By.id(findBy.toString())));
            } catch (org.openqa.selenium.NoSuchElementException e) {
                throw new NoSuchElementException(e, findBy);
            } finally {
                if (switchContext) {
                    switchToWebViewContext();
                }
            }
        }

        log.trace("WebDriver.findElement(by.accessbilityId({}));", findBy);
        try {
            return new SeleniumElement(((MobileDriver) webDriver).findElementByAccessibilityId(findBy.toString()));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            throw new NoSuchElementException(e, findBy);
        } finally {
            if (switchContext) {
                switchToWebViewContext();
            }
        }
    }

    /**
     * Performs a LeftClick on the element passed as an argument.
     *
     * @param element The element to perform the click on.
     */
    public final void click(WebControl element) {
        if (element.getSelector() instanceof IByMobile) {
            switchToNativeAppContext();
            try {
                super.click(element);
            } finally {
                switchToWebViewContext();
            }

            return;
        }

        super.click(element);
    }

    @Override
    public void set(WebControl control, WebSelectOption option, String setValue) {
        if (control.getSelector() instanceof IByMobile) {
            switchToNativeAppContext();
            try {
                sendKeysToElement(control, setValue);
            } finally {
                switchToWebViewContext();
            }

            return;
        }

        super.set(control, option, setValue);
    }

    @Override
    public final void quit() {
        log.trace("AppiumWebDriver.quit();");
        if (browserType != BrowserType.ANDROID_CHROME
                && browserType != BrowserType.IOS_SAFARI
                && browserType != AppType.ANDROID_HYBRID_APP
                && browserType != AppType.IOS_HYBRID_APP) {
            super.quit();

            return;
        }

        try {
            getMobileWebDriver().closeApp();
        } catch (Exception e) {
            log.trace("Failed to close app.", e);
        } finally {
            getMobileWebDriver().quit();
        }
    }

    private void switchToNativeAppContext() {
        log.trace("Switching to native app context {}", NATIVE_APP);
        getMobileWebDriver().context(NATIVE_APP);
    }

    private void switchToWebViewContext() {
        log.trace("Switching to web view context {}", context);
        getMobileWebDriver().context(context);
    }

    private void tapOnControl(WebControl control) {

        log.trace("tap on control via coordinates");

        WebElement underlyingWebElement = ((SeleniumElement) control).getUnderlyingWebElement();
        Point webElementLocation = underlyingWebElement.getLocation();
        log.trace("elementLocation: {}, {}", webElementLocation.getX(), webElementLocation.getY());
        Dimension elementSize = underlyingWebElement.getSize();
        log.trace("elementSize: {}, {}", elementSize.getWidth(), elementSize.getHeight());

        long webRootWidth = (long) executeScript("return screen.availWidth");
        long webRootHeight = (long) executeScript("return screen.availHeight");
        log.trace("screenSize: {}, {}", webRootWidth, webRootHeight);

        int windowWidth;
        int windowHeight;
        try {
            switchToNativeAppContext();
            Dimension windowSize;
            try {
                windowSize = getMobileWebDriver().manage().window().getSize();
            } finally {
                switchToWebViewContext();
            }
            windowWidth = windowSize.getWidth();
            windowHeight = windowSize.getHeight();
            if (browserType == AppType.ANDROID_HYBRID_APP && mobileDeviceResolutions.containsKey(windowWidth)
                    && (windowHeight - mobileDeviceResolutions.get(windowWidth)) < 300) {
                windowHeight = mobileDeviceResolutions.get(windowWidth);
            }
        } catch (WebDriverException e) {
            log.trace(e.getMessage());
            windowWidth = (int) webRootWidth;
            windowHeight = 1920;
        }

        log.trace("windowSize: {}, {}", windowWidth, windowHeight);

        double xRatio = windowWidth * 1.0 / webRootWidth;
        double yRatio = windowHeight * 1.0 / webRootHeight;
        int pointX = webElementLocation.getX() + elementSize.getWidth() / 2;
        int pointY = webElementLocation.getY() + elementSize.getHeight() / 2;
        Point tapPoint = new Point((int) (pointX * xRatio), (int) (pointY * yRatio));

        log.trace("tapPoint: {}, {}", tapPoint.getX(), tapPoint.getY());
        switchToNativeAppContext();
        try {
            TouchAction a = new TouchAction((AppiumDriver) getWebDriver());
            a.tap(PointOption.point(tapPoint.getX(), tapPoint.getY())).perform();
        } finally {
            switchToWebViewContext();
        }
    }
}
