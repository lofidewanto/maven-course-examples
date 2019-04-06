package com.sonatype.maven.weather.yahoo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class YahooRetriever {

	private static Logger log = Logger.getLogger(YahooRetriever.class);

	public InputStream retrieve(String zipcode) throws Exception {
		log.info("Retrieving Weather Data");
		InputStream inputStream = getFromFile(zipcode);
		return inputStream;
	}

	@SuppressWarnings("unused")
	private InputStream getFromInternet(String zipcode) throws IOException, MalformedURLException {
		String url = "http://weather.yahooapis.com/forecastrss?p=" + zipcode;
		URLConnection conn = new URL(url).openConnection();
		return conn.getInputStream();
	}
	
	private InputStream getFromFile(String zipcode) throws IOException, MalformedURLException {
		InputStream nyData = getClass().getClassLoader().getResourceAsStream("ny-weather.xml");
		return nyData;
	}

}
