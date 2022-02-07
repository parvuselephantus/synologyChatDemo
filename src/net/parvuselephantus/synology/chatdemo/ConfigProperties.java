package net.parvuselephantus.synology.chatdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Quick and dirty way to load config/application.properties.
 * @author Krzysztof Mroczek
 */
public class ConfigProperties {
	
	private String chatURL;
	private String chatToken;
	
	public String getChatToken() {
		return chatToken;
	}
	
	public String getChatURL() {
		return chatURL;
	}
	
	public String load() throws IOException {
		InputStream inputStream = null;
		String result = "";
		try {
			Properties prop = new Properties();
			String propFileName = "application.properties";
			
			if (new File("config" + File.separator + propFileName).exists()) {
				try {
					inputStream = new FileInputStream(new File("config" + File.separator + propFileName));
				} catch (FileNotFoundException e1) {
				}
			} else {
				inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			}
 
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 		
			chatURL = prop.getProperty("chatdemo.chat.url");
			chatToken = prop.getProperty("chatdemo.chat.token");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return result;
	}
}
