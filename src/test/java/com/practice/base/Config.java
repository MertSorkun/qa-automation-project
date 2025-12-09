package com.practice.base;

import java.io.InputStream;
import java.util.Properties;

/**
 * Config class is responsible for reading key/value pairs from
 * config.properties (like baseUrl, browser, timeout).
 *
 * This allows us to change values in ONE file instead of hard-coding them in
 * the code.
 */
public class Config {

	// This object stores everything from config.properties
	private static final Properties props = new Properties();

	static {
		try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config/config.properties")) {

			if (in != null) {
				// Load key/value pairs
				props.load(in);
			} else {
				throw new RuntimeException("config.properties not found");
			}

		} catch (Exception e) {
			throw new RuntimeException("Failed to load config.properties", e);
		}
	}

	/**
	 * Call Config.get("baseUrl") to retrieve values.
	 */
	public static String get(String key) {
		return props.getProperty(key);
	}
}
