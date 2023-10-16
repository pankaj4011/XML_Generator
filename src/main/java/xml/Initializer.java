package xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Initializer {
	Properties prop = new Properties();
	InputStream input = null;

	public void initialize() {
		try {
			//ClassLoader loader = getClass().getClassLoader();
			//input=loader.getResourceAsStream("config.properties");
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			if (input != null) {
				prop.load(input);
				System.out.println("file read successful");
			} else {
				System.out.println("file not found");
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private static class Initializer_InnerClass {

		private final static Initializer INSTANCE = new Initializer();
		static {
			INSTANCE.initialize();
		}
	}
	public static Initializer getInstance() {
		return Initializer_InnerClass.INSTANCE;
	}
	
	public String getProperty(String propertyName) {
		return prop.getProperty(propertyName);
	}
}
