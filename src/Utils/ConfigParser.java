package Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class ConfigParser {
	private static final Logger _logger = new Logger(ConfigParser.class.getName());
	private final String configFilePath;
	private final Properties properties;

	public ConfigParser(String configFilePath, boolean silent) {
		_logger.setConsoleLoggingEnabled(silent);
		FileOperations._logger.setConsoleLoggingEnabled(false);
		try {
			_logger.debug("Initializing ConfigParser with config file path: " + configFilePath);
			this.configFilePath = configFilePath;
			this.properties = new Properties();

			if (!FileOperations.doesExists(configFilePath, false)) {
				_logger.fatal("Config file does not exist: " + configFilePath);
				throw new RuntimeException("Config file does not exist: " + configFilePath);
			}
			if (!configFilePath.endsWith(".properties")) {
				_logger.fatal("Config file must have .properties extension: " + configFilePath);
				throw new IllegalArgumentException("Config file must have .properties extension: " + configFilePath);
			}

			loadConfig();
		} finally {
			FileOperations._logger.setConsoleLoggingEnabled(true);
		}
	}


	private void loadConfig() {
		try (FileInputStream fis = new FileInputStream(configFilePath)) {
			properties.load(fis);
			_logger.debug("Config file loaded successfully: " + configFilePath);
			_logger.debug("Config properties: " + properties.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String key) {
		if (!properties.containsKey(key)) {
			_logger.warning("Property not found: " + key);
			return null;
		}
		return properties.getProperty(key);
	}

	public boolean parseBoolean(String key, boolean defaultValue) {
		if (!properties.containsKey(key)) {
			_logger.warning("Property not found: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
		String value = getProperty(key);
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(value);
		} else {
			_logger.warning("Invalid boolean value for key: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
	}

	public int parseInt(String key, int defaultValue) {
		if (!properties.containsKey(key)) {
			_logger.warning("Property not found: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
		String value = getProperty(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			_logger.warning("Found invalid integer value: '" + value + "' for key: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
	}

	public long parseLong(String key, long defaultValue) {
		if (!properties.containsKey(key)) {
			_logger.warning("Property not found: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
		String value = getProperty(key);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			_logger.warning("Found invalid long value: '" + value + "' for key: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
	}

	public String getProperty(String key, String defaultValue) {
		if (!properties.containsKey(key)) {
			_logger.warning("Property not found: " + key + ", returning default value: " + defaultValue);
			return defaultValue;
		}
		return properties.getProperty(key);
	}

	public Properties getProperties() {
		return properties;
	}

	public void saveConfig(String comments) {
		try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
			properties.store(fos, comments);
			_logger.debug("Config saved successfully: " + configFilePath);
		} catch (IOException e) {
			_logger.fatal("Failed to save config: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public void removeProperty(String key) {
		properties.remove(key);
	}

	/**
	 * Get all configuration properties as a Map
	 * @return Map of all key-value pairs from the configuration file
	 */
	public Map<String, String> getAllProperties() {
		Map<String, String> result = new java.util.HashMap<>();
		for (String key : properties.stringPropertyNames()) {
			result.put(key, properties.getProperty(key));
		}
		return result;
	}

}