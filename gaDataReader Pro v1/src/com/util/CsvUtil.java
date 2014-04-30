package com.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Useful CSV utilities.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2006/06/parse-csv-upload.html
 */
public class CsvUtil {

	// Init
	// ---------------------------------------------------------------------------------------

	// Defaults.
	private static final char DEFAULT_CSV_SEPARATOR = ',';
	private static final String DEFAULT_LINE_SEPARATOR = "\r\n"; // CRLF.

	private CsvUtil() {
		// Utility class, hide the constructor.
	}

	// Parsers
	// ------------------------------------------------------------------------------------

	/**
	 * CSV content parser. Convert an InputStream with the CSV contents to a
	 * two-dimensional List of Strings representing the rows and columns of the
	 * CSV. Each CSV record is expected to be separated by the default CSV field
	 * separator, a comma.
	 * 
	 * @param csvInput
	 *            The InputStream with the CSV contents.
	 * @return A two-dimensional List of Strings representing the rows and
	 *         columns of the CSV.
	 */
	public static List<List<String>> parseCsv(InputStream csvInput,
			Charset charset) {
		return parseCsv(csvInput, DEFAULT_CSV_SEPARATOR, charset);
	}

	/**
	 * CSV content parser. Convert an InputStream with the CSV contents to a
	 * two-dimensional List of Strings representing the rows and columns of the
	 * CSV. Each CSV record is expected to be separated by the specified CSV
	 * field separator.
	 * 
	 * @param csvInput
	 *            The InputStream with the CSV contents.
	 * @param csvSeparator
	 *            The CSV field separator to be used.
	 * @return A two-dimensional List of Strings representing the rows and
	 *         columns of the CSV.
	 */
	public static List<List<String>> parseCsv(InputStream csvInput,
			char csvSeparator, Charset charset) {

		// Prepare.
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(
				csvInput, charset));
		List<List<String>> csvList = new ArrayList<List<String>>();
		String csvRecord;

		// Process records.
		try {
			while ((csvRecord = csvReader.readLine()) != null) {
				csvList.add(parseCsvRecord(csvRecord, csvSeparator));
			}
		} catch (IOException e) {
			// This exception should never occur however as this should already
			// be covered by the
			// source which feeds the InputStream to this method.
			throw new RuntimeException("Reading CSV failed.", e);
		}

		return csvList;
	}

	/**
	 * CSV record parser. Convert a CSV record to a List of Strings representing
	 * the fields of each CSV record. The CSV record is expected to be separated
	 * by the specified CSV field separator.
	 * 
	 * @param record
	 *            The CSV record.
	 * @param csvSeparator
	 *            The CSV field separator to be used.
	 * @return A List of Strings representing the fields of each CSV record.
	 */
	public static List<String> parseCsvRecord(String record, char csvSeparator) {

		// Prepare.
		boolean quoted = false;
		StringBuilder csvBuilder = new StringBuilder();
		List<String> fields = new ArrayList<String>();

		// Process fields.
		for (int i = 0; i < record.length(); i++) {
			char c = record.charAt(i);
			csvBuilder.append(c);

			if (c == '"') {
				quoted = !quoted; // Detect nested quotes.
			}

			if ((!quoted && c == csvSeparator) // The separator.
					|| i + 1 == record.length()) // End of record.
			{
				String field = csvBuilder.toString(); // Obtain the field.
				field = field.replaceAll(csvSeparator + "$", ""); // Trim ending
				// semicolon
				field = field.replaceAll("^\"|\"$", ""); // Trim surrounding
				// quotes.
				field = field.replaceAll("\"\"", "\\\""); // Re-escape quotes.
				fields.add(field.trim()); // Add field to List.
				csvBuilder = new StringBuilder(); // Reset.
			}
		}

		return fields;
	}

	// Formatters
	// --------------------------------------------------------------------------------

	/**
	 * CSV content formatter. Convert a two-dimensional List of Objects to a CSV
	 * in an InputStream. Each CSV record will be separated by the default CSV
	 * field separator, a comma.
	 * 
	 * @param csvList
	 *            A two-dimensional List of Objects representing the rows and
	 *            columns of the CSV.
	 * @param charset
	 * @return The InputStream containing the CSV contents (actually a
	 *         ByteArrayInputStream).
	 */
	public static String formatCsv(
			ArrayList<ArrayList<?>> csvList, Charset charset) {
		return formatCsv(csvList, DEFAULT_CSV_SEPARATOR, charset);
	}

	/**
	 * CSV content formatter. Convert a two-dimensional List of Objects to a CSV
	 * in an InputStream. Each CSV record will be separated by the specified CSV
	 * field separator.
	 * 
	 * @param csvList
	 *            A two-dimensional List of Objects representing the rows and
	 *            columns of the CSV.
	 * @param csvSeparator
	 *            The CSV field separator to be used.
	 * @return The InputStream containing the CSV contents (actually a
	 *         ByteArrayInputStream).
	 */
	public static String formatCsv(
			ArrayList<ArrayList<?>> csvList, char csvSeparator, Charset charset) {

		// Prepare.
		StringBuilder csvContent = new StringBuilder();

		// Process records.
		for (ArrayList<?> csvRecord : csvList) {
			if (csvRecord != null) {
				csvContent.append(formatCsvRecord(csvRecord, csvSeparator));
			}

			// Add default line separator.
			csvContent.append(DEFAULT_LINE_SEPARATOR);
		}

		return csvContent.toString();
	}
	
	public static String formatCsvfromArray(
			ArrayList csvList, char csvSeparator, Charset charset) {
		// Prepare.
				StringBuilder csvContent = new StringBuilder();

				// Process records.
				for (Object csvRecord : csvList) {
					if (csvRecord != null) {
						
						csvContent.append(formatCsvRecord((ArrayList)csvRecord, csvSeparator));
					}

					// Add default line separator.
					csvContent.append(DEFAULT_LINE_SEPARATOR);
	}
				return csvContent.toString();	
	}


	/**
	 * CSV record formatter. Convert a List of Objects representing the fields
	 * of a CSV record to a String representing each CSV record. The CSV record
	 * will be separated by the specified CSV field separator.
	 * 
	 * @param csvRecord
	 *            A List of Objects representing the fields of a CSV reecord.
	 * @param csvSeparator
	 *            The CSV field separator to be used.
	 * @return A String representing a CSV record.
	 */
	static <T extends Object> String formatCsvRecord(ArrayList<?> csvRecord,
			char csvSeparator) {

		// Prepare.
		StringBuilder fields = new StringBuilder();
		String separator = String.valueOf(csvSeparator);

		// Process fields.
		for (Iterator<?> iter = csvRecord.iterator(); iter.hasNext();) {
			Object object = iter.next();

			if (object != null) {
				String field = object.toString();

				if (field.contains("\"")) {
					field = field.replaceAll("\"", "\"\""); // Escape quotes.
				}

				if (field.contains(separator) || field.contains("\"")) {
					field = "\"" + field + "\""; // Surround with quotes.
				}

				fields.append(field);
			}

			if (iter.hasNext()) {
				fields.append(separator); // Add field separator.
			}
		}

		return fields.toString();
	}
	
	

}