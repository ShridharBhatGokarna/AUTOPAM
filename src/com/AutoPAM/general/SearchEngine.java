package com.AutoPAM.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to search for matching pattern in the file specified.
 *
 * 
 */
public class SearchEngine {

	
	/**
	 * This method loads the file and returns as buffer which will be used to validate the presence of pattern
	 * @param filename - Name of the file which has to be loaded
	 * @return - Returns character buffer from the file loaded
	 * @throws IOException
	 */
	public CharSequence fromFile(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		FileChannel fc = fis.getChannel();
		// Create a read-only CharBuffer on the file
		ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc
				.size());
		CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
		return cbuf;
	}

	/**
	 * This method validates if the string pattern exists in the specified file
	 * @param file_name - File in which the pattern should be validated
	 * @param string_pattern - Pattern which should be searched in the file
	 * @return - Returns true if pattern is found or false if the pattern is not found
	 */
	public boolean isStringExists(String file_name, String string_pattern) {
		if ((file_name == null) || (string_pattern == null)) {
			System.out.println("Invalid input ");
			return false;
		}
		try {
			// Check whether file exists or not
			File cfile = new File(file_name);
			if (!cfile.exists()) {
				//System.out.println("SearchEngine :: isStringExists :: File does not exist "	+ file_name);
				return false;
			}else{
				System.out.println("[INFO] SearchEngine :: isStringExists :: File  exist "	+ file_name);
			}
			Pattern pattern = Pattern.compile(string_pattern);
			Matcher matcher = pattern.matcher(fromFile(file_name));
			// Find all matches
			while (matcher.find()) {
				// Get the matching string
				String match = matcher.group();
				// We can comment this s o p- this is just for testing
			//	System.out
					//	.println("SearchEngine :: isStringExists :: Matching String "
							//	+ matcher);
				return true;
			}

		} catch (IOException exp) {
			System.out
					.println("SearchEngine :: isStringExists :: Exception in string search "
							+ exp);
			return false;
		}
		return false;
	}

}