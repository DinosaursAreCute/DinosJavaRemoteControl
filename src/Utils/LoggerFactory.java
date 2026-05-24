package Utils;

public class LoggerFactory {
	public static ConfigParser configParser = new ConfigParser("AppData/config/project.properties", true);


	public static Logger getLogger(Class<?> clazz) {
		ConfigParser configParser = LoggerFactory.configParser;
		Logger logger = new Logger(clazz.getName());
		logger.setLogLevel(configParser.parseInt("logger.level", 1));
		logger.setConsoleLoggingEnabled(configParser.parseBoolean("logger.logToConsole", true));
		logger.setFileLoggingEnabled(configParser.parseBoolean("logger.logToFile", false));
		return logger;
	}

	public static Logger getLogger(String name) {
		ConfigParser configParser = LoggerFactory.configParser;
		Logger logger = new Logger(name);
		logger.setLogLevel(configParser.parseInt("logger.level", 1));
		logger.setConsoleLoggingEnabled(configParser.parseBoolean("logger.logToConsole", true));
		logger.setFileLoggingEnabled(configParser.parseBoolean("logger.logToFile", false));
		return logger;
	}
}
