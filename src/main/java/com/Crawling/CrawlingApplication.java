package com.Crawling;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class CrawlingApplication {
	public static void main(String[] args) throws InterruptedException {
		// 크롤링 크롬 드라이버
		WebDriver driver;
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();

		String url = "https://www.vivino.com/explore?e=eJwdi7sOgCAMAP-mswyOjR_" +
				"g5uJoKhRCIsUUMPL3Pm643HKT7TgvYF-tEDxepJErHZAUDeS9o1KNEspmc5MKWR06LhYS3TgOH5CioPnrfJ8mjn0UdsjyAOxsH3E";

		driver.get(url);

		// 와인 타입 선택
		WebElement isub = driver.findElement(By.xpath("//*[@id=\"explore-page-app\"]/div/div/div[2]/div[1]/div/div[1]/div[2]/label[1]"));
		isub.click();

		// 필터 셀렉트박스
		WebElement isub2 = driver.findElement(By.xpath("//*[@id=\"explore-page-app\"]/div/div/div[1]/div[2]/div"));
		isub2.click();

		// 필터 셀렉트박스 -> popular 선택
		WebElement isub3 = driver.findElement(By.xpath("//*[@id=\"menu-\"]/div[3]/ul/li[7]"));
		isub3.click();

		// 스크롤링
		JavascriptExecutor js = (JavascriptExecutor) driver;
		int move = 800;
		for(int i=0;;i++){
			String s = "window.scrollTo(" + move * i +","+ move * (i+1) +")";
			js.executeScript(s);
			Thread.sleep(2000);
			if(i==2){
				break;
			}
		}

		// 크롤링 할 영역 선택
		List<WebElement> products = driver.findElements(By.cssSelector("div.wineCard__topSection--11oVj"));

		// Json 만들기
		JSONObject jsonObject = new JSONObject();
		JSONArray wine_array = new JSONArray();

		jsonObject.put("site_url", url);

		String imgUrl = null;
		String winary = null;
		String wineName = null;
		String wineRegion = null;
		String avgRate = null;
		String avgPrice= null;

		for(WebElement e : products) {
			JSONObject data = new JSONObject();

			imgUrl = e.findElement(By.cssSelector(".wineCard__bottleSection--3Bzic img")).getAttribute("src");
			winary = e.findElement(By.cssSelector(".wineInfoVintage__truncate--3QAtw")).getText();
			wineName = e.findElement(By.cssSelector(".wineInfoVintage__vintage--VvWlU")).getText();
			wineRegion = e.findElement(By.cssSelector(".wineInfoLocation__regionAndCountry--1nEJz")).getText();
			avgRate = e.findElement(By.cssSelector(".vivinoRating_averageValue__uDdPM")).getText();
			avgPrice = e.findElement(By.cssSelector(".addToCart__ppcPrice--ydrd5")).getText();

			// avgPrice => Available online from ₩18,960
			String[] parts = avgPrice.split("\\u20A9"); // ₩ 기호를 기준으로 문자열 분할
			String price = parts[1].replaceAll("[^0-9]", ""); // 숫자만 추출

			data.put("wine_name", wineName);
			data.put("winary", winary);
			data.put("wine_region", wineRegion);
			data.put("wine_rate", avgRate);
			data.put("wine_price", price);
			data.put("wine_img_url", imgUrl);

			wine_array.add(data);
		}
		jsonObject.put("RED", wine_array);
	}
}
