package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {
	public static Logger _logger = new Logger("fileOperations");


	public static java.util.Map<String, Boolean> getFilesInDirectory(String path, boolean includeDirectories) {
		File f = loadInputFile(path);
		java.util.Map<String, Boolean> result = new java.util.LinkedHashMap<>();
		if (f.exists() && f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File file : files) {
					if (includeDirectories || file.isFile()) {
						result.put(file.getName(), file.isFile());
					}
				}
				return result;
			} else {
				_logger.error("Error listing files in directory: " + path);
				return result;
			}
		}
		return result;
	}

	public static File loadInputFile(String path) {
		File inputFile = new File(path);
		if (!inputFile.isAbsolute()) {
			_logger.debug("Path is not absolute, converting to absolute path.");
			inputFile = inputFile.getAbsoluteFile();
			_logger.debug("Absolute path: " + inputFile.getPath());
			path = inputFile.getPath();
		}
		return inputFile;
	}

	public static List<String> readFileToList(String path) {
		_logger.debug("Reading file: "+path);
		path = loadInputFile(path).getPath();
		if (!doesExists(path, false)) {
			_logger.error("File does not exist: " + path);
			return null;
		}
		List<String> content = new ArrayList<>();
		try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				content.add(sCurrentLine);
			}
		} catch (IOException e) {
			_logger.error("Error reading file: " + e.getMessage());
			return null;
		}
		_logger.debug("File content: "+ content);
		return content;
	}

	public static String readFile(String path) {
		_logger.debug("Reading file: " + path);
		path = loadInputFile(path).getPath();
		if (!doesExists(path, false)) {
			_logger.error("File does not exist: " + path);
			return null;
		}
		StringBuilder contentBuilder = new StringBuilder();
		try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				contentBuilder.append(sCurrentLine).append("\n");
			}
		} catch (IOException e) {
			_logger.error("Error reading file: " + e.getMessage());
			return null;
		}
		_logger.debug("File content: "+ contentBuilder);
		return contentBuilder.toString();
	}

	public static boolean doesExists(String path, boolean isDirectory) {
		_logger.debug("Checking existence of " + (isDirectory ? "directory" : "file") + ": " + path);
		File inputFile = loadInputFile(path);
		path = inputFile.getPath();
		if (!inputFile.exists()) {
			_logger.value((isDirectory ? "directory" : "file") + " does not exist: " + path);
			return false;
		}
		if (inputFile.isDirectory()) {
			if (isDirectory) {
				_logger.value("Directory exists: " + path);
				return true;
			} else {
				_logger.value("Expected a file but found a directory: " + path);
				return false;
			}

		}
		_logger.value("File exists: " + path);
		return true;
	}

	public static boolean writeFile(String path, String content) {
		_logger.debug("Writing file: " + path);
		try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(path))) {
			bw.write(content);
			_logger.debug("File written successfully: " + path);
			return true;
		} catch (IOException e) {
			_logger.error("Error writing file: " + e.getMessage());
			return false;
		}
	}

	public static void clearFile(String filepath) {
		_logger.debug("Writing file: " + filepath);
		try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(filepath))) {
			bw.flush();
	} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static boolean writeStringToFile(String filepath, String line) {
		_logger.debug("Writing file: " + filepath);
		try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(filepath))) {
			bw.append(line);
			_logger.debug("File written successfully: " + filepath);
			return true;
		} catch (IOException e) {
			_logger.error("Error writing file: " + e.getMessage());
			return false;
		}
	}
}
