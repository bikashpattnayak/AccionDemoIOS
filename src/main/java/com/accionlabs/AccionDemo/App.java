package com.accionlabs.AccionDemo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;

/**
 * @author AL1984
 *
 */
public class App {
	public static String remoteURL = "http://127.0.0.1:4723/wd/hub";
	public static void main(String[] args) throws MalformedURLException  {

		IOSDriver<IOSElement> driver = null;
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "TestSimulator");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "IOS");
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST);
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.3");
		capabilities.setCapability(MobileCapabilityType.APP, "//Users//bikash//Desktop//Appium//TRA-Client-iOS 2.app");
		capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
		driver = new IOSDriver<IOSElement>(new URL(App.remoteURL), capabilities);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		try {
			 driver = loginToApp(driver, "traqatest5@gmail.com", "welcometr1", "9546532543");
			updateBusinessCard(driver);
			updateAutomaticCallback(driver);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}

	}

	
	/**
	 * @param driver
	 *            Background: Given User is on login page
	 * 
	 *            Scenario User is able to update Business Card When User navigates
	 *            to Business Card option Then it should be able to update
	 *            salutation
	 * @throws InterruptedException
	 * 
	 * 
	 */
	public static void updateBusinessCard(IOSDriver<IOSElement> driver) throws InterruptedException {
		// click on `Daily View` menu -> Settings
		openSidePanelOption(driver, "Settings");

		// click on `Settings` -> `My Account`
		openSettingsSubOption(driver, "My Account");

		// click on `My Account` sub-menu items
		openSettingsSubOptions_Option(driver, "My Account", "Business Card");

		// Update the salutation text
		String retSaluation = updateSalutation(driver);
		boolean retResult = verifySalutation(driver, retSaluation);

		GoToHomeScreen(driver);
	}

	/**
	 * @param driver
	 *            Background: Given User is on login page
	 * 
	 *            Scenario User is able to enable or disable Automatic Callback
	 *            When User navigates to Automatic Callback Then it should be able to toggle it
	 *            And the updated value should reflect.
	 * @throws InterruptedException 
	 * 
	 */
	public static void updateAutomaticCallback(IOSDriver<IOSElement> driver) {
		openSidePanelOption(driver, "Settings");
		openSettingsSubOption(driver, "Joining Options");
		IOSElement callbackElement = driver.findElementByXPath("//XCUIElementTypeStaticText[@name=\"Automatic Callback\"]/..");
		MobileElement statusElement = callbackElement.findElementsByClassName("XCUIElementTypeStaticText").get(1);
		String initialValue = statusElement.getAttribute("label").trim();
		openSettingsSubOptions_Option(driver, "Joining Options", "Automatic Callback");

		IOSElement element = driver.findElementByXPath("//XCUIElementTypeNavigationBar[@name='Automatic Callback']/..");
		List<MobileElement> elements = element.findElementsByClassName("XCUIElementTypeSwitch");
		for(MobileElement e: elements) {
			if(e.getAttribute("name").contains("AutomaticCall") || e.getAttribute("name").contains("AutoCallAllDay")) {
				e.click();
				break;
			}
		}
		if(initialValue.equals("OFF")) {
			driver.findElementByXPath("//XCUIElementTypeStaticText[@name='Callback Number']").click();
			driver.findElementByXPath("//XCUIElementTypeStaticText[@name='Mobile 1']").click();
		}
		
		driver.findElementById("SaveButton").click();
		String finalValue = statusElement.getAttribute("label").trim();
		boolean retResult = false;
		if(initialValue.equals("ON")  && finalValue.equals("OFF")){
			retResult = true;
		} else if(initialValue.equals("OFF")  && finalValue.equals("ON")){
			retResult = true;
		} else {
			retResult = false;
		}
			
		System.out.println("Validation result "+ retResult);

		GoToHomeScreen(driver);

	}

	public static void openSidePanelOption(IOSDriver<IOSElement> driver, String option) {
		WebDriverWait wait = new WebDriverWait(driver, 120);
		wait.until(ExpectedConditions
				.presenceOfElementLocated(By.id("ToggleSettingsButton")));
		// click on Daily View menu -> Settings
		driver.findElementByAccessibilityId("ToggleSettingsButton").click();
		List<IOSElement> menuItems = driver.findElementsByClassName("XCUIElementTypeStaticText");
		for (IOSElement element : menuItems) {
			if (element.getAttribute("label") != null && element.getAttribute("label").contains(option)) {
				element.click();
				break;
			}
		}
	}

	public static void openSettingsSubOption(IOSDriver<IOSElement> driver, String option)  {
		if (driver.findElementsByXPath("//XCUIElementTypeNavigationBar[@name='Settings']").size() == 1) {
			List<IOSElement> subMenuItems = driver.findElementsByClassName("XCUIElementTypeCell");
			for (IOSElement element : subMenuItems) {
				if (element.getAttribute("label") != null && element.getAttribute("label").equals(option)) {
					element.click();
					break;
				}
			}
		}
	}

	public static void openSettingsSubOptions_Option(IOSDriver<IOSElement> driver, String option, String subOptions) {
		if (driver.findElementsByXPath("//XCUIElementTypeNavigationBar[@name='" + option + "']").size() == 1) {
			List<IOSElement> myAccountMenuItems = driver.findElementsByClassName("XCUIElementTypeStaticText");
			for (IOSElement element : myAccountMenuItems) {
				if (element.getAttribute("label") != null && element.getAttribute("label").contains(subOptions)) {
					element.click();
					break;
				}
			}
		}
	}

	public static String updateSalutation(IOSDriver<IOSElement> driver) {
		String saluationValue = driver.findElementsByXPath("//XCUIElementTypeStaticText[@name='Title']/../XCUIElementTypeStaticText").get(1).getAttribute("label");
		if(!(saluationValue.equals("Title") || saluationValue.equals("Miss"))) {
			if(!saluationValue.endsWith("."))
				saluationValue = saluationValue + ".";
		}
		System.out.println("Original Salutation Value "+ saluationValue);
		driver.findElementByXPath("//XCUIElementTypeStaticText[@name='Title']").click();
		List<String> saluation = new ArrayList<String>();

		IOSElement thisElement = driver.findElementByXPath("//XCUIElementTypeNavigationBar[@name='Title']/..");
		Random ran = new Random();
		int x = ran.nextInt(5) + 1;
		System.out.println("Random Number Generated " + x);
		List<MobileElement> salutationListItems = thisElement.findElementsByClassName("XCUIElementTypeStaticText");

		for (MobileElement ele : salutationListItems) {
			saluation.add(ele.getAttribute("label"));
		}
		
		System.out.println("List "+saluation);
		
		//If the already salutation value is the random generated value, keep iterating till a unique value
		while(true) {
			int temp = saluation.indexOf(saluationValue);
			System.out.println("Index of Original value "+ temp);
			if(temp == x) 
				x = ran.nextInt(5) + 1;
			else
				break;
		}
		
		
		for (MobileElement ele : salutationListItems) {
			if (ele.getAttribute("label").contains(saluation.get(x))) {
				ele.click();
				driver.findElementByAccessibilityId("BusinessCard_SaveButton").click();
				break;
			}
		}

		// XCUIElementTypeStaticText[@name="My Account"]
		WebDriverWait wait = new WebDriverWait(driver, 120);
		wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//XCUIElementTypeStaticText[@name='My Account']")));
		return saluation.get(x);
	}

	public static boolean verifySalutation(IOSDriver<IOSElement> driver, String expected) {
		openSettingsSubOptions_Option(driver, "My Account", "Business Card");
		IOSElement element = driver.findElementByXPath("//XCUIElementTypeStaticText[@name='Title']/..");
		List<MobileElement> elements = element.findElementsByClassName("XCUIElementTypeStaticText");
		if (elements.get(1).getAttribute("label").contains(expected))
			return true;

		return false;
	}

	public static void createWebinars(AndroidDriver<AndroidElement> driver, String name, String url) {
		driver.findElementById("com.thinkrite.assistant:id/floatingActionButton").click();
		driver.findElementById("com.thinkrite.assistant:id/joiningOptionLabelEditText").setValue(name);
		driver.findElementById("com.thinkrite.assistant:id/joiningOptionValueEditText").setValue(url);
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.attributeContains(By.id("com.thinkrite.assistant:id/save"), "enabled", "true"));
		driver.findElementById("com.thinkrite.assistant:id/save").click();
	}

	

	// Go back to Home screen of the application (Either Daily View or
	// Notifications page)
	public static void GoToHomeScreen(IOSDriver<IOSElement> driver) {
		while (true) {
			String title;
			try {
				title = driver.findElementByClassName("XCUIElementTypeNavigationBar").getAttribute("name");
			} catch (Exception e) {
				break;
			}
			if (title.equals("Settings")) {
				driver.navigate().back();
				driver.findElementByXPath("//XCUIElementTypeStaticText[@name='Daily View']").click();
				break;
			} else {
				driver.navigate().back();
			}

		}
	}

	public static IOSDriver<IOSElement> loginToApp(IOSDriver<IOSElement> driver, String email, String pwd, String number)
			throws InterruptedException {

		if (driver.findElementsByXPath("//XCUIElementTypeStaticText[@name='Sign In']").size() == 1) {
			driver.findElementByAccessibilityId("emailAddress_textfield").clear();
			driver.findElementByAccessibilityId("emailAddress_textfield").setValue(email);
			driver.findElementByAccessibilityId("password_textfield").click();
			driver.findElementByAccessibilityId("password_textfield").clear();
			driver.findElementByAccessibilityId("password_textfield").setValue(pwd);
			driver.findElementByAccessibilityId("numberInputTextField").clear();
			driver.findElementByAccessibilityId("numberInputTextField").setValue(number);

			WebDriverWait wait = new WebDriverWait(driver, 100);
			wait.until(ExpectedConditions.attributeContains(By.id("signInButton"), "enabled", "true"));
			driver.findElementByAccessibilityId("signInButton").click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendar_checkbox")));

			if (driver.findElementsByAccessibilityId("calendar_checkbox").size() >= 1) {
				driver.findElementByAccessibilityId("calendar_checkbox").click();
				driver.switchTo().alert().accept();
				driver.findElementByAccessibilityId("Done").click();
				driver.findElementByAccessibilityId("okayAllowNotifications_button").click();
				driver.switchTo().alert().accept();
				driver.findElementByAccessibilityId("okayAllowNotifications_button").click();
				driver.switchTo().alert().accept();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Coach1")));
				driver.closeApp();
				try {
					DesiredCapabilities capa = new DesiredCapabilities(driver.getCapabilities());
					capa.setCapability(MobileCapabilityType.NO_RESET, true);
					driver = new IOSDriver<IOSElement>(new URL(App.remoteURL),capa);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (driver.findElementsByXPath("//XCUIElementTypeStaticText[@name='Sign In']").size() == 1) {
					driver.findElementByAccessibilityId("emailAddress_textfield").clear();
					driver.findElementByAccessibilityId("emailAddress_textfield").setValue(email);
					driver.findElementByAccessibilityId("password_textfield").click();
					driver.findElementByAccessibilityId("password_textfield").clear();
					driver.findElementByAccessibilityId("password_textfield").setValue(pwd);
					driver.findElementByAccessibilityId("numberInputTextField").clear();
					driver.findElementByAccessibilityId("numberInputTextField").setValue(number);
					
					wait.until(ExpectedConditions.attributeContains(By.id("signInButton"), "enabled", "true"));
					driver.findElementByAccessibilityId("signInButton").click();
				}
				return driver;
			}
			
		}
		return driver;

	}

	public static void acceptCalendarAlert(AndroidDriver<AndroidElement> driver) {
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.id("com.android.packageinstaller:id/permission_allow_button")));
		driver.findElementById("com.android.packageinstaller:id/permission_allow_button").click();
	}

}
