package Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic CSV Handler for reading and writing CSV files.
 * Provides consistent CSV operations across all factories.
 */
public class CSVHandler {
    private static final Logger logger = new Logger("CSVHandler");
    private final String delimiter;
    private final String filepath;

    /**
     * Creates a CSVHandler with comma as delimiter.
     * @param filepath the path to the CSV file
     */
    public CSVHandler(String filepath) {
        this(filepath, ",");
    }

    /**
     * Creates a CSVHandler with custom delimiter.
     * @param filepath the path to the CSV file
     * @param delimiter the delimiter to use (e.g., "," or ";")
     */
    public CSVHandler(String filepath, String delimiter) {
        this.filepath = filepath;
        this.delimiter = delimiter;
        logger.debug("CSVHandler initialized for file: " + filepath + " with delimiter: '" + delimiter + "'");
    }

    /**
     * Parses a single CSV line into fields.
     * @param line the CSV line to parse
     * @return array of fields
     */
    public String[] parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            logger.warning("Attempted to parse null or empty line");
            return new String[0];
        }
        logger.debug("Parsing line: " + line);
        String[] fields = line.split(delimiter);
        // Trim whitespace from each field
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }

    /**
     * Formats an array of fields into a CSV line.
     * @param fields the fields to format
     * @return formatted CSV line
     */
    public String formatLine(String[] fields) {
        if (fields == null || fields.length == 0) {
            logger.warning("Attempted to format null or empty fields");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            sb.append(fields[i]);
            if (i < fields.length - 1) {
                sb.append(delimiter);
            }
        }
        String line = sb.toString();
        logger.debug("Formatted line: " + line);
        return line;
    }

    /**
     * Reads all lines from the CSV file and parses them.
     * @return list of parsed lines (each line is a String array)
     */
    public List<String[]> readAll() {
        logger.debug("Reading all lines from: " + filepath);
        List<String> lines = FileOperations.readFileToList(filepath);
        List<String[]> parsedData = new ArrayList<>();

        if (lines != null) {
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    parsedData.add(parseLine(line));
                }
            }
            logger.info("Read " + parsedData.size() + " lines from " + filepath);
        } else {
            logger.warning("No data read from file: " + filepath);
        }

        return parsedData;
    }

    /**
     * Reads all lines excluding the header.
     * @return list of parsed data lines (without header)
     */
    public List<String[]> readAllWithoutHeader() {
        logger.debug("Reading all lines without header from: " + filepath);
        List<String[]> allData = readAll();
        if (!allData.isEmpty()) {
            allData.removeFirst(); // Remove header
            logger.debug("Removed header, " + allData.size() + " data lines remaining");
        }
        return allData;
    }

    /**
     * Writes all data to the CSV file (overwrites existing content).
     * @param data list of data rows (each row is a String array)
     */
    public void writeAll(List<String[]> data) {
        logger.debug("Writing " + data.size() + " lines to: " + filepath);

        // Clear and recreate file
        FileOperations.clearFile(filepath);

        for (String[] fields : data) {
            String line = formatLine(fields);
            FileOperations.writeStringToFile(filepath, line);
        }

        logger.info("Successfully wrote " + data.size() + " lines to " + filepath);
    }

    /**
     * Appends a single line to the CSV file.
     * @param fields the fields to append
     */
    public void appendLine(String[] fields) {
        String line = formatLine(fields);
        logger.debug("Appending line to " + filepath + ": " + line);
        FileOperations.writeStringToFile(filepath, line);
    }

    /**
     * Initializes the CSV file with a header row.
     * Creates the file if it doesn't exist, or clears it if it does.
     * @param headerFields the header fields
     */
    public void initializeWithHeader(String[] headerFields) {
        logger.info("Initializing file " + filepath + " with header");
        FileOperations.clearFile(filepath);
        String header = formatLine(headerFields);
        FileOperations.writeStringToFile(filepath, header);
        logger.info("File initialized with header: " + header);
    }

    public List<String> getAllLinesAsStrings(String filepath) {
        logger.debug("Getting all lines as strings from: " + filepath);
        return Collections.singletonList(FileOperations.readFile(filepath));
    }





    /**
     * Gets the filepath associated with this handler.
     * @return the filepath
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Gets the delimiter used by this handler.
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Updates a specific data line (excluding header) in the CSV file.
     * dataIndex is zero-based and refers to data rows (header is not counted).
     *
     * Example: dataIndex = 0 updates the first data row (line 2 in file if header exists).
     *
     * @param dataIndex index of the data row to update (0-based, excluding header)
     * @param newFields new fields to write at that row
     */
    public void updateDataLine(int dataIndex, String[] newFields) {
        logger.info("Updating data line (dataIndex=" + dataIndex + ") in file: " + filepath);

        // Read all lines (includes header as first element)
        List<String[]> allLines = readAll();
        if (allLines == null || allLines.isEmpty()) {
            logger.error("CSV file appears to be empty or missing header: " + filepath);
            throw new IllegalStateException("CSV file is empty or missing header: " + filepath);
        }

        int targetIndex = dataIndex + 1; // shift because readAll() includes header at index 0
        if (targetIndex < 1 || targetIndex >= allLines.size()) {
            logger.error("Requested dataIndex out of range. Available data rows: " + (allLines.size() - 1));
            throw new IndexOutOfBoundsException("Data index out of range: " + dataIndex);
        }

        // Replace the row and write all back
        allLines.set(targetIndex, newFields);
        writeAll(allLines);
        logger.info("Successfully updated data line " + dataIndex + " in file: " + filepath);
    }
}
