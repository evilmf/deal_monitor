package com.sales.af.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.sales.af.bo.Category;
import com.sales.af.bo.Gender;
import com.sales.af.bo.Image;
import com.sales.af.bo.Product;
import com.sales.af.bo.SnapshotDetail;
import com.sales.af.to.ProductsTo;
import com.sales.af.util.Util;

public class AfCrawler extends ProductQueue {
	private static Logger logger = Logger.getLogger(DataLoader.class);

	private static final int size = 1;
	private static Semaphore isRunnable = new Semaphore(size);
	private static final String brandName = "Abercrombie & Fitch";

	private ProductsTo allProducts;

	@Value("${afHomePage}")
	private String afHomePage;

	public void crawl() throws InterruptedException, IOException {
		long startTime = System.currentTimeMillis();
		logger.info(String.format("Start crawling with %s",
				AfCrawler.class.getName()));

		if (!isRunnable.tryAcquire()) {
			logger.info(String.format("%s is already running. Skip crawling.",
					AfCrawler.class.getName()));
			return;
		}

		allProducts = new ProductsTo();
		allProducts.getBrand().setName(brandName.toLowerCase());

		try {
			getProducts();

			if (!allProducts.getProducts().isEmpty()) {
				logger.info(String
						.format("Enqueuing Brand: %s; Categories Found: %s; Products Found: %s",
								allProducts.getBrand().getName(),
								allProducts.getGetCategoryCount(),
								allProducts.getProductCount()));

				productQueue.add(allProducts);
			} else {
				logger.info(String.format("%s gender found", allProducts
						.getGenders().size()));
				logger.info(String.format("%s category found", allProducts
						.getCategories().size()));
				logger.warn(String.format("No product found for brand %s",
						allProducts.getBrand().getName()));
			}
		} catch (Exception e) {
			logger.error("Got exception while crawling", e);
		} finally {
			isRunnable.release();
		}

		long endTime = System.currentTimeMillis();
		logger.info(String.format("Done crawling with %s; Duration: %s ms",
				AfCrawler.class.getName(), endTime - startTime));
	}

	private void getProducts() throws IOException {
		GenderInfoList genderUrls = new GenderInfoList();
		genderUrls = getGenderUrls();

		CategoryInfoList categoryUrls = new CategoryInfoList();
		categoryUrls = getCategoryUrls(genderUrls);

		for (CategoryInfo ci : categoryUrls) {
			try {
				Document docProduct = Util.getConnWithUserAgent(ci.url).get();
				Elements productElements = docProduct
						.select("ul.category-product-wrap.whiteoutarea li.product-wrap>div");
				// logger.info(String.format("Category %s - %s",
				// ci.category.getName(),
				// ci.url));
				for (Element p : productElements) {
					try {
						String dataProductId = p.attr("data-productid");
						String dataCollectionId = p.attr("data-collection");

						Element prodInfo = p.select(
								"div.product-info>div.name a").first();
						String productName = prodInfo.text();
						String productUrl = prodInfo.absUrl("href");
						String imageUrl = String
								.format("http://anf.scene7.com/is/image/anf/anf_%s_01_prod1?$category-r-anf$",
										dataCollectionId);
						Elements prodPrice = p
								.select("div.product-info>div.price span");
						if (prodPrice.size() < 2) {
							continue;
						}

						float listPrice = Util
								.getPrice(prodPrice.get(0).text());
						float offerPrice = Util.getPrice(prodPrice.get(1)
								.text());

						if (listPrice == 0 || offerPrice == 0) {
							logger.warn(String
									.format("Either list price [%s] or offer price [%s] is zero. Category URL: %s. Product Data ID: %. Product Name: %s.",
											listPrice, offerPrice, ci.url,
											dataProductId, productName));
							continue;
						}

						Product product = new Product();
						List<Image> images = new ArrayList<Image>();
						List<SnapshotDetail> snapshots = new ArrayList<SnapshotDetail>();

						Image image = new Image();
						image.setImageUrl(imageUrl);
						images.add(image);

						SnapshotDetail snapshot = new SnapshotDetail();
						snapshot.setPriceRegular(listPrice);
						snapshot.setPriceDiscount(offerPrice);
						snapshots.add(snapshot);

						product.setName(productName);
						product.setImages(images);
						product.setSnapshotDetail(snapshots);
						product.setCategory(ci.category);
						product.setGender(ci.gender);
						product.setProductUrl(productUrl);
						product.setProductId(dataProductId);
						product.setBrand(allProducts.getBrand());

						allProducts.getProducts().put(dataProductId, product);
					} catch (Exception e) {
						logger.info(String.format(
								"Error getting product on page %s", ci.url));
						logger.error("Error getting product", e);
					}

				}
			} catch (Exception e) {
				logger.error(String.format(
						"Exception while crawling category %s",
						ci.category.getName()));
				logger.error("Exception", e);
			}
		}
	}

	private GenderInfoList getGenderUrls() throws IOException {
		GenderInfoList genderUrls = new GenderInfoList();
		Document doc = Util.getConnWithUserAgent(afHomePage).get();
		Elements genderElements = doc
				.select("a[id*=division][href*=/mens], [id*=division][href*=/womens]");
		for (Element g : genderElements) {
			Gender gender = new Gender();
			String genderName = g.text().toLowerCase();
			gender.setName(genderName);
			allProducts.getGenders().put(genderName, gender);

			GenderInfo genderInfo = new GenderInfo();
			genderInfo.gender = gender;
			genderInfo.url = g.absUrl("href");
			genderUrls.add(genderInfo);
		}

		// logger.info(String.format("%s gender urls found!",
		// genderUrls.size()));

		return genderUrls;
	}

	private CategoryInfoList getCategoryUrls(GenderInfoList genderUrls)
			throws IOException {
		CategoryInfoList categoryUrls = new CategoryInfoList();

		for (GenderInfo genderUrl : genderUrls) {
			Document docCate = Util.getConnWithUserAgent(genderUrl.url).get();
			Elements discounts = docCate
					.select("ul.primary li a[href*=sale], a[href*=clearance], a[href*=secret]");
			for (Element d : discounts) {
				Document docDiscount = Util.getConnWithUserAgent(
						d.absUrl("href")).get();
				Elements categoryElements = docDiscount
						.select("ul.secondary li");
				for (Element e : categoryElements) {
					if (e.select("ul.tertiary").size() > 0) {
						continue;
					}

					Element link = e.select("a").first();
					String categoryName = link.text().toLowerCase().trim();

					Category category = new Category();
					category.setName(categoryName);
					allProducts.getCategories().put(categoryName, category);

					CategoryInfo categoryInfo = new CategoryInfo();
					categoryInfo.gender = genderUrl.gender;
					categoryInfo.url = link.absUrl("href");
					categoryInfo.category = category;
					categoryUrls.add(categoryInfo);
				}
			}
		}

		return categoryUrls;
	}
}

class GenderInfo {
	public String url;
	public Gender gender;
}

class CategoryInfo {
	public String url;
	public Category category;
	public Gender gender;
}

class GenderInfoList extends ArrayList<GenderInfo> {
	private static final long serialVersionUID = 8327499788787399644L;
}

class CategoryInfoList extends ArrayList<CategoryInfo> {
	private static final long serialVersionUID = 4568037474241286411L;
}