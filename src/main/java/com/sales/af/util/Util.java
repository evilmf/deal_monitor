package com.sales.af.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	private final static String USERAGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36";
	private final static Pattern pricePattern = Pattern.compile("([0-9\\.]+)");
	private final static int timeout = 15000;

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
