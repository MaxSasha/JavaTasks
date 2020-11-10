package com.maxsasha.javatasks.api.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesUtils {
	public static Properties getProperties(String propFileName) throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(propFileName);
		
		if (inputStream == null) {
			throw new FileNotFoundException(String.format("Property file {} not found", propFileName));
		}
		properties.load(inputStream);
		return properties;
	}
}