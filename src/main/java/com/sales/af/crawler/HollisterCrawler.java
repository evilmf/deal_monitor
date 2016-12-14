package com.sales.af.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.util.Util;


public class HollisterCrawler implements Crawler {
	private final static Logger LOGGER = Logger.getLogger(HollisterCrawler.class);
	
	private static final String BRAND_NAME = "Hollister";
	private static final String HOME_PAGE_URL = "https://www.hollisterco.com";
	private static final String IMAGE_URL = "https://anf.scene7.com/is/image/anf/hol_%s_01_prod1";
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${hollisterHomePage}")
	private String hollisterHomePage;

	@Value("${hollisterProductAPI}")
	private String hollisterProductAPI;
	
	@Value("${minDiscount}")
	private Float minDiscount;
	
	@Override
	public Set<SnapshotDetailTo> getProducts() {
		Set<UrlInfo> urlInfoSet = getUrlInfo();

		return crawl(urlInfoSet);
	}
	
	private Set<SnapshotDetailTo> crawl(Set<UrlInfo> urlInfoSet) {
		Map<String, SnapshotDetailTo> products = new HashMap<String, SnapshotDetailTo>();
		
		String url;
		String res;
		DocumentContext docCtx;
		for (UrlInfo urlInfo : urlInfoSet) {
			url = String.format(hollisterProductAPI, urlInfo.storeId, urlInfo.catalogId, urlInfo.categoryId,
					urlInfo.categoryId);
			try {
				res = restTemplate.getForObject(url, String.class);
				docCtx = JsonPath.parse(res);

				LOGGER.info(url);
				
				String productJsonPath = "$.categories[%s].products[%s].%s";
				int numOfCategories = (Integer) docCtx.read("$.categories.length()");
				for(int c = 0; c < numOfCategories; c++) {
					Long numOfProducts = new Long((Integer) docCtx.read(String.format("$.categories[%s].products.length()", c)));
					LOGGER.info(String.format("Number of products found for category id %s gender %s: %s",
							urlInfo.categoryId, urlInfo.gender, numOfProducts));
					for (Long i = 0L; i < numOfProducts; i++) {
						try {
							String productDataId = ((Integer) docCtx.read(String.format(productJsonPath, c, i.toString(), "id"))).toString();
							String productName = (String) docCtx.read(String.format(productJsonPath, c, i, "name"));
							String productImageUrl = String.format(IMAGE_URL, ((Integer) docCtx.read(String.format(productJsonPath, c, i, "productCollection"))).toString());
							String productUrl = HOME_PAGE_URL + (String) docCtx.read(String.format(productJsonPath, c, i, "productUrl"));
							String genderName = urlInfo.gender;
							Float offerPrice = Float.parseFloat(StringUtils.strip((String) docCtx.read(String.format(productJsonPath, c, i, "price.priceLow")), "$"));
							Float listPrice = Float.parseFloat(StringUtils.strip((String) docCtx.read(String.format(productJsonPath, c, i, "price.lowListPrice")), "$"));
							Boolean showSalePrice = (Boolean) docCtx.read(String.format(productJsonPath, c, i, "price.showSalePrice"));
							String categoryName = getCategoryName(productName).toLowerCase();
		
							if (showSalePrice == null || !showSalePrice) {
								continue;
							}
		
							if (offerPrice == null || listPrice == null || offerPrice == 0 || listPrice == 0
									|| offerPrice.equals(listPrice)) {
								continue;
							}
							
							float discount = (listPrice - offerPrice) / listPrice;
							if(discount <= minDiscount) {
								continue;
							}
							
							SnapshotDetailTo snapshotDetailTo = new SnapshotDetailTo();
							List<String> images = new ArrayList<String>();
							images.add(productImageUrl);
	
							snapshotDetailTo.setProductName(productName);
							snapshotDetailTo.setImages(images);
							snapshotDetailTo.setPriceRegular(listPrice);
							snapshotDetailTo.setPriceDiscount(offerPrice);
							snapshotDetailTo.setCategoryName(categoryName);
							snapshotDetailTo.setGenderName(genderName);
							snapshotDetailTo.setProductUrl(productUrl);
							snapshotDetailTo.setProductDataId(productDataId.toString());
							snapshotDetailTo.setBrandName(BRAND_NAME.toLowerCase());
							
							products.put(productDataId, snapshotDetailTo);
							
						} catch (Exception e) {
							LOGGER.info("Error getting product info.");
							LOGGER.error("Error getting product", e);
						} 
					}
				}

			} catch (Exception e) {
				LOGGER.info(String.format("Error getting product on page %s", url));
				LOGGER.error("Error getting product", e);
			}
		}
		
		return new HashSet<SnapshotDetailTo>(products.values());
	}
	
	private Set<UrlInfo> getUrlInfo() {
		Set<UrlInfo> urlInfoList = new HashSet<UrlInfo>();
		UrlInfo urlInfo;
		try {
			Document doc = Util.getConnWithUserAgent(hollisterHomePage).get();
	
			Element storeElement = doc.select("form#search-input-form-desktop input#storeId").first();
			Long storeId = Long.parseLong(storeElement.attr("value"));
	
			Element catalogElement = doc.select("form#search-input-form-desktop input#catalogId").first();
			Long catalogId = Long.parseLong(catalogElement.attr("value"));
			
			Elements girlsElements = doc.select(
					"li#nav-primary-girls li.nav-item>a.nav-link--major:contains(sale), li#nav-primary-girls li.nav-item>a.nav-link--major:contains(clearance)");
			for (Element e : girlsElements) {
				urlInfo = new UrlInfo();
				urlInfo.gender = "girls";
				urlInfo.storeId = storeId;
				urlInfo.catalogId = catalogId;
				urlInfo.categoryId = Long.parseLong(e.parent().attr("data-catid"));
	
				urlInfoList.add(urlInfo);
			}
	
			Elements guysElements = doc.select(
					"li#nav-primary-guys li.nav-item>a.nav-link--major:contains(sale), li#nav-primary-guys li.nav-item>a.nav-link--major:contains(clearance)");
			for (Element e : guysElements) {
				urlInfo = new UrlInfo();
				urlInfo.gender = "guys";
				urlInfo.storeId = storeId;
				urlInfo.catalogId = catalogId;
				urlInfo.categoryId = Long.parseLong(e.parent().attr("data-catid"));
	
				urlInfoList.add(urlInfo);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

		return urlInfoList;
	}
	
	private String getCategoryName(String productName) {
		String[] names = productName.split("[^a-zA-Z-]");
		String categoryName = names[names.length - 1];

		return categoryName == null || categoryName.length() == 0 ? "other" : categoryName;
	}
	
	private static class UrlInfo {
		String gender;
		Long categoryId;
		Long storeId;
		Long catalogId;
	}
}
