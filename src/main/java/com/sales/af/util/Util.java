package com.sales.af.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	private final static String USERAGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36";
	private final static Pattern pricePattern = Pattern.compile("([0-9\\.]+)");
	private final static int timeout = 15000;
	private static HashMap<String, HashMap<String, String>> host2cookies = new HashMap<String, HashMap<String, String>>();

	public static Connection getConnWithUserAgent(String url) {
		int maxRetry = 3;
		Connection conn = null;

		for (int i = 1; i <= maxRetry; i++) {
			try {
				conn = Jsoup.connect(url).userAgent(USERAGENT).timeout(timeout);
				break;
			} catch (Exception e) {
			}
		}

		return conn;
	}
	
	public static Document getDocumentWithUserAgent(String urlString) {
		int maxRetry = 3;
		Connection conn = null;
		Map<String, String> cookies = new HashMap<String, String>();
		Document doc = null;
		
		for (int i = 1; i <= maxRetry; i++) {
			try {
				URL url = new URL(urlString);
				conn = Jsoup.connect(url.toString()).userAgent(USERAGENT).timeout(timeout);
				loadCookiesByHost(url, conn);
				
				doc = conn.get();
				url = conn.request().url();
				
				storeCookiesByHost(url, conn);
				
				break;
			} catch (Exception e) {
				
			}
		}

		return doc;
	}
	
	private static void loadCookiesByHost(URL url, Connection con) {
	    try {
	        String host = url.getHost();
	        if (host2cookies.containsKey(host)) {
	            HashMap<String, String> cookies = host2cookies.get(host);
	            for (Entry<String, String> cookie : cookies.entrySet()) {
	                con.cookie(cookie.getKey(), cookie.getValue());
	            }
	        }
	    } catch (Throwable t) {
	    }
	}

	private static void storeCookiesByHost(URL url, Connection con) {
	        try {
	            String host = url.getHost();
	            HashMap<String, String> cookies = host2cookies.get(host);
	            if (cookies == null) {
	                cookies = new HashMap<String, String>();
	                host2cookies.put(host, cookies);
	            }
	            cookies.putAll(con.response().cookies());
	        } catch (Throwable t) {
	        }    
	}   

	public static float getPrice(String price) {
		Matcher m = pricePattern.matcher(price.trim().replace("$", ""));
		m.find();
		return Float.parseFloat(m.group(0));
	}
	
	public static String escapeForSearch(String keyword) {
		return keyword.replace("*", "\\*")
				.replace(":", "\\:")
				.replace("&", "\\&")
				.replaceAll("\\s+", " ")
				.trim();
	}
	
	public static String parsedSearchKeyword(String keyword) {
		String[] words = escapeForSearch(keyword).split(" ");
		for(int i = 0; i < words.length; i++) {
			words[i] = words[i] + ":*";
		}
		return StringUtils.join(words, " & ");
	}
}
