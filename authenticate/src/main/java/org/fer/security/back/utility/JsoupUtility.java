package org.fer.security.back.utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class JsoupUtility {

	public static String encoding = "utf-8";
	
	public String getTextOnly(String s) {
		if (null != s) {
			Document doc = Jsoup.parse(s, encoding);
			return doc.text();
		}
		else return null;
	} 
	
	public String getBasicFormatedText(String s) {
		Whitelist wl = Whitelist.basic();
		if (null != s) {
			return Jsoup.clean(s, wl);
		}
		else return null;
	} 
	
	public String getBasicWithImages(String s) {
		Whitelist wl = Whitelist.basicWithImages();
		if (null != s) {
			return Jsoup.clean(s, wl);
		}
		else return null;
	} 
}
