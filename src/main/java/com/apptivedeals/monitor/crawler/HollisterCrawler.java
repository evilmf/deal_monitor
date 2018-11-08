package com.apptivedeals.monitor.crawler;

import java.net.HttpCookie;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.apptivedeals.monitor.to.AfProducts;
import com.apptivedeals.monitor.to.Snapshot.SnapshotDetail;

@Service
public class HollisterCrawler implements Crawler {
	private static Logger LOGGER = LoggerFactory.getLogger(AfCrawler.class);

	private static String HOLLISTER_PRODUCT_API_TPL = "https://www.hollisterco.com/webapp/wcs/stores/servlet/AjaxNavAPIResponseJSON?catalogId=10201&categoryId=%s&langId=-1&requestType=category&rows=%s&sort=bestmatch&start=%s&storeId=10251";
	private static Integer NUM_OF_ROW = 90;
	private static final String IMAGE_URL = "https://anf.scene7.com/is/image/anf/hol_%s_01_prod1";
	private static final String BRAND = "hollister";
	private static final String HOST = "https://www.hollisterco.com";
	private Map<String, HttpCookie> cookieStore = new HashMap<String, HttpCookie>();
	private static Map<Long, String> SALES_CATEGORY_ID_MAP = new HashMap<Long, String>() {
		private static final long serialVersionUID = -3545380775372128159L;
		{
			put(12634L, "guys");
			put(12635L, "girls");
		}
	};
	
	@Value("${minDiscount}")
	private Float minDiscount;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Set<SnapshotDetail> getProducts() {
		LOGGER.info("Running Hollister scanner...");
		Map<Long, SnapshotDetail> productMap = new HashMap<Long, SnapshotDetail>();
		
		for (Long id : SALES_CATEGORY_ID_MAP.keySet()) {
			String gender = SALES_CATEGORY_ID_MAP.get(id);
			int startNum = 0;
			while (true) {
				AfProducts products = postForProducts(id, startNum, NUM_OF_ROW);
				
				if (products == null) {
					LOGGER.info("Null response from Hollister product API; startNum: {}; id: {}; num_of_row: {}.", startNum, id, NUM_OF_ROW);
					break;
				}
				
				for (AfProducts.Product p : products.products) {
					if(((p.lowListPrice - p.lowPrice) / p.lowListPrice) <= minDiscount) {
						continue;
					}
					
					if(p.isSoldOut.equals(true)) {
						continue;
					}
					
					SnapshotDetail snapshotDetail = new SnapshotDetail();
					snapshotDetail.setProductName(p.name);
					snapshotDetail.setImages(Arrays.asList(String.format(IMAGE_URL, p.collection)));
					snapshotDetail.setPriceRegular(p.lowListPrice);
					snapshotDetail.setPriceDiscount(p.lowPrice);
					snapshotDetail.setCategoryName(getCategoryName(p.name));
					snapshotDetail.setGenderName(gender);
					snapshotDetail.setProductUrl(HOST + p.productUrl);
					snapshotDetail.setProductDataId(p.productId.toString());
					snapshotDetail.setBrandName(BRAND);
					
					productMap.put(p.productId, snapshotDetail);
				}
				
				if (products.products.size() == 0) {
					break;
				}
				
				startNum += NUM_OF_ROW;
			}
		}

		return new HashSet<SnapshotDetail>(productMap.values());
	}

	private AfProducts postForProducts(long categoryId, int startNum, int numOfRow) {
		LOGGER.info("Posting for products with categoryId {}, startNum {}, numOfRow {}.", categoryId, startNum, numOfRow);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
		headers.add("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		headers.add("x-requested-with", "XMLHttpRequest");
		headers.add("cookie", getCookieString());
		headers.add("Accept-Encoding", "application/gzip");
		

		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<AfProducts> res = restTemplate.exchange(String.format(HOLLISTER_PRODUCT_API_TPL, categoryId, numOfRow, startNum),
				HttpMethod.GET, entity, AfProducts.class);
		
		storeCookies(res.getHeaders().get(HttpHeaders.SET_COOKIE));
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return res.getBody();
	}
	
	private String getCategoryName(String productName) {
		String[] names = productName.split("[^a-zA-Z-]");
		String categoryName = names[names.length - 1];

		return categoryName == null || categoryName.length() == 0 ? "other" : categoryName;
	}
	
	private String getCookieString() {
		Iterator<Map.Entry<String, HttpCookie>> it = cookieStore.entrySet().iterator();
		String cookieString = "";
		while (it.hasNext()) {
			Map.Entry<String, HttpCookie> cookie = it.next();
			if(cookie.getValue().hasExpired()) {
				it.remove();
				continue;
			}
			
			cookieString = cookieString + cookie.getValue() + "; ";
		}
				
		return cookieString;
	}
	
	private void storeCookies(List<String> cookies) {
		if (cookies == null) {
			return;
		}
		
		for (String cookie : cookies) {
			List<HttpCookie> httpCookies = HttpCookie.parse(cookie);
			for(HttpCookie httpCookie : httpCookies) {
				cookieStore.put(httpCookie.getName(), httpCookie);
			}
		}
	}
}
