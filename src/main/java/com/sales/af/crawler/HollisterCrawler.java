package com.sales.af.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.to.SnapshotTo;
import com.sales.af.util.Util;

public class HollisterCrawler extends ProductQueue {
	private static Logger logger = Logger.getLogger(DataLoader.class);

	private static final int size = 1;
	private static Semaphore isRunnable = new Semaphore(size);
	private static final String brandNameHol = "Hollister";
	private static final String brandUrlHol = "https://www.hollisterco.com";
	private static final String imageUrlHol = "https://anf.scene7.com/is/image/anf/hol_%s_01_prod1";

	private SnapshotTo allProducts;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${hollisterHomePage}")
	private String hollisterHomePage;

	@Value("${hollisterProductAPI}")
	private String hollisterProductAPI;
	
	@Value("${minDiscount}")
	private Float minDiscount;

	public void crawl() throws InterruptedException, IOException {
		long startTime = System.currentTimeMillis();
		logger.info(String.format("Start crawling with %s", HollisterCrawler.class.getName()));
		
		if (!isRunnable.tryAcquire()) {
			logger.info(String.format("%s is already running. Skip crawling.", HollisterCrawler.class.getName()));
			return;
		}
		
		allProducts = new SnapshotTo();
		allProducts.setSnapshotDetail(new HashMap<Long, SnapshotDetailTo>());

		try {
			getProducts();

			if (!allProducts.getSnapshotDetail().isEmpty()) {
				logger.info(String.format("Enqueuing Brand: %s; Products Found: %s", brandNameHol.toLowerCase(),
						allProducts.getSnapshotDetail().size()));

				productQueue.add(allProducts);
			}

		} catch (Exception e) {
			logger.error("Got exception while crawling", e);
		} finally {
			isRunnable.release();
		}

		long endTime = System.currentTimeMillis();
		logger.info(String.format("Done crawling with %s; Duration: %s ms", HollisterCrawler.class.getName(),
				endTime - startTime));
	}

	private void getProducts() throws IOException {
		UrlInfoList urlInfoList = getGenderUrls();

		String url;
		String res;
		DocumentContext docCtx;
		for (UrlInfo urlInfo : urlInfoList) {
			url = String.format(hollisterProductAPI, urlInfo.storeId, urlInfo.catalogId, urlInfo.categoryId,
					urlInfo.categoryId);
			try {
				res = restTemplate.getForObject(url, String.class);
				docCtx = JsonPath.parse(res);
				Long numOfProducts = new Long((Integer) docCtx.read("$.categories[0].products.length()"));
				logger.info(String.format("Number of products found for category id %s gender %s: %s",
						urlInfo.categoryId, urlInfo.gender, numOfProducts));
				logger.info(url);
				String productJsonPath = "$.categories[0].products[%s].%s";

				for (Long i = 0L; i < numOfProducts; i++) {
					try {
						Long productDataId = new Long(
								(Integer) docCtx.read(String.format(productJsonPath, i.toString(), "id")));
						String productName = (String) docCtx.read(String.format(productJsonPath, i, "name"));
						String productImageUrl = String.format(imageUrlHol,
								((Integer) docCtx.read(String.format(productJsonPath, i, "productCollection"))).toString());
						String productUrl = brandUrlHol
								+ (String) docCtx.read(String.format(productJsonPath, i, "productUrl"));
						String genderName = urlInfo.gender;
						Float offerPrice = Float.parseFloat(StringUtils
								.strip((String) docCtx.read(String.format(productJsonPath, i, "price.priceLow")), "$"));
						Float listPrice = Float.parseFloat(StringUtils
								.strip((String) docCtx.read(String.format(productJsonPath, i, "price.lowListPrice")), "$"));
						Boolean showSalePrice = (Boolean) docCtx
								.read(String.format(productJsonPath, i, "price.showSalePrice"));
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
						snapshotDetailTo.setBrandName(brandNameHol.toLowerCase());
						
						allProducts.getSnapshotDetail().put(productDataId, snapshotDetailTo);
						
					} catch (Exception e) {
						logger.info("Error getting product info.");
						logger.error("Error getting product", e);
					} 
				}

			} catch (Exception e) {
				logger.info(String.format("Error getting product on page %s", url));
				logger.error("Error getting product", e);
			}
		}
	}

	private UrlInfoList getGenderUrls() throws IOException {
		UrlInfoList urlInfoList = new UrlInfoList();
		UrlInfo urlInfo;
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

		return urlInfoList;
	}

	private String getCategoryName(String productName) {
		String[] names = productName.split("[^a-zA-Z-]");
		String categoryName = names[names.length - 1];

		return categoryName == null || categoryName.length() == 0 ? "other" : categoryName;
	}

	class UrlInfo {
		String gender;
		Long categoryId;
		Long storeId;
		Long catalogId;
	}

	class UrlInfoList extends ArrayList<UrlInfo> {
		private static final long serialVersionUID = 8327149788787399644L;
	}
}
