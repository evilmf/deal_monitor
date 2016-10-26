package com.sales.af.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.util.Util;

@Service
public class AfCrawler implements Crawler {

	private final static Logger LOGGER = Logger.getLogger(AfCrawler.class);
	private static final String IMAGE_URL = "https://anf.scene7.com/is/image/anf/anf_%s_01_prod1";
	private static final String HOME_PAGE_URL = "https://www.abercrombie.com";
	private static final String BRAND_NAME = "Abercrombie & Fitch";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${afHomePage}")
	private String afHomePage;

	@Value("${afProductAPI}")
	private String afProductAPI;

	@Value("${minDiscount}")
	private Float minDiscount;

	@Override
	public Set<SnapshotDetailTo> getProducts() {
		/* Add exception handling here. It should handle all exception so that 
		 * throwing exception here won't block the rest of the crawler from 
		 * crawling.
		 * */
		Set<UrlInfo> urlInfoSet = getUrlInfo();

		return crawl(urlInfoSet);
	}

	private Set<SnapshotDetailTo> crawl(Set<UrlInfo> urlInfoSet) {
		Map<String, SnapshotDetailTo> products = new HashMap<String, SnapshotDetailTo>();

		String url;
		String res;
		DocumentContext docCtx;
		for (UrlInfo urlInfo : urlInfoSet) {
			url = String.format(afProductAPI, urlInfo.storeId, urlInfo.catalogId, urlInfo.categoryId);
			try {
				res = restTemplate.getForObject(url, String.class);
				docCtx = JsonPath.parse(res);
				int numOfProducts = (Integer) docCtx.read("$.categories[0].products.length()");
				LOGGER.info(String.format("Number of products found for category id %s gender %s: %s",
						urlInfo.categoryId, urlInfo.gender, numOfProducts));
				LOGGER.info(url);
				String productJsonPath = "$.categories[0].products[%s].%s";
				
				for(Integer i = 0; i < numOfProducts; i++) {
					try {
						String productDataId = ((Integer) docCtx.read(String.format(productJsonPath, i.toString(), "id"))).toString();
						String productName = (String) docCtx.read(String.format(productJsonPath, i, "name"));
						String productImageUrl = String.format(IMAGE_URL, ((Integer) docCtx.read(String.format(productJsonPath, i, "productCollection"))).toString());
						String productUrl = HOME_PAGE_URL + (String) docCtx.read(String.format(productJsonPath, i, "productUrl"));
						String genderName = urlInfo.gender;
						Float offerPrice = Float.parseFloat(StringUtils.strip((String) docCtx.read(String.format(productJsonPath, i, "price.priceLow")), "$"));
						Float listPrice = Float.parseFloat(StringUtils.strip((String) docCtx.read(String.format(productJsonPath, i, "price.lowListPrice")), "$"));
						Boolean showSalePrice = (Boolean) docCtx.read(String.format(productJsonPath, i, "price.showSalePrice"));
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
						snapshotDetailTo.setProductDataId(productDataId);
						snapshotDetailTo.setBrandName(BRAND_NAME.toLowerCase());
						
						products.put(productDataId, snapshotDetailTo);
					}
					catch(Exception e) {
						LOGGER.info("Error getting product info.");
						LOGGER.error("Error getting product", e);
					}
				}
			}
			catch(Exception e) {
				LOGGER.info(String.format("Error getting product on page %s", url));
				LOGGER.error("Error getting product", e);
			}
		}

		return new HashSet<SnapshotDetailTo>(products.values());
	}

	private Set<UrlInfo> getUrlInfo() {
		Set<UrlInfo> urlInfoSet = new HashSet<UrlInfo>();
		UrlInfo urlInfo;

		Document doc;
		try {
			doc = Util.getConnWithUserAgent(afHomePage).get();
			
			Element storeElement = doc.select("input[name=storeId]").first();
			Long storeId = storeElement != null ? Long.parseLong(storeElement.attr("value")) : 10051L;

			Element catalogElement = doc.select("input[name=catalogId]").first();
			Long catalogId = catalogElement != null ? Long.parseLong(catalogElement.attr("value")) : 10901L;

			for (String g : Arrays.asList("Mens", "Womens")) {
				Elements menElements = doc.select(String.format("ul>li:matches(%s) ul>li>a:contains(clearance)", g));
				for (Element e : menElements) {
					urlInfo = new UrlInfo();
					urlInfo.gender = g.toLowerCase();
					urlInfo.storeId = storeId;
					urlInfo.catalogId = catalogId;
					urlInfo.categoryId = Long.parseLong(e.parent().attr("id").split("-")[1]);

					urlInfoSet.add(urlInfo);
				}
			}
			
			if(urlInfoSet.isEmpty()) {
				Map<Long, String> defaultCategories = new HashMap<Long, String>() {
					private static final long serialVersionUID = 2740729364185064202L;
					{
						put(6234532L,"Womens");
						put(6234533L,"Mens");
						put(12205L,"Womens");
						put(12204L,"Mens");
					}
				};
				
				for(Long categoryId : defaultCategories.keySet()) {
					urlInfo = new UrlInfo();
					urlInfo.gender = defaultCategories.get(categoryId).toLowerCase();
					urlInfo.storeId = storeId;
					urlInfo.catalogId = catalogId;
					urlInfo.categoryId = categoryId;

					urlInfoSet.add(urlInfo);
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}

		return urlInfoSet;
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
